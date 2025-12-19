package com.startup.graveyard.domain.repo.assetrepo

import com.startup.graveyard.common.ResultState
import com.startup.graveyard.domain.models.CreateAssetRequestModel
import com.startup.graveyard.domain.models.CreateAssetResponseModel
import kotlinx.coroutines.flow.Flow

interface AssetRepository {

    suspend fun createNewAsset(
        createAssetRequestModel: CreateAssetRequestModel
    ) : Flow<ResultState<CreateAssetResponseModel>>



}