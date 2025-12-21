package com.startup.graveyard.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.startup.graveyard.common.ResultState
import com.startup.graveyard.domain.models.CreateAssetRequestModel
import com.startup.graveyard.domain.models.CreateAssetResponseModel
import com.startup.graveyard.domain.usecase.assetusecases.CreateAssetUseCase
import com.startup.graveyard.domain.usecase.assetusecases.GetAssetsPagerUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AssetViewModel @Inject constructor(
    private val createAssetUseCase: CreateAssetUseCase,
    private val getAssetsPagerUseCase: GetAssetsPagerUseCase
) : ViewModel() {


    private val _createAssetState = MutableStateFlow(AssetState<CreateAssetResponseModel>())
    val createAssetState = _createAssetState.asStateFlow()


    fun createAsset(createAssetRequestModel: CreateAssetRequestModel) {
        viewModelScope.launch(Dispatchers.IO) {
            createAssetUseCase.createAssetUseCase(createAssetRequestModel).collect {
                when (it) {
                    is ResultState.Error -> {
                        _createAssetState.value = AssetState(isLoading = false, error = it.error)
                    }

                    is ResultState.Loading -> {
                        _createAssetState.value = AssetState(isLoading = true)
                    }

                    is ResultState.Success -> {
                        _createAssetState.value = AssetState(isLoading = false, data = it.data)
                    }
                }
            }
        }
    }


    val assetsPagingFlow =
        getAssetsPagerUseCase()
            .flow
            .cachedIn(viewModelScope)

}


data class AssetState<T>(
    val data: T? = null,
    val error: String = "",
    val isLoading: Boolean = false
)