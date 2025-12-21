package com.startup.graveyard.domain.models

data class LoginUserResponseModel(
    val created_at: String,
    val data: DataLogin,
    val message: String,
    val success: Boolean
)

data class DataLogin(
    val created_at: String,
    val email: String,
    val id: Int,
    val name: String,
    val profile_pic_url: String,
    val role: String,
    val uuid: String,
    val verified_at: String
)