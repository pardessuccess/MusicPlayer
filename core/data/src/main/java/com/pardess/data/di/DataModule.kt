package com.pardess.data.di

import android.content.Context
import com.pardess.data.repository.ManageRepositoryImpl
import com.pardess.data.repository.MediaPlayerListenerRepositoryImpl
import com.pardess.data.repository.MediaPlayerRepositoryImpl
import com.pardess.data.repository.MusicRepositoryImpl
import com.pardess.data.repository.PlaylistRepositoryImpl
import com.pardess.data.repository.PrefRepositoryImpl
import com.pardess.datastore.UserPreferences
import com.pardess.domain.repository.ManageRepository
import com.pardess.domain.repository.MediaPlayerListenerRepository
import com.pardess.domain.repository.MediaPlayerRepository
import com.pardess.domain.repository.MusicRepository
import com.pardess.domain.repository.PlaylistRepository
import com.pardess.domain.repository.PrefRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    internal abstract fun bindsManageRepository(
        managerRepository: ManageRepositoryImpl
    ): ManageRepository

    @Binds
    internal abstract fun bindsPlaylistRepository(
        playlistRepository: PlaylistRepositoryImpl
    ): PlaylistRepository

    @Binds
    internal abstract fun bindsMediaPlayerListenerRepository(
        mediaPlayerListenerRepository: MediaPlayerListenerRepositoryImpl
    ): MediaPlayerListenerRepository

    @Binds
    internal abstract fun bindsMediaPlayerRepository(
        mediaPlayerRepository: MediaPlayerRepositoryImpl
    ): MediaPlayerRepository

}

@Module
@InstallIn(SingletonComponent::class)
object MediaDiModule {

    @Provides
    @Singleton
    fun providesPrefRepository(
        userPreferences: UserPreferences
    ): PrefRepository {
        return PrefRepositoryImpl(userPreferences)
    }

    @Provides
    @Singleton
    fun providesMusicRepository(
        @ApplicationContext context: Context,
    ): MusicRepository {
        return MusicRepositoryImpl(context)
    }
}