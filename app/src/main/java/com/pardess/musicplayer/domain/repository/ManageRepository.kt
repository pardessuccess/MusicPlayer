package com.pardess.musicplayer.domain.repository

import com.pardess.musicplayer.data.entity.SongEntity
import com.pardess.musicplayer.data.entity.join.FavoriteSong
import com.pardess.musicplayer.data.entity.join.HistorySong
import com.pardess.musicplayer.data.entity.join.PlayCountSong
import kotlinx.coroutines.flow.Flow

interface ManageRepository {

    fun getFavoriteSongs(): Flow<List<FavoriteSong>>

    fun getHistorySongs(): Flow<List<HistorySong>>

    fun getPlayCountSongs(): Flow<List<PlayCountSong>>

    suspend fun upsertFavorite(songEntity: SongEntity)

    suspend fun upsertPlayCount(songEntity: SongEntity)

    suspend fun insertHistory(songEntity: SongEntity, timestamp: Long)

    suspend fun insertSongEntity(songEntity: SongEntity)

    suspend fun removeFavorite(songId: Long)

    suspend fun removeHistory(timestamp: Long)

    suspend fun removePlayCount(songId: Long)


}