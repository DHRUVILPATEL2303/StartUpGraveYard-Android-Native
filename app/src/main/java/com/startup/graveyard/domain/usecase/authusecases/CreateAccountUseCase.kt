package com.startup.graveyard.domain.usecase.authusecases

import com.startup.graveyard.domain.models.CreateAccountModel
import com.startup.graveyard.domain.repo.authrepo.AuthRepository
import javax.inject.Inject

class CreateAccountUseCase @Inject constructor(
    private val authRepository: AuthRepository
){

    suspend fun createAccountUseCase(
        createAccountModel: CreateAccountModel
    ) =authRepository.createAccount(createAccountModel)
}