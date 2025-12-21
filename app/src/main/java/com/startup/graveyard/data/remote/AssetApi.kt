package com.startup.graveyard.data.remote

import com.startup.graveyard.domain.models.CreateAssetRequestModel
import com.startup.graveyard.domain.models.CreateAssetResponseModel
import com.startup.graveyard.domain.models.getallassets.GetAllAssetsResponseModel
import retrofit2.Response
import retrofit2.http.*

interface AssetApi {

    @POST("/assets")
    suspend fun createNewAsset(
        @Body createAssetRequestModel: CreateAssetRequestModel
    ): Response<CreateAssetResponseModel>

    @GET("/assets")
    suspend fun getAllAssets(
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): GetAllAssetsResponseModel
}