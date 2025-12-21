package com.startup.graveyard.domain.usecase.assetusecases

import com.startup.graveyard.domain.models.CreateAssetRequestModel
import com.startup.graveyard.domain.repo.assetrepo.AssetRepository
import javax.inject.Inject

class CreateAssetUseCase @Inject constructor(
    private val assetRepository: AssetRepository
) {

    suspend fun createAssetUseCase(createAssetRequestModel: CreateAssetRequestModel) =
        assetRepository.createNewAsset(createAssetRequestModel)
}