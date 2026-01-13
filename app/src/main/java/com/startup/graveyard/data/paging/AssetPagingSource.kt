package com.startup.graveyard.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.startup.graveyard.data.remote.AssetApi
import com.startup.graveyard.data.remote.AssetFilter
import com.startup.graveyard.domain.mappers.toDomain
import com.startup.graveyard.domain.models.assets.Asset

class AssetsPagingSource(
    private val assetApi: AssetApi,
    private val filter: AssetFilter
) : PagingSource<Int, Asset>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Asset> {
        val page = params.key ?: 1

        return try {
            val response = assetApi.getAllAssets(
                page = page,
                limit = params.loadSize,
                userUuid = filter.userUuid,
                assetType = filter.assetType,
                isSold = filter.isSold
            )

            val items = response.data.items.map { it.toDomain() }

            LoadResult.Page(
                data = items,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (items.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Asset>): Int? {
        return state.anchorPosition?.let { pos ->
            state.closestPageToPosition(pos)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(pos)?.nextKey?.minus(1)
        }
    }
}