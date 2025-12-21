package com.startup.graveyard.domain.models.assets


data class Asset(
    val id: Int,
    val title: String,
    val description: String,
    val assetType: String,
    val price: Int,
    val imageUrl: String,
    val isSold: Boolean,
    val isActive: Boolean,
    val isNegotiable: Boolean,
    val userUuid: String,
    val createdAt: String
)