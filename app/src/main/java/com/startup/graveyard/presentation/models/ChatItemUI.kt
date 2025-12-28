package com.startup.graveyard.presentation.models

data class ChatItemUI(
    val peerId: String,
    val lastMessage: String,
    val timestamp: Long,
    val unreadCount: Int
)