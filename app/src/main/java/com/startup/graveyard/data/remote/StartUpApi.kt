package com.startup.graveyard.data.remote

import com.startup.graveyard.domain.models.startups.GetAllStartUpsResponseModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface StartUpApi {

    @GET("/startups")
    suspend fun getAllStartUps(
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): GetAllStartUpsResponseModel
}