package com.startup.graveyard.domain.models

data class CreatedAccountResponseModel(
    val created_at: String,
    val data: Data,
    val message: String,
    val success: Boolean
)

data class Data(
    val created_at: String,
    val email: String,
    val id: Int,
    val name: String,
    val profile_pic_url: String,
    val role: String,
    val uuid: String,
    val verified_at : String
)

/*
{
  "created_at": "string",
  "data": {
    "created_at": "string",
    "email": "string",
    "id": 0,
    "name": "string",
    "profile_pic_url": "string",
    "role": "string",
    "uuid": "string",
    "verified_at": "string"
  },
  "message": "string",
  "success": true
}
* */