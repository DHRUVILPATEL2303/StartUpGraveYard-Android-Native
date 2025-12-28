package com.startup.graveyard.data.mappers.mesageentitymappers

import com.startup.graveyard.data.local.entity.MessageEntity
import com.startup.graveyard.utils.MessageUI
import com.startup.graveyard.utils.SendStatus


fun MessageEntity.toUI(): MessageUI {
    return MessageUI(
        senderId = senderId,
        receiverId = receiverId,
        content = content,
        messageType = messageType,
        timestamp = timestamp,
        isRead = isRead,
        sendStatus = SendStatus.SENT
    )
}

fun MessageUI.toEntity(chatKey: String): MessageEntity {
    return MessageEntity(
        chatKey = chatKey,
        senderId = senderId,
        receiverId = receiverId,
        content = content,
        messageType = messageType,
        timestamp = timestamp,
        isRead = isRead
    )
}