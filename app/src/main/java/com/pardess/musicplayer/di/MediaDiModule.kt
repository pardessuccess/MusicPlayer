package com.pardess.musicplayer.di

import com.pardess.musicplayer.domain.usecase.artist.ArtistUseCase
import com.pardess.musicplayer.domain.usecase.artist.ArtistUseCaseImpl
import com.pardess.musicplayer.domain.usecase.main.MainDetailUseCase
import com.pardess.musicplayer.domain.usecase.main.MainDetailUseCaseImpl
import com.pardess.musicplayer.domain.usecase.main.MainUseCase
import com.pardess.musicplayer.domain.usecase.main.MainUseCaseImpl
import com.pardess.musicplayer.domain.usecase.main.SearchUseCase
import com.pardess.musicplayer.domain.usecase.main.SearchUseCaseImpl
import com.pardess.musicplayer.domain.usecase.playback.media_player.MediaPlayerListenerUseCase
import com.pardess.musicplayer.domain.usecase.playback.media_player.MediaPlayerListenerUseCaseImpl
import com.pardess.musicplayer.domain.usecase.playback.media_player.MediaPlayerUseCase
import com.pardess.musicplayer.domain.usecase.playback.media_player.MediaPlayerUseCaseImpl
import com.pardess.musicplayer.domain.usecase.playlist.PlaylistUseCase
import com.pardess.musicplayer.domain.usecase.playlist.PlaylistUseCaseImpl
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
        mediaPlayerListenerUseCaseImpl: MediaPlayerListenerUseCaseImpl,
    ): MediaPlayerListenerUseCase


    @Binds
    abstract fun bindsPlaylistUseCase(
        playlistUseCaseImpl: PlaylistUseCaseImpl
    ): PlaylistUseCase

    @Binds
    abstract fun bindsArtistUseCase(
        artistUseCaseImpl: ArtistUseCaseImpl
    ): ArtistUseCase

    @Binds
    abstract fun bindsMainUseCase(
        mainUseCaseImpl: MainUseCaseImpl
    ): MainUseCase

    @Binds
    abstract fun bindsMainDetailUseCase(
        mainDetailUseCaseImpl: MainDetailUseCaseImpl
    ): MainDetailUseCase

    @Binds
    abstract fun bindsSearchUseCase(
        searchUseCaseImpl: SearchUseCaseImpl
    ): SearchUseCase

}