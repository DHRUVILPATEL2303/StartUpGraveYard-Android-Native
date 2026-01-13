package com.startup.graveyard.domain.di

import com.startup.graveyard.data.repoimpl.assetsrepoimpl.AssetRepositoryImpl
import com.startup.graveyard.data.repoimpl.authrepoimpl.AuthRepositoryImpl
import com.startup.graveyard.data.repoimpl.chatrepoimpl.ChatRepositoryImpl
import com.startup.graveyard.data.repoimpl.startuprepoimpl.StartUpRepositoryImpl
import com.startup.graveyard.domain.repo.assetrepo.AssetRepository
import com.startup.graveyard.domain.repo.authrepo.AuthRepository
import com.startup.graveyard.domain.repo.chatrepo.ChatRepository
import com.startup.graveyard.domain.repo.startuprepo.StartUpRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class DomainModule {

    @Singleton
    @Binds
    abstract fun provideAuthRepository(authRepositoryImpl: AuthRepositoryImpl) : AuthRepository

    @Singleton
    @Binds
    abstract fun provideAssetRepository(assetRepositoryImpl: AssetRepositoryImpl) : AssetRepository


    @Binds
    @Singleton
    abstract fun bindChatRepository(
        impl: ChatRepositoryImpl
    ): ChatRepository

    @Binds
    @Singleton
    abstract fun bindStartUpRepository(
        impl : StartUpRepositoryImpl
    ) : StartUpRepository
}