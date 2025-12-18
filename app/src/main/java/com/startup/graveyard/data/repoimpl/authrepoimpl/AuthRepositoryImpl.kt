package com.startup.graveyard.data.repoimpl.authrepoimpl

import android.util.Log
import androidx.collection.emptyIntSet
import androidx.compose.ui.graphics.RectangleShape
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.startup.graveyard.common.ResultState
import com.startup.graveyard.data.remote.AuthApi
import com.startup.graveyard.domain.models.CreateAccountModel
import com.startup.graveyard.domain.models.CreatedAccountResponseModel
import com.startup.graveyard.domain.models.DeleteAccountResponseModel
import com.startup.graveyard.domain.models.SendOTPReqeustResponseModel
import com.startup.graveyard.domain.models.SendOTPRequestModel
import com.startup.graveyard.domain.models.UpdateUserAccountRequestModel
import com.startup.graveyard.domain.models.UserAccountResponseModel
import com.startup.graveyard.domain.models.VerifyOTPRequestModel
import com.startup.graveyard.domain.models.VerifyOTPResponseModel
import com.startup.graveyard.domain.repo.authrepo.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val authApi: AuthApi
) : AuthRepository {

    companion object {
        private const val TAG = "AuthRepositoryImpl"
    }

    override suspend fun createAccount(
        createAccountModel: CreateAccountModel
    ): Flow<ResultState<CreatedAccountResponseModel>> = flow {

        Log.d(TAG, " createAccount() called")
        Log.d(TAG, " Input model: $createAccountModel")

        emit(ResultState.Loading)
        Log.d(TAG, " Emitted Loading state")

        try {
            Log.d(TAG, " Creating Firebase user with email=${createAccountModel.email}")

            val authResult = firebaseAuth
                .createUserWithEmailAndPassword(
                    createAccountModel.email,
                    createAccountModel.password
                )
                .await()

            val firebaseUser = authResult.user

            if (firebaseUser == null) {
                Log.e(TAG, " Firebase user is NULL after creation")
                emit(ResultState.Error("Firebase user creation failed"))
                return@flow
            }

            Log.d(TAG, " Firebase user created")
            Log.d(TAG, " Firebase UID: ${firebaseUser.uid}")

            createAccountModel.uuid = firebaseUser.uid
            Log.d(TAG, "️ UUID updated in model: ${createAccountModel.uuid}")

            Log.d(TAG, " Calling backend createAccount API")

            val response = authApi.createAccount(createAccountModel)

            Log.d(TAG, " API raw response: ${response.raw()}")
            Log.d(TAG, " API code: ${response.code()}")
            Log.d(TAG, " API message: ${response.message()}")

            if (response.isSuccessful && response.body() != null) {
                Log.d(TAG, " Backend account created successfully")
                Log.d(TAG, " Response body: ${response.body()}")

                emit(ResultState.Success(response.body()!!))
                Log.d(TAG, " Emitted Success state")

            } else {
                Log.e(TAG, " Backend account creation failed")
                Log.e(TAG, "️ Rolling back Firebase user")

                firebaseUser.delete().await()
                Log.d(TAG, "️ Firebase user deleted successfully")

                emit(ResultState.Error(response.message() ?: "Backend account creation failed"))
                Log.d(TAG, " Emitted Error state (API failure)")
            }

        } catch (e: Exception) {
            Log.e(TAG, " Exception occurred", e)
            emit(ResultState.Error(e.message ?: "Unknown error occurred"))
            Log.d(TAG, " Emitted Error state (Exception)")
        }

        Log.d(TAG, " createAccount() flow completed")
    }

    override fun login(email: String, password: String): Flow<ResultState<FirebaseUser>> = flow {
        emit(ResultState.Loading)

        try {
            val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            if (authResult.user != null) {
                emit(ResultState.Success(authResult.user!!))
            } else {
                emit(ResultState.Error("Something Went Wrong Try Again After Sometime!!!"))
            }

        } catch (e: Exception) {
            emit(ResultState.Error(e.message.toString()))
        }


    }

    override suspend fun getUserAccountDetails(): Flow<ResultState<UserAccountResponseModel>> =
        flow {
            emit(ResultState.Loading)

            try {
                val uuid = firebaseAuth.uid
                val response = authApi.getAccountDetails(uuid.toString())
                if (response.isSuccessful && response.body() != null) {
                    emit(ResultState.Success(response.body()!!))

                } else {
                    emit(ResultState.Error("Something Went Wrong"))
                }

            } catch (e: Exception) {
                emit(ResultState.Error(e.message.toString()))
            }
        }

    override suspend fun updateAccount(updateUserAccountRequestModel: UpdateUserAccountRequestModel): Flow<ResultState<UserAccountResponseModel>> =
        flow {

            emit(ResultState.Loading)

            try {
                val uuid = firebaseAuth.uid
                updateUserAccountRequestModel.uuid = uuid.toString()

                val response = authApi.updateAccount(uuid.toString(), updateUserAccountRequestModel)
                if (response.isSuccessful && response.body() != null) {
                    emit(ResultState.Success(response.body()!!))
                } else {
                    emit(ResultState.Error("Something Went Wrong"))
                }

            } catch (e: Exception) {
                emit(ResultState.Error(e.message.toString()))
            }
        }

    override suspend fun sendOtpRequest(): Flow<ResultState<SendOTPReqeustResponseModel>> = flow {

        emit(ResultState.Loading)

        try {
            val email = firebaseAuth.currentUser?.email ?: ""
            val emailRequestModel = SendOTPRequestModel(
                email = email
            )
            Log.d("TAG", "sendOtpRequest: $email")
            val response = authApi.sendOtpRequest(emailRequestModel)
            if (response.isSuccessful && response.body() != null) {
                emit(ResultState.Success(response.body()!!))
            } else {
                emit(ResultState.Error("Something Went Wrong Try Again"))
            }
        } catch (e: Exception) {
            emit(ResultState.Error(e.message.toString()))
        }
    }

    override suspend fun verifyOTP(verifyOTPRequestModel: VerifyOTPRequestModel): Flow<ResultState<VerifyOTPResponseModel>> =
        flow {

            emit(ResultState.Loading)


            try {
                val email = firebaseAuth.currentUser?.email ?: ""
                verifyOTPRequestModel.email = email
                val response = authApi.verifyOTP(verifyOTPRequestModel)
                if (response.isSuccessful && response.body() != null) {
                    emit(ResultState.Success(response.body()!!))
                } else {
                    emit(ResultState.Error("Something Went Wrong Try Again After Sometime"))
                }
            } catch (e: Exception) {
                emit(ResultState.Error(e.message.toString()))
            }
        }

    override suspend fun deleteUserAccount(): Flow<ResultState<DeleteAccountResponseModel>> = flow {
        emit(ResultState.Loading)

        try {
            val firebaseUser = firebaseAuth.currentUser
            val uuid = firebaseUser?.uid

            if (firebaseUser == null || uuid == null) {
                emit(ResultState.Error("User not authenticated"))
                return@flow
            }

            val response = authApi.deleteUserAccount(uuid)
            Log.d("DELETE-ACCOUNT RESPONSE",response.toString())

            if (response.isSuccessful && response.body() != null) {

                firebaseUser.delete().await()

                firebaseAuth.signOut()

                emit(ResultState.Success(response.body()!!))

            } else {
                emit(ResultState.Error("Account deletion failed on server"))
            }

        } catch (e: Exception) {
            emit(ResultState.Error(e.message ?: "Failed to delete account"))
        }
    }
}