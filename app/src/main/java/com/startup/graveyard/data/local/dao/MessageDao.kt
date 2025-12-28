package com.startup.graveyard.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.startup.graveyard.data.local.entity.MessageEntity

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

    @Query("""
        DELETE FROM messages
        WHERE chatKey = :chatKey
    """)
    suspend fun clearChat(chatKey: String)
}