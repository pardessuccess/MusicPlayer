package com.pardess.musicplayer.data.datasource.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.pardess.musicplayer.data.datasource.database.dao.FavoriteDao
import com.pardess.musicplayer.data.entity.PlaylistSong
import com.pardess.musicplayer.data.datasource.database.dao.HistoryDao
import com.pardess.musicplayer.data.datasource.database.dao.PlayCountDao
import com.pardess.musicplayer.data.datasource.database.dao.PlaylistDao
import com.pardess.musicplayer.data.datasource.database.dao.SearchDao
import com.pardess.musicplayer.data.datasource.database.dao.SongDao
import com.pardess.musicplayer.data.entity.FavoriteEntity
import com.pardess.musicplayer.data.entity.HistoryEntity
import com.pardess.musicplayer.data.entity.PlayCountEntity
import com.pardess.musicplayer.data.entity.PlaylistEntity
import com.pardess.musicplayer.data.entity.SearchHistoryEntity
import com.pardess.musicplayer.data.entity.SongEntity

@Database(
    entities = [
        PlaylistEntity::class, PlaylistSong::class, HistoryEntity::class, PlayCountEntity::class,
        FavoriteEntity::class, SongEntity::class, SearchHistoryEntity::class
    ], version = 2, exportSchema = false
)
abstract class MusicDatabase : RoomDatabase() {

    abstract fun songDao(): SongDao
    abstract fun historyDao(): HistoryDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun playCountDao(): PlayCountDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun searchDao(): SearchDao

    companion object {
        const val NAME = "music_db"

    }
}

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // ✅ 새로운 search_history 테이블 추가
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS search_history (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                type INTEGER NOT NULL,
                image TEXT,
                text TEXT NOT NULL,
                timestamp INTEGER NOT NULL
            )
        """.trimIndent()
        )
    }
}
