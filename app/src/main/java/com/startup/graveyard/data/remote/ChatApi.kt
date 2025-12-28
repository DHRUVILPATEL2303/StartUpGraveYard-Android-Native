package com.startup.graveyard.data.remote

import retrofit2.http.GET
import retrofit2.http.Query


data class MessagesResponse(
    val success: Boolean,
    val message: String,
    val data: MessagesData,
    val created_at: String
)

data class MessagesData(
    val messages: List<MessageDto>,
    val count: Int
)

data class MessageDto(
    val sender_id: String,
    val receiver_id: String,
    val content: String,
    val message_type: Int,
    val is_read: Boolean,
    val messaged_at: Long
)


interface ChatApi {

    @GET("messages")
    suspend fun getMessages(
        @Query("user_id") userId: String,
        @Query("peer_id") peerId: String,
        @Query("limit") limit: Int = 50,
        @Query("before") before: Long
    ): MessagesResponse
}