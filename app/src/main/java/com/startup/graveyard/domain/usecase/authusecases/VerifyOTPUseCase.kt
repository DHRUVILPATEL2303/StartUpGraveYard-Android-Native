package com.startup.graveyard.domain.usecase.authusecases

import com.startup.graveyard.data.remote.AuthApi
import com.startup.graveyard.domain.models.VerifyOTPRequestModel
import com.startup.graveyard.domain.repo.authrepo.AuthRepository
import javax.inject.Inject

class VerifyOTPUseCase @Inject constructor(
    private val authRepository: AuthRepository
){
    suspend fun verifyOtp(
        verifyOTPRequestModel: VerifyOTPRequestModel
    )=authRepository.verifyOTP(verifyOTPRequestModel)
}