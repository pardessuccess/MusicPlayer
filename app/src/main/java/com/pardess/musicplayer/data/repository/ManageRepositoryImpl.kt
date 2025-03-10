package com.pardess.musicplayer.data.repository

import com.pardess.musicplayer.data.datasource.database.dao.FavoriteDao
import com.pardess.musicplayer.data.datasource.database.dao.HistoryDao
import com.pardess.musicplayer.data.datasource.database.dao.PlayCountDao
import com.pardess.musicplayer.data.datasource.database.dao.SearchDao
import com.pardess.musicplayer.data.datasource.database.dao.SongDao
import com.pardess.musicplayer.data.entity.HistoryEntity
import com.pardess.musicplayer.data.entity.SongEntity
import com.pardess.musicplayer.data.entity.join.FavoriteSong
import com.pardess.musicplayer.data.entity.join.HistorySong
import com.pardess.musicplayer.data.entity.join.PlayCountSong
import com.pardess.musicplayer.data.mapper.toEntity
import com.pardess.musicplayer.data.mapper.toSearchHistory
import com.pardess.musicplayer.domain.model.SearchHistory
import com.pardess.musicplayer.domain.repository.ManageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ManageRepositoryImpl @Inject constructor(
    private val favoriteDao: FavoriteDao,
    private val historyDao: HistoryDao,
    private val playCountDao: PlayCountDao,
    private val songDao: SongDao,
    private val searchDao: SearchDao
) : ManageRepository {
    override fun getFavoriteSongs(): Flow<List<FavoriteSong>> {
        return favoriteDao.getFavoriteSongs()
    }

    override fun getHistorySongs(): Flow<List<HistorySong>> {
        return historyDao.getHistorySongs()
    }

    override fun getPlayCountSongs(): Flow<List<PlayCountSong>> {
        return playCountDao.getMostPlayedSongs()
    }

    override fun getSearchHistory(): Flow<List<SearchHistory>> {
        return searchDao.getSearchHistory().map { searchHistories ->
            searchHistories.map { entity ->
                entity.toSearchHistory()
            }
        }
    }

    override suspend fun upsertFavorite(songEntity: SongEntity) {
        insertSongEntity(songEntity)
        favoriteDao.increaseOrInsertFavorite(songEntity.id)
    }

    override suspend fun upsertPlayCount(songEntity: SongEntity) {
        insertSongEntity(songEntity)
        playCountDao.increaseOrInsertPlayCount(songEntity.id)
    }

    override suspend fun insertHistory(songEntity: SongEntity, timestamp: Long) {
        insertSongEntity(songEntity)
        historyDao.insertHistory(
            history = HistoryEntity(
                songId = songEntity.id,
                timestamp = timestamp
            )
        )
    }

    override suspend fun insertSongEntity(songEntity: SongEntity) {
        songDao.insertSongEntity(songEntity)
    }

    override suspend fun saveSearchHistory(searchHistory: SearchHistory) {
        searchDao.insertSearchHistory(searchHistory.toEntity())
    }

    override suspend fun removeFavorite(songId: Long) {
        favoriteDao.deleteFavoriteBySongId(songId)
    }

    override suspend fun resetHistory() {
        historyDao.deleteAllHistory()
    }

    override suspend fun removeHistory(timestamp: Long) {
        historyDao.deleteHistoryByTimestamp(timestamp)
    }

    override suspend fun removePlayCount(songId: Long) {
        playCountDao.deletePlayCountBySongId(songId)
    }

    override suspend fun deleteSearchHistory(searchId: Long) {
        searchDao.deleteSearchHistory(searchId)
    }
}