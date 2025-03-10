package com.pardess.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.pardess.database.entity.FavoriteEntity
import com.pardess.database.entity.join.FavoriteSongDto
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteEntity)

    @Delete
    suspend fun deleteFavorite(favorite: FavoriteEntity)

    @Query("DELETE FROM FavoriteEntity WHERE songId = :songId")
    suspend fun deleteFavoriteBySongId(songId: Long)

    @Query("SELECT * FROM FavoriteEntity WHERE songId = :songId")
    fun getFavoriteBySongId(songId: Long): FavoriteEntity?

    @Query("SELECT * FROM FavoriteEntity ORDER BY count DESC")
    fun getAllFavorites(): Flow<List<FavoriteEntity>>

    @Query("UPDATE FavoriteEntity SET count = count + 1 WHERE songId = :songId")
    suspend fun incrementFavoriteCount(songId: Long)

    @Transaction
    suspend fun increaseOrInsertFavorite(songId: Long){
        val existingFavorite = getFavoriteBySongId(songId)
        if (existingFavorite != null) {
            incrementFavoriteCount(songId)
        } else {
            insertFavorite(FavoriteEntity(id = 0, songId = songId, count = 1))
        }
    }

    @Query("""
        SELECT SongEntity.*, FavoriteEntity.count 
        FROM FavoriteEntity
        INNER JOIN SongEntity ON FavoriteEntity.songId = SongEntity.id
        ORDER BY FavoriteEntity.count DESC
    """)
    fun getFavoriteSongs(): Flow<List<FavoriteSongDto>>

}
