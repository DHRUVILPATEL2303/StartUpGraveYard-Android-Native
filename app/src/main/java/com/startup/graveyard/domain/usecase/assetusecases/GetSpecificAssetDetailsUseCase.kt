package com.startup.graveyard.domain.usecase.assetusecases

import com.startup.graveyard.domain.repo.assetrepo.AssetRepository
import javax.inject.Inject

class GetSpecificAssetDetailsUseCase @Inject constructor(
    private val assetRepository: AssetRepository
) {

    suspend fun getSpecificAssetDetailsUseCase(
        id : String
    )=assetRepository.getSpecificAssetByID(id)
}