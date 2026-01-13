package com.startup.graveyard.presentation.viewmodels.startups

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.startup.graveyard.domain.usecase.startupusecases.GetAllSpecificUserStartupUseCase
import com.startup.graveyard.domain.usecase.startupusecases.GetAllStartUpUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StartUpViewModel @Inject constructor(
    private val getAllStartUpUseCase: GetAllStartUpUseCase,
    private val getAllSpecificUserStartupUseCase: GetAllSpecificUserStartupUseCase
) : ViewModel(){


    val startupsPagingFlow =
        getAllStartUpUseCase.getAllStartupUseCase()
            .flow
            .cachedIn(viewModelScope)


    val specificUserStartUpsPagingFlow =
        getAllSpecificUserStartupUseCase.getAllSpecificUserStartUpUseCase()
            .flow
            .cachedIn(viewModelScope)
}

data class StartUPState<T>(
    val data: T? = null,
    val error: String = "",
    val isLoading: Boolean = false
)