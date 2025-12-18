package com.startup.graveyard.domain.repo.authrepo

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
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun createAccount(createAccountModel: CreateAccountModel): Flow<ResultState<CreatedAccountResponseModel>>
    fun login(email: String, password: String): Flow<ResultState<FirebaseUser>>


    suspend fun getUserAccountDetails(): Flow<ResultState<UserAccountResponseModel>>

    suspend fun updateAccount(
        updateUserAccountRequestModel: UpdateUserAccountRequestModel
    ): Flow<ResultState<UserAccountResponseModel>>


    suspend fun sendOtpRequest(

    ): Flow<ResultState<SendOTPReqeustResponseModel>>


    suspend fun verifyOTP(
        verifyOTPRequestModel: VerifyOTPRequestModel
    ): Flow<ResultState<VerifyOTPResponseModel>>

    suspend fun deleteUserAccount(

    ): Flow<ResultState<DeleteAccountResponseModel>>
}