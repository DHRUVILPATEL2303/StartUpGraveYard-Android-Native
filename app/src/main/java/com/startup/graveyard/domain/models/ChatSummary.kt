package com.startup.graveyard.domain.models

import com.startup.graveyard.data.local.entity.ChatSummaryEntity

data class ChatSummary(
    val chatKey: String,
    val peerId: String,
    val lastMessage: String,
    val timestamp: Long,
    val unreadCount: Int
)

fun ChatSummaryEntity.toDomain() = ChatSummary(
    chatKey = chatKey,
    peerId = peerId,
    lastMessage = lastMessage,
    timestamp = timestamp,
    unreadCount = unreadCount
)