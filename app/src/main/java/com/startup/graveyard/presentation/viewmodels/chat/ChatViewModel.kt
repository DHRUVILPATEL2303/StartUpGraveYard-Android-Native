package com.startup.graveyard.presentation.viewmodels.chat

import androidx.lifecycle.ViewModel
import com.startup.graveyard.cache.chatmemorycache.ChatMemoryCache
import com.startup.graveyard.data.chat.ChatSocketManager
import com.startup.graveyard.utils.MessageUI
import com.startup.graveyard.utils.SendStatus
import com.startup.graveyard.utils.chatKey
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val socket: ChatSocketManager,
    private val cache: ChatMemoryCache
) : ViewModel() {

    fun connect(userId: String) {
        socket.connect(userId)
    }

    fun disconnect() {
        socket.disconnect()
    }

    fun send(self: String, peer: String, text: String) {
        val key = chatKey(self, peer)

        val msg = MessageUI(
            senderId = self,
            receiverId = peer,
            content = text,
            messageType = 0,
            timestamp = System.currentTimeMillis() / 1000,
            isRead = false,
            sendStatus = SendStatus.SENDING
        )

        cache.appendMessage(key, msg)
        socket.sendMessage(msg)
    }

    fun markRead(chatKey: String) {
        val ids = cache.getMessages(chatKey)
            .filter { !it.isRead }
            .map { it.localId }

        socket.sendReadReceipt(ids)
    }
}