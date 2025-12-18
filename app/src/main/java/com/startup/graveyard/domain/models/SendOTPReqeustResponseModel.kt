package com.startup.graveyard.domain.models

data class SendOTPReqeustResponseModel(
    val created_at: String,
    val message: String,
    val success: Boolean
)