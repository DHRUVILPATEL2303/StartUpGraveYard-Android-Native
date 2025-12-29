package com.startup.graveyard.data.chat

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.startup.graveyard.cache.chatmemorycache.ChatMemoryCache
import com.startup.graveyard.data.mappers.mesageentitymappers.toEntity
import com.startup.graveyard.domain.repo.chatrepo.ChatRepository
import com.startup.graveyard.network.WebSocketClient
import com.startup.graveyard.network.websokcet.WsEvent
import com.startup.graveyard.network.websokcet.WsSendMessage
import com.startup.graveyard.network.websokcet.parseWsEvent
import com.startup.graveyard.utils.MessageUI
import com.startup.graveyard.utils.SendStatus
import com.startup.graveyard.utils.chatKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatSocketManager @Inject constructor(
    @WebSocketClient private val client: OkHttpClient,
    private val cache: ChatMemoryCache,
    private val repository: ChatRepository,
    private val firebaseAuth: FirebaseAuth
) {

    private var socket: WebSocket? = null
    private var selfId: String? = null

    private val uuid = firebaseAuth.uid

    fun connect(userId: String) {
        require(userId.isNotBlank()) { "userId cannot be empty" }

        selfId = userId

        val url = "ws://grveyard-backend.onrender.com/ws/chat?user_id=$userId"
        Log.d("WS", "Connecting to $url")

        val request = Request.Builder()
            .url(url)
            .build()

        socket = client.newWebSocket(request, listener)
    }

    fun disconnect() {
        socket?.close(1000, "User disconnected")
        socket = null
        selfId = null
    }


    fun sendMessage(msg: MessageUI) {
        val payload = WsSendMessage(
            receiver_id = msg.receiverId,
            content = msg.content,
            message_type = msg.messageType
        )
        socket?.send(Gson().toJson(payload))
        Log.d("CHAT-MESSAGE-SEND",payload.toString())
    }

    fun sendReadReceipt(messageIds: List<String>) {
        if (messageIds.isEmpty()) return

        val payload = mapOf(
            "event_type" to "message_read",
            "message_ids" to messageIds
        )
        socket?.send(Gson().toJson(payload))
    }
    private fun resendQueuedMessages() {
        cache.getQueuedMessages().forEach { msg ->
            val payload = WsSendMessage(
                receiver_id = msg.receiverId,
                content = msg.content,
                message_type = msg.messageType
            )
            socket?.send(Gson().toJson(payload))
        }
    }

    private val listener = object : WebSocketListener() {

        override fun onOpen(webSocket: WebSocket, response: Response) {
            Log.d("WS", "Connected as $selfId")
            resendQueuedMessages()
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            when (val event = parseWsEvent(text)) {


                is WsEvent.IncomingMessage -> {
                    val msg = event.message
                    Log.d("INCOMING SOCKET MESSAGE", msg.toString())
                    val key = chatKey(msg.senderId, msg.receiverId)

                    CoroutineScope(Dispatchers.IO).launch {
                        repository.saveMessage(msg.toEntity(key))
                        cache.appendMessage(key, msg, selfId!!)
                    }
                }

                is WsEvent.Ack -> {
                    handleAck(event.messageId, event.status)
                }

                is WsEvent.ReadReceipt -> {
                    handleReadReceipt(event.ids)
                }

                is WsEvent.Unknown -> {
                    Log.w("WS", "Unknown payload: ${event.raw}")
                }
            }
        }

        override fun onFailure(
            webSocket: WebSocket,
            t: Throwable,
            response: Response?
        ) {
            Log.e("WS", "Socket error", t)
            cache.markSendingAsQueued()
        }
    }


    private fun handleAck(messageId: String, status: String) {
        val newStatus =
            if (status == "sent") SendStatus.SENT
            else SendStatus.QUEUED

        cache.updateMessageStatus(messageId, newStatus)
    }
    private fun handleReadReceipt(ids: List<String>) {
        cache.markMessagesRead(ids)
    }
}