package com.startup.graveyard.domain.models

data class UserAccountResponseModel(
    val created_at: String,
    val data: DataModel,
    val message: String,
    val success: Boolean
)


data class DataModel(
    val created_at: String,
    val email: String,
    val id: Int,
    val name: String,
    val profile_pic_url: String,
    val role: String,
    val uuid: String
)