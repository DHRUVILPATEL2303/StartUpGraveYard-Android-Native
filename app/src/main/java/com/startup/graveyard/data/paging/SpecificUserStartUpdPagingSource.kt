package com.startup.graveyard.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.startup.graveyard.data.remote.StartUpApi
import com.startup.graveyard.domain.mappers.toDomain
import com.startup.graveyard.domain.models.startups.Startup
import com.startup.graveyard.domain.models.startups.mappers.toDomain

class SpecificUserStartUpPagingSource(
    private val startUpApi: StartUpApi,
    private val userId: String
) : PagingSource<Int, Startup>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Startup> {
        return try {
            val page = params.key ?: 1
            val limit = params.loadSize

            val response = startUpApi.getAllSpecificPersonStartUps(
                uid = userId,
                page = page,
                limit = limit
            )

            val startups = response.data.items.map { it.toDomain() }

            LoadResult.Page(
                data = startups,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (startups.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Startup>): Int? {
        return state.anchorPosition?.let { position ->
            state.closestPageToPosition(position)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(position)?.nextKey?.minus(1)
        }
    }
}