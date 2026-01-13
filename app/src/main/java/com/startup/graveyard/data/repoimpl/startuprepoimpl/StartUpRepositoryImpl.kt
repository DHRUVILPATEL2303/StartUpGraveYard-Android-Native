package com.startup.graveyard.data.repoimpl.startuprepoimpl

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.startup.graveyard.data.paging.StartupPagingSource
import com.startup.graveyard.data.remote.StartUpApi
import com.startup.graveyard.domain.models.startups.Startup
import com.startup.graveyard.domain.repo.startuprepo.StartUpRepository
import javax.inject.Inject

class StartUpRepositoryImpl @Inject constructor(
    private val startUpApi : StartUpApi
) : StartUpRepository{
    override fun getStartupsPager(): Pager<Int, Startup> =
        Pager(
            config = PagingConfig(
                pageSize = 10,
                initialLoadSize = 10,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                StartupPagingSource(startUpApi)
            }
        )
}