package com.startup.graveyard.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.startup.graveyard.data.local.entity.ChatSummaryEntity
import com.startup.graveyard.data.local.entity.MessageEntity
import com.startup.graveyard.domain.models.ChatSummary

@Dao
interface MessageDao {

    @Query("""
        SELECT * FROM messages
        WHERE chatKey = :chatKey
        ORDER BY timestamp ASC
        LIMIT :limit
    """)
    suspend fun getLatest(
        chatKey: String,
        limit: Int
    ): List<MessageEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(message: MessageEntity)

    @Query("DELETE FROM messages WHERE chatKey = :chatKey")
    suspend fun clearChat(chatKey: String)

    @Query("""
        SELECT 
            chatKey,
            CASE 
                WHEN senderId = :selfId THEN receiverId
                ELSE senderId
            END AS peerId,
            content AS lastMessage,
            MAX(timestamp) AS timestamp,
            SUM(
                CASE 
                    WHEN isRead = 0 AND receiverId = :selfId THEN 1
                    ELSE 0
                END
            ) AS unreadCount
        FROM messages
        GROUP BY chatKey
        ORDER BY timestamp DESC
    """)
    suspend fun loadChatSummaries(selfId: String): List<ChatSummaryEntity>
}