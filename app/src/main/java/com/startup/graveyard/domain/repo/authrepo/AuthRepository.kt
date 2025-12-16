package com.startup.graveyard.domain.repo.authrepo

import com.google.firebase.auth.FirebaseUser
import com.startup.graveyard.common.ResultState
import com.startup.graveyard.domain.models.CreateAccountModel
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun createAccount(createAccountModel: CreateAccountModel) : Flow<ResultState<FirebaseUser>>
    fun login() : Flow<ResultState<FirebaseUser>>
}