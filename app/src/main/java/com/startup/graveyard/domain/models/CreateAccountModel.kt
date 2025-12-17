package com.startup.graveyard.domain.models


data class CreateAccountModel(
    val email : String,
    val password : String,
    val name : String,
    val profile_pic_url : String,
    val role : String,
    var uuid : String

)

/*

{
  "email": "string",
  "name": "string",
  "password": "string",
  "profile_pic_url": "string",
  "role": "string",
  "uuid": "string"
}
* */
