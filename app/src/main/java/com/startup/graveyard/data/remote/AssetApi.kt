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
        @Query("limit") limit: Int,
        @Query("user_uuid") userUuid: String? = null,
        @Query("asset_type") assetType: String? = null,
        @Query("is_sold") isSold: Boolean? = null
    ): GetAllAssetsResponseModel

    @GET("/assets/{id}")
    suspend fun getAssetsSpecificDetails(
        @Path("id") id: String
    ): Response<GetSpecificAssetResponseModel>
}



data class AssetFilter(
    val userUuid: String? = null,
    val assetType: String? = null,
    val isSold: Boolean? = null
)