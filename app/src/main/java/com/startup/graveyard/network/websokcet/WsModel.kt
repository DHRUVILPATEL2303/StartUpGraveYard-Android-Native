package com.startup.graveyard.network.websokcet

data class WsSendMessage(
    val receiver_id: String,
    val content: String,
    val message_type: Int = 0
)

data class WsAck(
    val message_id: String,
    val status: String // sent | queued | error
)

data class WsIncomingMessage(
    val id: String,
    val sender_id: String,
    val receiver_id: String,
    val content: String,
    val message_type: Int,
    val is_read: Boolean,
    val timestamp: String
)

data class WsReadReceipt(
    val event_type: String,
    val message_ids: List<String>,
    val read_by: String
)