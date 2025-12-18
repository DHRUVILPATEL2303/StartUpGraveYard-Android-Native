package com.startup.graveyard.domain.models

data class VerifyOTPRequestModel(
    val code : String,
    var email : String
)