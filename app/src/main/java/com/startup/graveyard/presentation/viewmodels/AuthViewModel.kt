package com.startup.graveyard.presentation.viewmodels

import android.util.Log
import androidx.compose.ui.window.isPopupLayout
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.startup.graveyard.common.ResultState
import com.startup.graveyard.domain.models.CreateAccountModel
import com.startup.graveyard.domain.models.CreatedAccountResponseModel
import com.startup.graveyard.domain.models.DeleteAccountResponseModel
import com.startup.graveyard.domain.models.SendOTPReqeustResponseModel
import com.startup.graveyard.domain.models.UpdateUserAccountRequestModel
import com.startup.graveyard.domain.models.UserAccountResponseModel
import com.startup.graveyard.domain.models.VerifyOTPRequestModel
import com.startup.graveyard.domain.models.VerifyOTPResponseModel
import com.startup.graveyard.domain.usecase.authusecases.CheckVerificationStatusUseCase
import com.startup.graveyard.domain.usecase.authusecases.CreateAccountUseCase
import com.startup.graveyard.domain.usecase.authusecases.DeleteUserAccountUseCase
import com.startup.graveyard.domain.usecase.authusecases.GetUserAccountDetailsUseCase
import com.startup.graveyard.domain.usecase.authusecases.LoginUserUseCase
import com.startup.graveyard.domain.usecase.authusecases.SendOTPVerrificationRequestUseCase
import com.startup.graveyard.domain.usecase.authusecases.UpdateUserAccountUseCase
import com.startup.graveyard.domain.usecase.authusecases.VerifyOTPUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val createAccountUseCase: CreateAccountUseCase,
    private val loginUserUseCase: LoginUserUseCase,
    private val getUserAccountDetailsUseCase: GetUserAccountDetailsUseCase,
    private val updateUserAccountUseCase: UpdateUserAccountUseCase,
    private val firebaseAuth: FirebaseAuth,
    private val sendOTPVerrificationRequestUseCase: SendOTPVerrificationRequestUseCase,
    private val verifyOTPUseCase: VerifyOTPUseCase,
    private val deleteUserAccountUseCase: DeleteUserAccountUseCase,
    private val checkVerificationStatusUseCase: CheckVerificationStatusUseCase
) : ViewModel() {

    private val _createAccountState = MutableStateFlow(AuthState<CreatedAccountResponseModel>())
    val createAccountState = _createAccountState.asStateFlow()

    private val _loginState = MutableStateFlow(AuthState<FirebaseUser>())
    val loginState = _loginState.asStateFlow()

    private val _accountState = MutableStateFlow(AuthState<UserAccountResponseModel>())
    val accountState = _accountState.asStateFlow()

    private val _updateAccountState = MutableStateFlow(AuthState<UserAccountResponseModel>())
    val updateAccountState = _updateAccountState.asStateFlow()

    private val _sendOtpForVerificationState =
        MutableStateFlow(AuthState<SendOTPReqeustResponseModel>())
    val sendOtpVerificationState = _sendOtpForVerificationState.asStateFlow()

    private val _verifyOtpState = MutableStateFlow(AuthState<VerifyOTPResponseModel>())
    val verifyOtpState = _verifyOtpState.asStateFlow()


    private val _checkVerificationState = MutableStateFlow(AuthState<Boolean>())
    val checkVerificationState = _checkVerificationState.asStateFlow()


    private val _deleteAccountState = MutableStateFlow(AuthState<DeleteAccountResponseModel>())
    val deleteAccountState = _deleteAccountState.asStateFlow()


    init {
        if (firebaseAuth.currentUser != null) {
            checkVerification()
        }
    }

    fun deleteAccount() {
        viewModelScope.launch(Dispatchers.IO) {
            deleteUserAccountUseCase.deleteuserAccoutnUseCase().collect {
                when (it) {
                    is ResultState.Loading -> _deleteAccountState.value =
                        AuthState(isLoading = true)

                    is ResultState.Error -> _deleteAccountState.value =
                        AuthState(isLoading = false, error = it.error)

                    is ResultState.Success -> _deleteAccountState.value =
                        AuthState(data = it.data, isLoading = false)
                }
            }
        }
    }

    fun verifyOtp(verifyOTPRequestModel: VerifyOTPRequestModel) {
        viewModelScope.launch(Dispatchers.IO) {
            verifyOTPUseCase.verifyOtp(verifyOTPRequestModel).collect {
                when (it) {
                    is ResultState.Error -> _verifyOtpState.value =
                        AuthState(isLoading = false, error = it.error)

                    is ResultState.Loading -> _verifyOtpState.value = AuthState(isLoading = true)
                    is ResultState.Success -> _verifyOtpState.value =
                        AuthState(isLoading = false, data = it.data)
                }
            }
        }
    }

    fun sendOtpForVerificationRequest() {
        viewModelScope.launch(Dispatchers.IO) {
            sendOTPVerrificationRequestUseCase.sendOtpVerificationRequestUseCase().collect {
                when (it) {
                    is ResultState.Error -> _sendOtpForVerificationState.value =
                        AuthState(isLoading = false, error = it.error)

                    is ResultState.Success -> _sendOtpForVerificationState.value =
                        AuthState(isLoading = false, data = it.data)

                    is ResultState.Loading -> _sendOtpForVerificationState.value =
                        AuthState(isLoading = true)
                }
            }
        }
    }

    fun checkVerification() {
        viewModelScope.launch(Dispatchers.IO) {
            checkVerificationStatusUseCase.checkVerification().collect {
                when (it) {
                    is ResultState.Error -> _checkVerificationState.value =
                        AuthState(isLoading = false, error = it.error)

                    is ResultState.Loading -> {
                        _checkVerificationState.value = AuthState(isLoading = true)
                    }

                    is ResultState.Success -> {
                        _checkVerificationState.value = AuthState(isLoading = false, data = it.data)
                        Log.d("VEIRIFCATION STATUS", it.data.toString())
                    }
                }
            }
        }
    }

    fun clearAllState() {
        _createAccountState.value = AuthState()
        _loginState.value = AuthState()
        _accountState.value = AuthState()
        _updateAccountState.value = AuthState()
        _sendOtpForVerificationState.value = AuthState()
        _verifyOtpState.value = AuthState()
        _deleteAccountState.value = AuthState()
    }

    fun logOut() {
        firebaseAuth.signOut()
        clearAllState()
    }

    fun updateAccount(updateUserAccountRequestModel: UpdateUserAccountRequestModel) {
        viewModelScope.launch(Dispatchers.IO) {
            updateUserAccountUseCase.updateUserAccountUseCase(updateUserAccountRequestModel)
                .collect {
                    when (it) {
                        is ResultState.Error -> _updateAccountState.value =
                            AuthState(isLoading = false, error = it.error)

                        is ResultState.Loading -> _updateAccountState.value =
                            AuthState(isLoading = true)

                        is ResultState.Success -> _updateAccountState.value =
                            AuthState(data = it.data, isLoading = false)
                    }
                }
        }
    }

    fun createAccount(createAccountModel: CreateAccountModel) {
        viewModelScope.launch {
            createAccountUseCase.createAccountUseCase(createAccountModel).collect {
                when (it) {
                    is ResultState.Error -> _createAccountState.value =
                        AuthState(error = it.error, isLoading = false)

                    is ResultState.Success -> _createAccountState.value =
                        AuthState(data = it.data, isLoading = false)

                    is ResultState.Loading -> _createAccountState.value =
                        AuthState(isLoading = true)
                }
            }
        }
    }

    fun getUserAccountDetails() {

        if (_accountState.value.data != null) return
        viewModelScope.launch(Dispatchers.IO) {
            getUserAccountDetailsUseCase.getUserAccountUseCase().collect {
                when (it) {
                    is ResultState.Error -> _accountState.value =
                        AuthState(error = it.error, isLoading = false)

                    is ResultState.Loading -> _accountState.value = AuthState(isLoading = true)
                    is ResultState.Success -> _accountState.value =
                        AuthState(isLoading = false, data = it.data)
                }
            }
        }
    }

    fun loginUser(email: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            loginUserUseCase.loginUseCase(email, password).collect {
                when (it) {
                    is ResultState.Error -> _loginState.value =
                        AuthState(error = it.error, isLoading = false)

                    is ResultState.Loading -> _loginState.value = AuthState(isLoading = true)
                    is ResultState.Success -> _loginState.value =
                        AuthState(isLoading = false, data = it.data)
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