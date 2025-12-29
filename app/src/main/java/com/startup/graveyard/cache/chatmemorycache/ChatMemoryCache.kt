package com.startup.graveyard.cache.chatmemorycache


import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.startup.graveyard.domain.models.ChatSummary
import com.startup.graveyard.presentation.models.ChatItemUI
import com.startup.graveyard.utils.MessageUI
import com.startup.graveyard.utils.SendStatus
import javax.inject.Singleton

@Singleton
class ChatMemoryCache {


    private val chatSummaries =
        mutableMapOf<String, ChatSummary>()

    fun seedChat(summary: ChatSummary) {
        chatMap.putIfAbsent(
            summary.chatKey,
            mutableStateListOf(
                MessageUI(
                    senderId = summary.peerId,
                    receiverId = "",
                    content = summary.lastMessage,
                    timestamp = summary.timestamp,
                    messageType = 0,
                    isRead = summary.unreadCount == 0,
                    sendStatus = SendStatus.SENT
                )
            )
        )
    }

    fun updateFromMessage(chatKey: String, msg: MessageUI, selfId: String) {
        val peer =
            if (msg.senderId == selfId) msg.receiverId else msg.senderId

        val unread =
            if (msg.senderId != selfId && !msg.isRead) 1 else 0

        chatSummaries[chatKey] = ChatSummary(
            chatKey = chatKey,
            peerId = peer,
            lastMessage = msg.content,
            timestamp = msg.timestamp,
            unreadCount = unread
        )
    }

    fun getChatList(selfId: String): List<ChatItemUI> {
        return chatSummaries.values
            .sortedByDescending { it.timestamp }
            .map {
                ChatItemUI(
                    peerId = it.peerId,
                    lastMessage = it.lastMessage,
                    timestamp = it.timestamp,
                    unreadCount = it.unreadCount
                )
            }
    }


    private val chatMap =
        mutableMapOf<String, SnapshotStateList<MessageUI>>()

    fun getMessages(chatKey: String): SnapshotStateList<MessageUI> {
        return chatMap.getOrPut(chatKey) { mutableStateListOf() }
    }

    fun appendMessage(chatKey: String, message: MessageUI, selfId: String) {
        getMessages(chatKey).add(message)
        updateFromMessage(chatKey, message, selfId)
    }

    fun setMessages(chatKey: String, messages: List<MessageUI>, selfId: String) {
        val list = getMessages(chatKey)
        list.clear()
        list.addAll(messages)

        messages.lastOrNull()?.let {
            updateFromMessage(chatKey, it, selfId)
        }
    }

    fun getMessagesForAllChats(): Collection<SnapshotStateList<MessageUI>> =
        chatMap.values
}