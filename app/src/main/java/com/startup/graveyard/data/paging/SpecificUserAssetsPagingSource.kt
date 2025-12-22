package com.startup.graveyard.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.startup.graveyard.data.remote.AssetApi
import com.startup.graveyard.domain.mappers.toDomain
import com.startup.graveyard.domain.models.assets.Asset

class UserAssetPagingSource(
    private val assetApi: AssetApi,
    private val userId: String
) : PagingSource<Int, Asset>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Asset> {
        return try {
            val page = params.key ?: 1
            val limit = params.loadSize

            val response =
                assetApi.getAllAssetsOfSpecificUser(userId, page, limit)

            LoadResult.Page(
                data = response.data.items.map {it.toDomain()  },
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (response.data.items.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Asset>): Int? =
        state.anchorPosition?.let { pos ->
            state.closestPageToPosition(pos)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(pos)?.nextKey?.minus(1)
        }
}