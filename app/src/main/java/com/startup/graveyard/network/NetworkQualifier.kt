package com.startup.graveyard.network


import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class OnlineInterceptor

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class OfflineInterceptor




@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RestClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class WebSocketClient