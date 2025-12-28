package com.startup.graveyard.network.websokcet


import com.startup.graveyard.utils.MessageUI
import com.startup.graveyard.utils.SendStatus
import org.json.JSONObject
import java.time.Instant

sealed class WsEvent {
    data class IncomingMessage(val message: MessageUI) : WsEvent()
    data class Ack(val messageId: String, val status: String) : WsEvent()
    data class ReadReceipt(val ids: List<String>, val readBy: String) : WsEvent()
    data class Unknown(val raw: String) : WsEvent()
}

fun parseWsEvent(json: String): WsEvent {
    val obj = JSONObject(json)

    return when {
        obj.has("status") && obj.has("message_id") -> {
            WsEvent.Ack(
                messageId = obj.getString("message_id"),
                status = obj.getString("status")
            )
        }

        obj.optString("event_type") == "message_read" -> {
            val idsArray = obj.getJSONArray("message_ids")
            val ids = List(idsArray.length()) {
                idsArray.getString(it)
            }
            WsEvent.ReadReceipt(
                ids = ids,
                readBy = obj.getString("read_by")
            )
        }

        obj.has("sender_id") && obj.has("receiver_id") -> {
            WsEvent.IncomingMessage(
                MessageUI(
                    serverId = obj.getString("id"),
                    senderId = obj.getString("sender_id"),
                    receiverId = obj.getString("receiver_id"),
                    content = obj.getString("content"),
                    messageType = obj.getInt("message_type"),
                    isRead = obj.getBoolean("is_read"),
                    timestamp = Instant.parse(
                        obj.getString("timestamp")
                    ).epochSecond,
                    sendStatus = SendStatus.SENT
                )
            )
        }

        else -> WsEvent.Unknown(json)
    }
}