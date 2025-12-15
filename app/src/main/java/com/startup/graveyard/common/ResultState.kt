package com.startup.graveyard.common

import androidx.compose.ui.Modifier

sealed class ResultState<out T> {
    data class Success<out T>(val data : T): ResultState<T>()
    data class Error(val error: String) : ResultState<Nothing>()
    object Loading : ResultState<Nothing>()

}