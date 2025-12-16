package com.startup.graveyard.data.remote

import com.startup.graveyard.domain.models.CreateAccountModel
import com.startup.graveyard.domain.models.CreateAccountResponseModel
import retrofit2.Response
import retrofit2.http.POST

interface AuthApi {

    @POST
    fun createAccount(
        createAccountModel: CreateAccountModel
    ) : Response<CreateAccountResponseModel>
}