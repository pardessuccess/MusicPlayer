package com.pardess.domain.repository

import com.pardess.model.SearchHistory
import com.pardess.model.Song
import com.pardess.model.join.FavoriteSong
import com.pardess.model.join.HistorySong
import com.pardess.model.join.PlayCountSong
import kotlinx.coroutines.flow.Flow

interface ManageRepository {

    fun getFavoriteSongs(): Flow<List<FavoriteSong>>

    fun getHistorySongs(): Flow<List<HistorySong>>

    fun getPlayCountSongs(): Flow<List<PlayCountSong>>

    fun getSearchHistory(): Flow<List<SearchHistory>>

    suspend fun upsertFavorite(song: Song)

    suspend fun resetHistory()

    suspend fun upsertPlayCount(song: Song)

    suspend fun insertHistory(song: Song, timestamp: Long)

    suspend fun insertSong(song: Song)

    suspend fun saveSearchHistory(searchHistory: SearchHistory)

    suspend fun removeFavorite(songId: Long)

    suspend fun removeHistory(timestamp: Long)

    suspend fun removePlayCount(songId: Long)

    suspend fun deleteSearchHistory(searchId: Long)


}