package com.startup.graveyard.presentation.viewmodels

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(

) : ViewModel() {

    private val _isAnimationDone = MutableStateFlow(false)
    val isAnimationDone: StateFlow<Boolean> = _isAnimationDone

    fun markAnimationDone() {
        if (!_isAnimationDone.value) {
            _isAnimationDone.value = true
        }
    }
}