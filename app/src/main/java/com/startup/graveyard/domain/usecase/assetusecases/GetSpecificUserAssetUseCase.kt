package com.startup.graveyard.domain.usecase.assetusecases

import androidx.paging.PagingData
import com.startup.graveyard.domain.models.assets.Asset
import com.startup.graveyard.domain.repo.assetrepo.AssetRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSpecificUserAssetUseCase @Inject constructor(
    private val assetRepository: AssetRepository
) {

    operator fun invoke(): Flow<PagingData<Asset>> =
        assetRepository.getAllSpecificUserAsset().flow
}