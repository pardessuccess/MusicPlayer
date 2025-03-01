package com.pardess.musicplayer.domain.repository

import com.pardess.musicplayer.domain.model.SearchHistory
import kotlinx.coroutines.flow.Flow

interface SearchRepository {

    suspend fun saveSearchHistory(searchHistory: SearchHistory)

    suspend fun deleteSearchHistory(searchId: Long)

    fun getSearchHistory(): Flow<List<SearchHistory>>

}