package com.startup.graveyard.data.local.databse

import androidx.room.Database
import androidx.room.RoomDatabase
import com.startup.graveyard.data.local.dao.MessageDao
import com.startup.graveyard.data.local.entity.ChatSummaryEntity
import com.startup.graveyard.data.local.entity.MessageEntity

@Database(
    entities = [
        MessageEntity::class,
        ChatSummaryEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class ChatDatabase : RoomDatabase() {
    abstract fun messageDao(): MessageDao
}
