package com.pardess.database.di

import android.app.Application
import androidx.room.Room
import com.pardess.database.MIGRATION_1_2
import com.pardess.database.MusicDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object DatabaseModule {
    @Provides
    @Singleton
    fun providesDatabase(app: Application): MusicDatabase {
        return Room.databaseBuilder(app, MusicDatabase::class.java, MusicDatabase.NAME)
            .addMigrations(MIGRATION_1_2)
            .build()
    }

}