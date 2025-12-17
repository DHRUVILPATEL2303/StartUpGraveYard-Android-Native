package com.startup.graveyard.domain.models

data class UpdateUserAccountRequestModel(
    val name: String,
    val profile_pic_url: String,
    val role: String,
    var uuid: String
)