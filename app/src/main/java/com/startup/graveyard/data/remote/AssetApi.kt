package com.startup.graveyard.data.remote

import com.startup.graveyard.domain.models.CreateAssetRequestModel
import com.startup.graveyard.domain.models.CreateAssetResponseModel
import com.startup.graveyard.domain.models.assets.GetSpecificAssetResponseModel
import com.startup.graveyard.domain.models.getallassets.GetAllAssetsOfSpecificUserResponseModel
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

    @GET("/users/{uid}/assets")
    suspend fun getAllAssetsOfSpecificUser(
        @Path("uid") uid : String,
        @Query("page") page : Int,
        @Query("limit") limit : Int
    ): GetAllAssetsOfSpecificUserResponseModel

    @GET("assets/{id}")
    suspend fun getAssetsSpecificDetails(
        @Path("id") id : String
    ): Response<GetSpecificAssetResponseModel>
}