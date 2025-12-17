package com.startup.graveyard.data.remote

import com.startup.graveyard.domain.models.CreateAccountModel
import com.startup.graveyard.domain.models.CreatedAccountResponseModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {

    @POST("/users")
   suspend fun createAccount(
        @Body createAccountModel: CreateAccountModel
    ): Response<CreatedAccountResponseModel>

}