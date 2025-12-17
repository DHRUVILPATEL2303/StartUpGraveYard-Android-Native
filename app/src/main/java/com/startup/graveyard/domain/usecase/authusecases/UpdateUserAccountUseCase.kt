package com.startup.graveyard.domain.usecase.authusecases

import com.startup.graveyard.domain.models.UpdateUserAccountRequestModel
import com.startup.graveyard.domain.repo.authrepo.AuthRepository
import javax.inject.Inject

class UpdateUserAccountUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {

    suspend fun updateUserAccountUseCase(
        updateUserAccountRequestModel: UpdateUserAccountRequestModel
    )=authRepository.updateAccount(updateUserAccountRequestModel)
}