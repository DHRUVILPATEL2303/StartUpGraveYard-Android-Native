package com.startup.graveyard.domain.models

data class VerifyOTPResponseModel(
    val created_at: String,
    val data: VerifyData,
    val message: String,
    val success: Boolean
)


data class VerifyData(
    val verified: Boolean
)