package com.startup.graveyard.domain.usecase.authusecases

import com.startup.graveyard.domain.repo.authrepo.AuthRepository
import javax.inject.Inject

class DeleteUserAccountUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {

    suspend fun deleteuserAccoutnUseCase()=authRepository.deleteUserAccount()
}