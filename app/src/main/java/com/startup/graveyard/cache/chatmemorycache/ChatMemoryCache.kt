package com.startup.graveyard.cache.chatmemorycache


import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.startup.graveyard.presentation.models.ChatItemUI
import com.startup.graveyard.utils.MessageUI
import javax.inject.Singleton

@Singleton
class ChatMemoryCache {

    private val chatMap =
        mutableMapOf<String, SnapshotStateList<MessageUI>>()

    fun getMessages(chatKey: String): SnapshotStateList<MessageUI> {
        return chatMap.getOrPut(chatKey) {
            mutableStateListOf()
        }
    }

    fun setMessages(chatKey: String, messages: List<MessageUI>) {
        val list = getMessages(chatKey)
        list.clear()
        list.addAll(messages)
    }

    fun getChatList(selfId: String): List<ChatItemUI> {
        return chatMap.mapNotNull { (key, messages) ->
            val last = messages.lastOrNull() ?: return@mapNotNull null

            val peer =
                if (last.senderId == selfId) last.receiverId
                else last.senderId

            ChatItemUI(
                peerId = peer,
                lastMessage = last.content,
                timestamp = last.timestamp,
                unreadCount = messages.count {
                    !it.isRead && it.senderId != selfId
                }
            )
        }.sortedByDescending { it.timestamp }
    }

    fun appendMessage(chatKey: String, message: MessageUI) {
        getMessages(chatKey).add(message)
    }

    fun contains(chatKey: String): Boolean {
        return chatMap[chatKey]?.isNotEmpty() == true
    }

    fun clear(chatKey: String) {
        chatMap.remove(chatKey)
    }

    fun getMessagesForAllChats(): Collection<SnapshotStateList<MessageUI>> {
        return chatMap.values
    }
}