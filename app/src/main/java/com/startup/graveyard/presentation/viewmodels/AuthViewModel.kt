package com.startup.graveyard.presentation.viewmodels

import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.startup.graveyard.common.ResultState
import com.startup.graveyard.domain.models.CreateAccountModel
import com.startup.graveyard.domain.models.CreateAccountResponseModel
import com.startup.graveyard.domain.usecase.authusecases.CreateAccountUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.ceil

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val createAccountUseCase: CreateAccountUseCase
) : ViewModel() {

    private val _createAccountState= MutableStateFlow(AuthState<FirebaseUser>())
    val createAccountState=_createAccountState.asStateFlow()
    fun createAccount(
        createAccountModel: CreateAccountModel
    ){
        viewModelScope.launch {
            createAccountUseCase.createAccountUseCase(createAccountModel).collect {
                when(it){
                    is ResultState.Error -> {
                        _createAccountState.value= AuthState(error=it.error, isLoading = false)
                    }
                    is ResultState.Success -> {
                        _createAccountState.value= AuthState(data=it.data, isLoading = false)

                    }
                    is ResultState.Loading -> {
                        _createAccountState.value= AuthState(isLoading = true)
                    }
                }
            }
        }
    }
}

data class AuthState<T>(
    val data : T ?= null,
    val error : String = "",
    val isLoading : Boolean=false
)