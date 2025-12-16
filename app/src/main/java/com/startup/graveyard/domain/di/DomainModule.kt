package com.startup.graveyard.domain.di

import com.startup.graveyard.data.repoimpl.authrepoimpl.AuthRepositoryImpl
import com.startup.graveyard.domain.repo.authrepo.AuthRepository
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
}