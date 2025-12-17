package com.startup.graveyard.domain.usecase.authusecases

import com.google.firebase.auth.ActionCodeEmailInfo
import com.startup.graveyard.domain.repo.authrepo.AuthRepository
import javax.inject.Inject

class LoginUserUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend fun loginUseCase(
        email : String ,
        password : String
    )=authRepository.login(email,password)
}