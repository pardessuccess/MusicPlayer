package com.pardess.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pardess.database.entity.SearchHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SearchDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSearchHistory(searchHistory: SearchHistoryEntity)

    @Query("DELETE FROM search_history WHERE id = :searchId")
    suspend fun deleteSearchHistory(searchId: Long)

    @Query("SELECT * FROM search_history ORDER BY timestamp DESC")
    fun getSearchHistory(): Flow<List<SearchHistoryEntity>>

}