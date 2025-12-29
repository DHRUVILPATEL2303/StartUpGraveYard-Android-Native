package com.startup.graveyard.data.repoimpl.chatrepoimpl




import com.startup.graveyard.data.local.dao.MessageDao
import com.startup.graveyard.data.local.entity.MessageEntity
import com.startup.graveyard.data.remote.ChatApi
import com.startup.graveyard.domain.models.ChatSummary
import com.startup.graveyard.domain.models.toDomain
import com.startup.graveyard.domain.repo.chatrepo.ChatRepository
import com.startup.graveyard.utils.MessageUI
import com.startup.graveyard.utils.SendStatus
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val dao: MessageDao,
    private val api: ChatApi
) : ChatRepository {

    override suspend fun getLocalMessages(
        chatKey: String,
        limit: Int
    ): List<MessageEntity> {
        return dao.getLatest(chatKey, limit)
    }

    override suspend fun saveMessage(message: MessageEntity) {
        dao.insert(message)
    }

    override suspend fun replaceLocalMessages(
        chatKey: String,
        messages: List<MessageEntity>
    ) {
        dao.clearChat(chatKey)
        messages.forEach { dao.insert(it) }
    }

    override suspend fun fetchRemoteMessages(
        selfId: String,
        peerId: String,
        limit: Int,
        before: Long
    ): List<MessageUI> {
        return api.getMessages(
            userId = selfId,
            peerId = peerId,
            limit = limit,
            before = before
        ).data.messages.map {
            MessageUI(
                senderId = it.sender_id,
                receiverId = it.receiver_id,
                content = it.content,
                messageType = it.message_type,
                timestamp = it.messaged_at,
                isRead = it.is_read,
                sendStatus = SendStatus.SENT
            )
        }
    }

    override suspend fun loadChatSummaries(selfId: String): List<ChatSummary> {
        return dao.loadChatSummaries(selfId).map { it.toDomain() }
    }

}