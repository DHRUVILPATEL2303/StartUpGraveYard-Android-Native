package com.startup.graveyard.presentation.viewmodels

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.ui.window.isPopupLayout
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.startup.graveyard.common.ResultState
import com.startup.graveyard.domain.models.CreateAccountModel
import com.startup.graveyard.domain.models.CreatedAccountResponseModel
import com.startup.graveyard.domain.usecase.authusecases.CreateAccountUseCase
import com.startup.graveyard.domain.usecase.authusecases.LoginUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.ceil

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val createAccountUseCase: CreateAccountUseCase,
    private val loginUserUseCase: LoginUserUseCase
) : ViewModel() {

    private val _createAccountState = MutableStateFlow(AuthState<CreatedAccountResponseModel>())
    val createAccountState = _createAccountState.asStateFlow()


    private val _loginState = MutableStateFlow(AuthState<FirebaseUser>())
    val loginState = _loginState.asStateFlow()
    fun createAccount(
        createAccountModel: CreateAccountModel
    ) {
        viewModelScope.launch {
            createAccountUseCase.createAccountUseCase(createAccountModel).collect {
                when (it) {
                    is ResultState.Error -> {
                        _createAccountState.value = AuthState(error = it.error, isLoading = false)
                    }

                    is ResultState.Success -> {
                        _createAccountState.value = AuthState(data = it.data, isLoading = false)

                    }

                    is ResultState.Loading -> {
                        _createAccountState.value = AuthState(isLoading = true)
                    }
                }
            }
        }
    }


    fun loginUser(
        email: String,
        password: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            loginUserUseCase.loginUseCase(email, password).collect {
                when (it) {
                    is ResultState.Error -> {
                        _loginState.value = AuthState(
                            error = it.error,
                            isLoading = false
                        )
                    }

                    is ResultState.Loading -> {
                        _loginState.value = AuthState(
                            isLoading = true
                        )
                    }

                    is ResultState.Success -> {
                        _loginState.value = AuthState(
                            isLoading = false,
                            data = it.data
                        )

                    }
                }
            }
        }


    }
}

data class AuthState<T>(
    val data: T? = null,
    val error: String = "",
    val isLoading: Boolean = false
)