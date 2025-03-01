package com.pardess.musicplayer.data.repository

import com.pardess.musicplayer.data.datasource.database.dao.SearchDao
import com.pardess.musicplayer.data.entity.SearchHistoryEntity
import com.pardess.musicplayer.data.mapper.toEntity
import com.pardess.musicplayer.data.mapper.toSearchHistory
import com.pardess.musicplayer.domain.model.SearchHistory
import com.pardess.musicplayer.domain.repository.SearchRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SearchRepositoryImpl @Inject constructor(
    private val searchDao: SearchDao
) : SearchRepository {
    override suspend fun saveSearchHistory(searchHistory: SearchHistory){
        searchDao.insertSearchHistory(searchHistory.toEntity())
    }

    override suspend fun deleteSearchHistory(searchId: Long) {
        searchDao.deleteSearchHistory(searchId)
    }

    override fun getSearchHistory(): Flow<List<SearchHistory>> {
        return searchDao.getSearchHistory().map { searchHistories ->
            searchHistories.map { entity ->
                entity.toSearchHistory()
            }
        }
    }
}