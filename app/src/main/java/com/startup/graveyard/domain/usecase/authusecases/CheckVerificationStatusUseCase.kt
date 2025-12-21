package com.startup.graveyard.domain.usecase.authusecases

import com.startup.graveyard.domain.repo.authrepo.AuthRepository
import javax.inject.Inject

class CheckVerificationStatusUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {

    suspend fun checkVerification()=authRepository.checkVerificationStatus()
}