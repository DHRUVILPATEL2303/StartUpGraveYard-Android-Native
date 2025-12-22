package com.startup.graveyard.network


import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class OnlineInterceptor

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class OfflineInterceptor