package com.pardess.musicplayer.di

import com.pardess.musicplayer.domain.usecase.media_player.MediaPlayerListenerUseCase
import com.pardess.musicplayer.domain.usecase.media_player.MediaPlayerListenerUseCaseImpl
import com.pardess.musicplayer.domain.usecase.media_player.MediaPlayerUseCase
import com.pardess.musicplayer.domain.usecase.media_player.MediaPlayerUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class MediaDiModule {

    @Binds
    abstract fun bindsMediaPlayerUseCase(
        mediaServiceUseCaseImpl: MediaPlayerUseCaseImpl
    ): MediaPlayerUseCase

    @Binds
    abstract fun bindsMediaPlayerListenerUseCase(
        mediaPlayerListenerUseCaseImpl: MediaPlayerListenerUseCaseImpl
    ): MediaPlayerListenerUseCase
}