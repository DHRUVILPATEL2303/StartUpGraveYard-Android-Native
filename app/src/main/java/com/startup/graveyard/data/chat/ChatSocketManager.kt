package com.startup.graveyard.data.chat

import android.util.Log
import com.google.gson.Gson
import com.startup.graveyard.cache.chatmemorycache.ChatMemoryCache
import com.startup.graveyard.data.mappers.mesageentitymappers.toEntity
import com.startup.graveyard.domain.repo.chatrepo.ChatRepository
import com.startup.graveyard.network.WebSocketClient
import com.startup.graveyard.network.websokcet.WsSendMessage
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
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Instant

@Singleton
class ChatSocketManager @Inject constructor(
    @WebSocketClient private val client: OkHttpClient,
    private val cache: ChatMemoryCache,
    private val repository: ChatRepository
) {

    private var socket: WebSocket? = null
    private var selfId: String? = null

    fun connect(userId: String) {
        selfId = userId

        val request = Request.Builder()
            .url("ws://localhost:8080/ws/chat?user_id=$userId")
            .build()

        socket = client.newWebSocket(request, listener)
    }

    fun disconnect() {
        socket?.close(1000, "User disconnected")
        socket = null
    }


    fun sendMessage(msg: MessageUI) {
        val payload = WsSendMessage(
            receiver_id = msg.receiverId,
            content = msg.content,
            message_type = msg.messageType
        )

        socket?.send(Gson().toJson(payload))
    }

    fun sendReadReceipt(messageIds: List<String>) {
        val payload = mapOf(
            "event_type" to "message_read",
            "message_ids" to messageIds
        )
        socket?.send(Gson().toJson(payload))
    }


    private val listener = object : WebSocketListener() {

        override fun onOpen(ws: WebSocket, response: Response) {
            Log.d("WS", "Connected")
        }

        override fun onMessage(ws: WebSocket, text: String) {
            handleIncoming(text)
        }

        override fun onFailure(
            ws: WebSocket,
            t: Throwable,
            response: Response?
        ) {
            Log.e("WS", "Error", t)
        }
    }


    private fun handleIncoming(json: String) {
        val obj = JSONObject(json)

        when {
            obj.has("status") -> handleAck(obj)
            obj.has("event_type") -> handleReadReceipt(obj)
            obj.has("sender_id") -> handleIncomingMessage(obj)
        }
    }

    private fun handleAck(obj: JSONObject) {
        val status = obj.getString("status")

        cache.getMessagesForAllChats()
            .flatten()
            .lastOrNull { it.sendStatus == SendStatus.SENDING }
            ?.apply {
                sendStatus =
                    if (status == "sent") SendStatus.SENT
                    else SendStatus.QUEUED
            }
    }

    private fun handleIncomingMessage(obj: JSONObject) {
        val msg = MessageUI(
            senderId = obj.getString("sender_id"),
            receiverId = obj.getString("receiver_id"),
            content = obj.getString("content"),
            messageType = obj.getInt("message_type"),
            isRead = obj.getBoolean("is_read"),
            timestamp = java.time.Instant.parse(obj.getString("timestamp")).epochSecond,
            sendStatus = SendStatus.SENT
        )

        val key = chatKey(msg.senderId, msg.receiverId)

        cache.appendMessage(key, msg)

        CoroutineScope(Dispatchers.IO).launch {
            repository.saveMessage(msg.toEntity(key))
        }
    }

    private fun handleReadReceipt(obj: JSONObject) {
        val ids = obj.getJSONArray("message_ids")
        val readBy = obj.getString("read_by")

        val idList = (0 until ids.length()).map {
            ids.getString(it)
        }

        cache.getMessagesForAllChats()
            .flatten()
            .filter { it.senderId == selfId && it.localId in idList }
            .forEach { it.isRead = true }
    }
}