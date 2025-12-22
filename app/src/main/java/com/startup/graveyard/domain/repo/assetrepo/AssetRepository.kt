package com.startup.graveyard.domain.repo.assetrepo

import androidx.paging.Pager
import androidx.paging.PagingData
import com.startup.graveyard.common.ResultState
import com.startup.graveyard.domain.models.CreateAssetRequestModel
import com.startup.graveyard.domain.models.CreateAssetResponseModel
import com.startup.graveyard.domain.models.assets.Asset
import com.startup.graveyard.domain.models.getallassets.GetAllAssetsResponseModel
import kotlinx.coroutines.flow.Flow

interface AssetRepository {

    suspend fun createNewAsset(
        createAssetRequestModel: CreateAssetRequestModel
    ): Flow<ResultState<CreateAssetResponseModel>>

    fun getAssetsPager(): Pager<Int, Asset>

    fun getAllSpecificUserAsset() :Pager<Int, Asset>


}