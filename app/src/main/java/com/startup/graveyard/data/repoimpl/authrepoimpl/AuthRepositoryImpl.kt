package com.startup.graveyard.data.repoimpl.authrepoimpl

import androidx.collection.emptyIntSet
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.startup.graveyard.common.ResultState
import com.startup.graveyard.domain.models.CreateAccountModel
import com.startup.graveyard.domain.repo.authrepo.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : AuthRepository {
    override suspend fun createAccount(createAccountModel: CreateAccountModel): Flow<ResultState<FirebaseUser>> =
        flow { val user = firebaseAuth.createUserWithEmailAndPassword(
                createAccountModel.email,
                createAccountModel.password
            ).await()

            try {
                val user = firebaseAuth.createUserWithEmailAndPassword(
                    createAccountModel.email,
                    createAccountModel.password
                ).await()

                if (user.user!=null){
                    emit(ResultState.Success(user.user!!))

                }

            }catch (e: Exception){
                emit(ResultState.Error(e.message.toString()))

            }
        }

    override fun login(): Flow<ResultState<FirebaseUser>> {
        TODO("Not yet implemented")
    }
}