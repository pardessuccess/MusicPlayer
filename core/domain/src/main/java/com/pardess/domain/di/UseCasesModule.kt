package com.pardess.domain.di

import com.pardess.domain.usecase.artist.ArtistUseCase
import com.pardess.domain.usecase.artist.ArtistUseCaseImpl
import com.pardess.domain.usecase.main.HomeUseCase
import com.pardess.domain.usecase.main.HomeUseCaseImpl
import com.pardess.domain.usecase.main.MainDetailUseCase
import com.pardess.domain.usecase.main.MainDetailUseCaseImpl
import com.pardess.domain.usecase.main.SearchUseCase
import com.pardess.domain.usecase.main.SearchUseCaseImpl
import com.pardess.domain.usecase.playback.PlaybackUseCase
import com.pardess.domain.usecase.playback.PlaybackUseCaseImpl
import com.pardess.domain.usecase.playlist.PlaylistUseCase
import com.pardess.domain.usecase.playlist.PlaylistUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class UseCasesModule {

    @Binds
    abstract fun bindsArtistUseCase(
        artistUseCaseImpl: ArtistUseCaseImpl
    ): ArtistUseCase

    @Binds
    abstract fun bindsHomeUseCase(
        homeUseCaseImpl: HomeUseCaseImpl
    ): HomeUseCase

    @Binds
    abstract fun bindsMainDetailUseCase(
        mainDetailUseCaseImpl: MainDetailUseCaseImpl
    ): MainDetailUseCase

    @Binds
    abstract fun bindsSearchUseCase(
        searchUseCaseImpl: SearchUseCaseImpl
    ): SearchUseCase

    @Binds
    abstract fun bindsPlaybackUseCase(
        playbackUseCaseImpl: PlaybackUseCaseImpl
    ): PlaybackUseCase

    @Binds
    abstract fun bindsPlaylistUseCase(
        playlistUseCaseImpl: PlaylistUseCaseImpl
    ): PlaylistUseCase

}