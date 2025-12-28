package com.startup.graveyard.utils

import java.util.UUID

fun chatKey(userA: String, userB: String): String {
    return if (userA < userB) {
        "${userA}_$userB"
    } else {
        "${userB}_$userA"
    }
}

data class MessageUI(
    val localId: String = UUID.randomUUID().toString(),

    val senderId: String,
    val receiverId: String,
    val content: String,
    val messageType: Int,
    val timestamp: Long,
    var isRead: Boolean,

    var sendStatus: SendStatus
)

enum class SendStatus {
    SENDING,
    SENT,
    QUEUED,
    FAILED
}