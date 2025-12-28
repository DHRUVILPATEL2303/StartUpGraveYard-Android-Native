package com.startup.graveyard.cache.chatmemorycache


import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
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