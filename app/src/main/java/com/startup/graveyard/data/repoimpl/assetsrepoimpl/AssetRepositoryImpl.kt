package com.startup.graveyard.data.repoimpl.assetsrepoimpl

import androidx.compose.ui.geometry.Rect
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.google.firebase.auth.FirebaseAuth
import com.startup.graveyard.common.ResultState
import com.startup.graveyard.data.paging.AssetPagingSource
import com.startup.graveyard.data.paging.UserAssetPagingSource
import com.startup.graveyard.data.remote.AssetApi
import com.startup.graveyard.domain.models.CreateAssetRequestModel
import com.startup.graveyard.domain.models.CreateAssetResponseModel
import com.startup.graveyard.domain.models.assets.Asset
import com.startup.graveyard.domain.models.getallassets.GetAllAssetsResponseModel
import com.startup.graveyard.domain.repo.assetrepo.AssetRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AssetRepositoryImpl @Inject constructor(
    private val assetApi: AssetApi,
    private val firebaseAuth: FirebaseAuth
) : AssetRepository {
    override suspend fun createNewAsset(createAssetRequestModel: CreateAssetRequestModel): Flow<ResultState<CreateAssetResponseModel>> =
        flow {

            emit(ResultState.Loading)

            try {

                val uuid = firebaseAuth.uid
                createAssetRequestModel.user_uuid = uuid.toString()

                val respone = assetApi.createNewAsset(createAssetRequestModel)
                if (respone.isSuccessful && respone.body() != null) {
                    emit(ResultState.Success(respone.body()!!))
                } else {
                    emit(ResultState.Error("Something Went Wrong Asset Not Created Try Again !!"))
                }


            } catch (e: Exception) {
                emit(ResultState.Error(e.message.toString()))
            }
        }

    override fun getAssetsPager(): Pager<Int, Asset> {
        return Pager(
            PagingConfig(
                pageSize = 20,
                initialLoadSize = 40,
                prefetchDistance = 6,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                AssetPagingSource(assetApi)
            }
        )
    }

    override fun getAllSpecificUserAsset(): Pager<Int, Asset> {

        val userId = firebaseAuth.uid.toString()
        return Pager(
            PagingConfig(
                pageSize = 20,
                initialLoadSize = 40,
                prefetchDistance = 6,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                UserAssetPagingSource(assetApi, userId = userId)
            }
        )
    }

}