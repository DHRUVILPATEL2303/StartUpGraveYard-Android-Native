package com.startup.graveyard.domain.models.getallassets

data class GetAllAssetsOfSpecificUserResponseModel(
    val created_at: String,
    val data: DataSpecific,
    val message: String,
    val success: Boolean
)

data class ItemSpecific(
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

data class DataSpecific(
    val items: List<ItemSpecific>,
    val limit: Int,
    val page: Int,
    val total: Int
)