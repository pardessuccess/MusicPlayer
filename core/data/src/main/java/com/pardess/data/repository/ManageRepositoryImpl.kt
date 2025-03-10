package com.pardess.data.repository

import com.pardess.data.mapper.toDomain
import com.pardess.data.mapper.toEntity
import com.pardess.data.mapper.toSearchHistory
import com.pardess.database.dao.FavoriteDao
import com.pardess.database.dao.HistoryDao
import com.pardess.database.dao.PlayCountDao
import com.pardess.database.dao.SearchDao
import com.pardess.database.dao.SongDao
import com.pardess.database.entity.HistoryEntity
import com.pardess.domain.repository.ManageRepository
import com.pardess.model.SearchHistory
import com.pardess.model.Song
import com.pardess.model.join.FavoriteSong
import com.pardess.model.join.HistorySong
import com.pardess.model.join.PlayCountSong
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
        return favoriteDao.getFavoriteSongs().map { it.map { it.toDomain() } }
    }

    override fun getHistorySongs(): Flow<List<HistorySong>> {
        return historyDao.getHistorySongs().map { it.map { it.toDomain() } }
    }

    override fun getPlayCountSongs(): Flow<List<PlayCountSong>> {
        return playCountDao.getMostPlayedSongs().map { it.map { it.toDomain() } }
    }

    override fun getSearchHistory(): Flow<List<SearchHistory>> {
        return searchDao.getSearchHistory().map { searchHistories ->
            searchHistories.map { entity ->
                entity.toSearchHistory()
            }
        }
    }

    override suspend fun upsertFavorite(song: Song) {
        insertSong(song)
        favoriteDao.increaseOrInsertFavorite(song.id)
    }

    override suspend fun upsertPlayCount(song: Song) {
        insertSong(song)
        playCountDao.increaseOrInsertPlayCount(song.id)
    }

    override suspend fun insertHistory(song: Song, timestamp: Long) {
        insertSong(song)
        historyDao.insertHistory(
            history = HistoryEntity(
                songId = song.id,
                timestamp = timestamp
            )
        )
    }

    override suspend fun insertSong(song: Song) {
        songDao.insertSongEntity(song.toEntity())
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