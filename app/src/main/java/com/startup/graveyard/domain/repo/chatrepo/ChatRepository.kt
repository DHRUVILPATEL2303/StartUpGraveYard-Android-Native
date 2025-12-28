package com.startup.graveyard.domain.repo.chatrepo


import com.startup.graveyard.data.local.entity.MessageEntity
import com.startup.graveyard.utils.MessageUI

interface ChatRepository {

    suspend fun getLocalMessages(
        chatKey: String,
        limit: Int
    ): List<MessageEntity>

    suspend fun saveMessage(
        message: MessageEntity
    )

    suspend fun replaceLocalMessages(
        chatKey: String,
        messages: List<MessageEntity>
    )

    suspend fun fetchRemoteMessages(
        selfId: String,
        peerId: String,
        limit: Int,
        before: Long
    ): List<MessageUI>
}