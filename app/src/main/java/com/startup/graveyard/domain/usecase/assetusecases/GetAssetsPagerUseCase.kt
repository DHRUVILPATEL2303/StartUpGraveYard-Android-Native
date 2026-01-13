package com.startup.graveyard.domain.usecase.assetusecases

import androidx.paging.Pager
import com.startup.graveyard.data.remote.AssetFilter
import com.startup.graveyard.domain.models.assets.Asset
import com.startup.graveyard.domain.repo.assetrepo.AssetRepository
import javax.inject.Inject

class GetAssetsPagerUseCase @Inject constructor(
    private val repository: AssetRepository
) {
    operator fun invoke(
        filter: AssetFilter
    ): Pager<Int, Asset> {
        return repository.getAssetsPager(filter)
    }
}