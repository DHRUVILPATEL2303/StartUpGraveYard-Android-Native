package com.startup.graveyard.data.remote

import com.startup.graveyard.domain.models.CheckVerificationStatusRequestModel
import com.startup.graveyard.domain.models.CreateAccountModel
import com.startup.graveyard.domain.models.CreatedAccountResponseModel
import com.startup.graveyard.domain.models.DeleteAccountResponseModel
import com.startup.graveyard.domain.models.LoginUserRequestModel
import com.startup.graveyard.domain.models.LoginUserResponseModel
import com.startup.graveyard.domain.models.SendOTPReqeustResponseModel
import com.startup.graveyard.domain.models.SendOTPRequestModel
import com.startup.graveyard.domain.models.UpdateUserAccountRequestModel
import com.startup.graveyard.domain.models.UserAccountResponseModel
import com.startup.graveyard.domain.models.VerifyOTPRequestModel
import com.startup.graveyard.domain.models.VerifyOTPResponseModel
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

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


   @POST("/getOTP")
   suspend fun sendOtpRequest(
       @Body sendOTPRequestModel: SendOTPRequestModel
   ): Response<SendOTPReqeustResponseModel>


   @POST("/verifyOTP")
   suspend fun verifyOTP(
       @Body verifyOTPRequestModel: VerifyOTPRequestModel
): Response<VerifyOTPResponseModel>


   @DELETE("/users/{id}")
   suspend fun deleteUserAccount(
       @Path("id") uuid  : String
   ): Response<DeleteAccountResponseModel>


   @GET("/users/checkVerification")
   suspend fun checkVerificationStatsu(
        @Query("email") email : String
   ): Response<Boolean>

   @POST("/users/login")
   suspend fun doLogin(
       @Body loginUserRequestModel: LoginUserRequestModel
   ): Response<LoginUserResponseModel>


}