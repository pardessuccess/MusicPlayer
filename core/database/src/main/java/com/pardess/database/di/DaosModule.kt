package com.pardess.database.di

import android.content.Context
import com.pardess.database.MusicDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DaosModule {

    @Provides
    fun providesSongDao(database: MusicDatabase) = database.songDao()

    @Provides
    fun providesPlaylistDao(database: MusicDatabase) = database.playlistDao()

    @Provides
    fun providesHistoryDao(database: MusicDatabase) = database.historyDao()

    @Provides
    fun providesFavoriteDao(database: MusicDatabase) = database.favoriteDao()

    @Provides
    fun providesPlayCountDao(database: MusicDatabase) = database.playCountDao()

    @Provides
    fun providesSearchDao(database: MusicDatabase) = database.searchDao()



}