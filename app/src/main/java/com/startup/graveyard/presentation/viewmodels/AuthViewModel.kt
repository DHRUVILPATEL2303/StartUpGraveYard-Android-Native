package com.startup.graveyard.presentation.viewmodels

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.ui.window.isPopupLayout
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.startup.graveyard.common.ResultState
import com.startup.graveyard.domain.models.CreateAccountModel
import com.startup.graveyard.domain.models.CreatedAccountResponseModel
import com.startup.graveyard.domain.models.UpdateUserAccountRequestModel
import com.startup.graveyard.domain.models.UserAccountResponseModel
import com.startup.graveyard.domain.usecase.authusecases.CreateAccountUseCase
import com.startup.graveyard.domain.usecase.authusecases.GetUserAccountDetailsUseCase
import com.startup.graveyard.domain.usecase.authusecases.LoginUserUseCase
import com.startup.graveyard.domain.usecase.authusecases.UpdateUserAccountUseCase
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
    private val loginUserUseCase: LoginUserUseCase,
    private val getUserAccountDetailsUseCase: GetUserAccountDetailsUseCase,
    private val updateUserAccountUseCase: UpdateUserAccountUseCase,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _createAccountState = MutableStateFlow(AuthState<CreatedAccountResponseModel>())
    val createAccountState = _createAccountState.asStateFlow()


    private val _loginState = MutableStateFlow(AuthState<FirebaseUser>())
    val loginState = _loginState.asStateFlow()


    private val _accountState = MutableStateFlow(AuthState<UserAccountResponseModel>())
    val accountState = _accountState.asStateFlow()


    private val _updateAccountState = MutableStateFlow(AuthState<UserAccountResponseModel>())
    val updateAccountState = _updateAccountState.asStateFlow()


    fun clearAllState() {
        _createAccountState.value = AuthState()
        _loginState.value = AuthState()
        _accountState.value = AuthState()
        _updateAccountState.value = AuthState()
    }

    fun logOut(){
        firebaseAuth.signOut()
        clearAllState()
    }

    fun updateAccount(
        updateUserAccountRequestModel: UpdateUserAccountRequestModel
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            updateUserAccountUseCase.updateUserAccountUseCase(updateUserAccountRequestModel)
                .collect {
                    when (it) {
                        is ResultState.Error -> {
                            _updateAccountState.value =
                                AuthState(isLoading = false, error = it.error)
                        }

                        is ResultState.Loading -> {
                            _updateAccountState.value = AuthState(isLoading = true)

                        }

                        is ResultState.Success -> {
                            _updateAccountState.value = AuthState(
                                data = it.data,
                                isLoading = false
                            )
                        }
                    }
                }
        }


    }

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


    fun getUserAccountDetails() {
        viewModelScope.launch(Dispatchers.IO) {
            getUserAccountDetailsUseCase.getUserAccountUseCase().collect {
                when (it) {
                    is ResultState.Error -> {
                        _accountState.value = AuthState(error = it.error, isLoading = false)
                    }

                    is ResultState.Loading -> {
                        _accountState.value = AuthState(isLoading = true)

                    }

                    is ResultState.Success -> {
                        _accountState.value = AuthState(isLoading = false, data = it.data)
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