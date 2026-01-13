package com.startup.graveyard.domain.models.startups

data class GetAllStartUpsResponseModel(
    val created_at: String,
    val `data`: DataStartUps,
    val message: String,
    val success: Boolean
)

data class DataStartUps(
    val items: List<ItemStartUps>,
    val limit: Int,
    val page: Int,
    val total: Int
)

data class ItemStartUps(
    val created_at: String,
    val description: String,
    val id: Int,
    val logo_url: String,
    val name: String,
    val owner_uuid: String,
    val status: String
)

data class Startup(
    val id: Int,
    val uuid: String,
    val name: String,
    val description: String,
    val logoUrl: String,
    val status: String,
    val createdAt: String
)