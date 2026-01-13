package com.startup.graveyard.data.di

import com.startup.graveyard.common.BASE_URL
import com.startup.graveyard.data.remote.AssetApi
import com.startup.graveyard.data.remote.AuthApi
import com.startup.graveyard.data.remote.ChatApi
import com.startup.graveyard.data.remote.StartUpApi
import com.startup.graveyard.network.RestClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    @Provides
    @Singleton
    fun provideRetrofit(
        @RestClient restClient: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(restClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthApi(retrofit: Retrofit): AuthApi =
        retrofit.create(AuthApi::class.java)

    @Provides
    @Singleton
    fun provideAssetApi(retrofit: Retrofit): AssetApi =
        retrofit.create(AssetApi::class.java)


    @Provides
    @Singleton
    fun provideChatApi(retrofit: Retrofit): ChatApi =
        retrofit.create(ChatApi::class.java)


    @Provides
    @Singleton
    fun provideStartUpApi(retrofit: Retrofit) : StartUpApi =retrofit.create(StartUpApi::class.java)
}