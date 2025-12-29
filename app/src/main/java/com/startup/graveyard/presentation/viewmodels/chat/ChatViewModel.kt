package com.startup.graveyard.presentation.viewmodels.chat

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.startup.graveyard.cache.chatmemorycache.ChatMemoryCache
import com.startup.graveyard.data.chat.ChatSocketManager
import com.startup.graveyard.data.mappers.mesageentitymappers.toEntity
import com.startup.graveyard.domain.models.ChatSummary
import com.startup.graveyard.domain.repo.chatrepo.ChatRepository
import com.startup.graveyard.presentation.models.ChatItemUI
import com.startup.graveyard.utils.MessageUI
import com.startup.graveyard.utils.SendStatus
import com.startup.graveyard.utils.chatKey
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val socket: ChatSocketManager,
    private val cache: ChatMemoryCache,
    private val chatRepository: ChatRepository,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val selfId: String? = firebaseAuth.uid


    private val _chatList = mutableStateOf<List<ChatSummary>>(emptyList())
    val chatList = _chatList
    init {
        selfId?.let {
            preloadChats(it)
            loadChatList(it)
            connect(it)
        }
    }

    fun connect(selfId: String) {
        socket.connect(selfId)
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

        cache.appendMessage(key, msg, self)

        viewModelScope.launch {
            chatRepository.saveMessage(msg.toEntity(key))
        }

        socket.sendMessage(msg)
    }

    fun chatList(): List<ChatItemUI> {
            return selfId?.let {

                   cache.getChatList(selfId)} ?: emptyList()




    }

    private fun loadChatList(selfId: String) {
        viewModelScope.launch {
            val chats = chatRepository.loadChatSummaries(selfId)

            chats.forEach { cache.seedChat(it) }

            _chatList.value = chats
        }
    }

    fun messages(chatKey: String) =
        cache.getMessages(chatKey)

    fun markRead(chatKey: String) {
        val ids = cache.getMessages(chatKey)
            .filter { !it.isRead && it.serverId != null }
            .map { it.serverId!! }

        if (ids.isNotEmpty()) {
            socket.sendReadReceipt(ids)
        }
    }

  fun preloadChats(selfId: String) {
        viewModelScope.launch {
            chatRepository.loadChatSummaries(selfId)
                .forEach { cache.seedChat(it) }
        }
    }
}