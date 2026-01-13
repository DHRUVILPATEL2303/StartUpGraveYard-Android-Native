package com.startup.graveyard.presentation.viewmodels.assets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.startup.graveyard.common.ResultState
import com.startup.graveyard.data.remote.AssetFilter
import com.startup.graveyard.domain.models.CreateAssetRequestModel
import com.startup.graveyard.domain.models.CreateAssetResponseModel
import com.startup.graveyard.domain.models.assets.Asset
import com.startup.graveyard.domain.models.assets.GetSpecificAssetResponseModel
import com.startup.graveyard.domain.usecase.assetusecases.CreateAssetUseCase
import com.startup.graveyard.domain.usecase.assetusecases.GetAssetsPagerUseCase
import com.startup.graveyard.domain.usecase.assetusecases.GetSpecificAssetDetailsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AssetViewModel @Inject constructor(
    private val createAssetUseCase: CreateAssetUseCase,
    private val getAssetsPagerUseCase: GetAssetsPagerUseCase,
    private val getSpecificAssetDetailsUseCase: GetSpecificAssetDetailsUseCase
) : ViewModel() {


    private val _createAssetState = MutableStateFlow(AssetState<CreateAssetResponseModel>())
    val createAssetState = _createAssetState.asStateFlow()

    fun createAsset(request: CreateAssetRequestModel) {
        viewModelScope.launch(Dispatchers.IO) {
            createAssetUseCase.createAssetUseCase(request).collect {
                when (it) {
                    is ResultState.Loading ->
                        _createAssetState.value = AssetState(isLoading = true)

                    is ResultState.Success ->
                        _createAssetState.value = AssetState(data = it.data)

                    is ResultState.Error ->
                        _createAssetState.value = AssetState(error = it.error)
                }
            }
        }
    }

    fun resetCreateAssetState() {
        _createAssetState.value = AssetState()
    }


    private val _assetFilter = MutableStateFlow(AssetFilter())
    val assetFilter = _assetFilter.asStateFlow()


    val assetsPagingFlow =
        _assetFilter
            .flatMapLatest { filter ->
                getAssetsPagerUseCase(filter).flow
            }
            .cachedIn(viewModelScope)


    fun loadBuyerAssets() {
        _assetFilter.value = AssetFilter()
    }

    fun loadSellerAssets(userUuid: String) {
        _assetFilter.value = AssetFilter(userUuid = userUuid)
    }

    fun filterByAssetType(type: String?) {
        _assetFilter.value = _assetFilter.value.copy(assetType = type)
    }

    fun filterBySold(isSold: Boolean?) {
        _assetFilter.value = _assetFilter.value.copy(isSold = isSold)
    }

    fun clearFilters() {
        _assetFilter.value = AssetFilter()
    }


    private val _specificAssetState = MutableStateFlow(AssetState<Asset>())
    val specificAssetState = _specificAssetState.asStateFlow()

    fun getSpecificAssetDetails(id: String) {
        val assetId = id.toInt()

        AssetMemoryCache.get(assetId)?.let {
            _specificAssetState.value = AssetState(data = it)
        }

        viewModelScope.launch(Dispatchers.IO) {
            getSpecificAssetDetailsUseCase
                .getSpecificAssetDetailsUseCase(id)
                .collect { result ->
                    when (result) {
                        is ResultState.Loading -> {
                            if (_specificAssetState.value.data == null) {
                                _specificAssetState.value = AssetState(isLoading = true)
                            }
                        }

                        is ResultState.Success -> {
                            AssetMemoryCache.put(result.data)
                            _specificAssetState.value = AssetState(data = result.data)
                        }

                        is ResultState.Error -> {
                            if (_specificAssetState.value.data == null) {
                                _specificAssetState.value = AssetState(error = result.error)
                            }
                        }
                    }
                }
        }
    }
}

data class AssetState<T>(
    val data: T? = null,
    val error: String = "",
    val isLoading: Boolean = false
)