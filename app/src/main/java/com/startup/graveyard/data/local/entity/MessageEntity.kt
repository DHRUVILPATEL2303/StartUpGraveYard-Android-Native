package com.startup.graveyard.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.startup.graveyard.utils.MessageUI
import com.startup.graveyard.utils.SendStatus

@Entity(
    tableName = "messages",
    indices = [
        Index("chatKey"),
        Index("timestamp")
    ]
)
data class MessageEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val chatKey: String,

    val senderId: String,
    val receiverId: String,
    val content: String,
    val messageType: Int,

    val timestamp: Long,
    val isRead: Boolean
)

