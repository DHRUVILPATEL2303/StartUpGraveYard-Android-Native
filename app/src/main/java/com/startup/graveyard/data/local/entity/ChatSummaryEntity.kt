package com.startup.graveyard.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_summary")
data class ChatSummaryEntity(
    @PrimaryKey
    val chatKey: String,
    val peerId: String,
    val lastMessage: String,
    val timestamp: Long,
    val unreadCount: Int
)