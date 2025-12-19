package com.startup.graveyard.data.remote

import com.startup.graveyard.domain.models.CreateAssetRequestModel
import com.startup.graveyard.domain.models.CreateAssetResponseModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AssetApi {

    @POST("/assets")
    suspend fun createNewAsset(
        @Body createAssetRequestModel: CreateAssetRequestModel
    ) : Response<CreateAssetResponseModel>


}