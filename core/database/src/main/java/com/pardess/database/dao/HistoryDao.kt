package com.pardess.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pardess.database.entity.HistoryEntity
import com.pardess.database.entity.join.HistorySongDto
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(history: HistoryEntity)

    @Delete
    suspend fun deleteHistory(history: HistoryEntity)

    @Query("DELETE FROM HistoryEntity")
    suspend fun deleteAllHistory()

    @Query("DELETE FROM HistoryEntity WHERE timestamp = :timestamp")
    suspend fun deleteHistoryByTimestamp(timestamp: Long)

    @Query("SELECT * FROM HistoryEntity WHERE songId = :songId")
    suspend fun getHistoryBySongId(songId: Long): HistoryEntity?

    @Query("UPDATE HistoryEntity SET timestamp = :timestamp WHERE songId = :songId")
    suspend fun updateHistoryTimestamp(songId: Long, timestamp: Long)
//
//    @Transaction
//    suspend fun insertOrUpdateHistory(songId: Long, timestamp: Long) {
//        insertHistory(HistoryEntity(id = 0, songId = songId, timestamp = timestamp)) // ✅ 없으면 추가
//    }

    @Query(
        """
        SELECT SongEntity.*, HistoryEntity.timestamp 
        FROM HistoryEntity
        INNER JOIN SongEntity ON HistoryEntity.songId = SongEntity.id
        ORDER BY HistoryEntity.timestamp DESC
    """
    )
    fun getHistorySongs(): Flow<List<HistorySongDto>>
}