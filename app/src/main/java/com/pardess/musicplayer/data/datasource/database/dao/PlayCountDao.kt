package com.pardess.musicplayer.data.datasource.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.pardess.musicplayer.data.entity.PlayCountEntity
import com.pardess.musicplayer.data.entity.join.PlayCountSong
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayCountDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlayCount(playCount: PlayCountEntity)

    @Delete
    suspend fun deletePlayCount(playCount: PlayCountEntity)

    @Query("DELETE FROM PlayCountEntity WHERE songId = :songId")
    suspend fun deletePlayCountBySongId(songId: Long)

    @Query("SELECT * FROM PlayCountEntity WHERE songId = :songId")
    suspend fun getPlayCountBySongId(songId: Long): PlayCountEntity?

    @Query("UPDATE PlayCountEntity SET count = count + 1 WHERE songId = :songId")
    suspend fun incrementPlayCount(songId: Long)

    @Transaction
    suspend fun increaseOrInsertPlayCount(songId: Long) {
        val existingPlayCount = getPlayCountBySongId(songId)
        if (existingPlayCount != null) {
            incrementPlayCount(songId) // ✅ 기존 데이터가 있으면 증가
        } else {
            insertPlayCount(PlayCountEntity(id = 0, songId = songId, count = 1)) // ✅ 없으면 새로 추가
        }
    }

    @Query("SELECT * FROM PlayCountEntity ORDER BY count DESC")
    fun getAllPlayCounts(): Flow<List<PlayCountEntity>>

    @Query(
        """
        SELECT SongEntity.*, PlayCountEntity.count 
        FROM PlayCountEntity
        INNER JOIN SongEntity ON PlayCountEntity.songId = SongEntity.id
        ORDER BY PlayCountEntity.count DESC
    """
    )
    fun getMostPlayedSongs(): Flow<List<PlayCountSong>>
}