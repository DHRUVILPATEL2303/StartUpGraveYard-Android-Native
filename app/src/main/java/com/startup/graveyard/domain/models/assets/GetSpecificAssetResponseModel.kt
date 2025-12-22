package com.startup.graveyard.domain.models.assets


data class GetSpecificAssetResponseModel(
    val created_at: String,
    val `data`: DataSpecificAsset,
    val message: String,
    val success: Boolean
)

data class DataSpecificAsset(
    val asset_type: String,
    val created_at: String,
    val description: String,
    val id: Int,
    val image_url: String,
    val is_active: Boolean,
    val is_negotiable: Boolean,
    val is_sold: Boolean,
    val price: Int,
    val title: String,
    val user_uuid: String
)