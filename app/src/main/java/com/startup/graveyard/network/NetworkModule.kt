package com.startup.graveyard.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideCache(
        @ApplicationContext context: Context
    ): Cache =
        Cache(context.cacheDir, 20L * 1024 * 1024)

    @Provides
    @Singleton
    @OnlineInterceptor
    fun provideOnlineCacheInterceptor(): Interceptor =
        Interceptor { chain ->
            val response = chain.proceed(chain.request())
            response.newBuilder()
                .header("Cache-Control", "public, max-age=60")
                .removeHeader("Pragma")
                .build()
        }

    @Provides
    @Singleton
    @OfflineInterceptor
    fun provideOfflineCacheInterceptor(
        @ApplicationContext context: Context
    ): Interceptor =
        Interceptor { chain ->
            var request = chain.request()
            if (!NetworkUtil.hasInternet(context)) {
                request = request.newBuilder()
                    .cacheControl(
                        CacheControl.Builder()
                            .onlyIfCached()
                            .maxStale(7, TimeUnit.DAYS)
                            .build()
                    )
                    .build()
            }
            chain.proceed(request)
        }

    @Provides
    @Singleton
    @RestClient
    fun provideRestOkHttp(
        cache: Cache,
        @OfflineInterceptor offline: Interceptor,
        @OnlineInterceptor online: Interceptor
    ): OkHttpClient =
        OkHttpClient.Builder()
            .cache(cache)
            .addInterceptor(offline)
            .addNetworkInterceptor(online)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .build()
}

object NetworkUtil {
    fun hasInternet(context: Context): Boolean {
        val cm =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(network) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}