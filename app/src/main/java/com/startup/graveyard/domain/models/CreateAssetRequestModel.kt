package com.startup.graveyard.domain.models

data class CreateAssetRequestModel(
    val asset_type: String,
    val description: String,
    val image_url: String,
    val is_negotiable: Boolean,
    val is_sold: Boolean,
    val price: Int,
    var user_uuid : String,
    val title: String
)


/**
 * {
 *   "asset_type": "string",
 *   "description": "string",
 *   "image_url": "string",
 *   "is_negotiable": true,
 *   "is_sold": true,
 *   "price": 0,
 *   "title": "string",
 *   "user_uuid": "string"
 * }
 * */