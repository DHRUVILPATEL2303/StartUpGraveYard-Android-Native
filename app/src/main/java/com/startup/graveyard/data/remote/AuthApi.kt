package com.startup.graveyard.data.remote

import com.startup.graveyard.domain.models.CreateAccountModel
import com.startup.graveyard.domain.models.CreatedAccountResponseModel
import com.startup.graveyard.domain.models.UpdateUserAccountRequestModel
import com.startup.graveyard.domain.models.UserAccountResponseModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface AuthApi {

    @POST("/users")
   suspend fun createAccount(
        @Body createAccountModel: CreateAccountModel
    ): Response<CreatedAccountResponseModel>


   @GET("/users/{id}")
   suspend fun getAccountDetails(
       @Path("id") id : String
   ): Response<UserAccountResponseModel>


   @PUT("/users/{id}")
   suspend fun updateAccount(
       @Path("id") id : String,
       @Body updateUserAccountRequestModel: UpdateUserAccountRequestModel
   ): Response<UserAccountResponseModel>


}