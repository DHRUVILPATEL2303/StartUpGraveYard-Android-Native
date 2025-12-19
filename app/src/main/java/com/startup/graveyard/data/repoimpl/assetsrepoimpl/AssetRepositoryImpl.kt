package com.startup.graveyard.data.repoimpl.assetsrepoimpl

import com.google.firebase.auth.FirebaseAuth
import com.startup.graveyard.common.ResultState
import com.startup.graveyard.data.remote.AssetApi
import com.startup.graveyard.domain.models.CreateAssetRequestModel
import com.startup.graveyard.domain.models.CreateAssetResponseModel
import com.startup.graveyard.domain.repo.assetrepo.AssetRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AssetRepositoryImpl @Inject constructor(
    private val assetApi: AssetApi,
    private val firebaseAuth: FirebaseAuth
)  : AssetRepository{
    override suspend fun createNewAsset(createAssetRequestModel: CreateAssetRequestModel): Flow<ResultState<CreateAssetResponseModel>> =
        flow{

            emit(ResultState.Loading)

            try {

                val uuid = firebaseAuth.uid
//                createAssetRequestModel.startup_id=uuid

            }catch (e: Exception){
                emit(ResultState.Error(e.message.toString()))
            }
        }


}