package com.startup.graveyard.domain.models

data class CreateAssetRequestModel(
    val asset_type: String,
    val description: String,
    val image_url: String,
    val is_negotiable: Boolean,
    val is_sold: Boolean,
    val price: Int,
    var startup_id: Int,
    val title: String
)