package com.startup.graveyard.data.di

import android.content.Context
import androidx.room.Room
import com.startup.graveyard.data.local.dao.MessageDao
import com.startup.graveyard.data.local.databse.ChatDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): ChatDatabase {
        return Room.databaseBuilder(
            context,
            ChatDatabase::class.java,
            "graveyard_chat.db"
        ).build()
    }

    @Provides
    fun provideMessageDao(
        db: ChatDatabase
    ): MessageDao = db.messageDao()
}