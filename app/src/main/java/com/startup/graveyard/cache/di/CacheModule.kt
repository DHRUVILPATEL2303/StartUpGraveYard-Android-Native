package com.startup.graveyard.cache.di

import com.startup.graveyard.cache.chatmemorycache.ChatMemoryCache
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CacheModule {

    @Provides
    @Singleton
    fun provideChatMemoryCache(): ChatMemoryCache {
        return ChatMemoryCache()
    }
}