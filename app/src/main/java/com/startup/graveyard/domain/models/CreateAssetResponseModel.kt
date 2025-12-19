package com.startup.graveyard.domain.models

data class CreateAssetResponseModel(
    val created_at: String,
    val data: DataRequest,
    val message: String,
    val success: Boolean
)

data class DataRequest(
    val asset_type: String,
    val created_at: String,
    val description: String,
    val id: Int,
    val image_url: String,
    val is_active: Boolean,
    val is_negotiable: Boolean,
    val is_sold: Boolean,
    val price: Int,
    val startup_id: Int,
    val title: String
)