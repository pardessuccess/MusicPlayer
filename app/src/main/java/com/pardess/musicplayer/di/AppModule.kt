package com.pardess.musicplayer.di

import com.pardess.media_service.PendingIntentProvider
import com.pardess.musicplayer.PendingIntentProviderImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providePendingIntentProvider(
        pendingIntentProvider: PendingIntentProviderImpl
    ): PendingIntentProvider = pendingIntentProvider
}