package com.pardess.musicplayer.data.datasource.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.pardess.musicplayer.data.entity.PlaylistEntity
import com.pardess.musicplayer.data.entity.join.PlaylistSongs
import com.pardess.musicplayer.data.entity.PlaylistSong
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSongEntity(playlistSong: PlaylistSong)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSongEntities(songEntities: List<PlaylistSong>)

    @Delete
    suspend fun deleteSongEntity(playlistSong: PlaylistSong)

    @Delete
    suspend fun deleteSongEntities(songEntities: List<PlaylistSong>)

    @Query("DELETE FROM playlist_song_entity WHERE playlist_creator_id = :id")
    suspend fun deleteSongsByPlaylistId(id: Long)

    @Update
    suspend fun updateSongEntity(playlistSong: PlaylistSong)

    @Query("SELECT * FROM playlist_song_entity WHERE playlist_creator_id = :id")
    fun getSongsByPlaylistId(id: Long): Flow<List<PlaylistSong>>

    // 플레이리스트 추가
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: PlaylistEntity): Long

    suspend fun insertAndGetPlaylist(playlist: PlaylistEntity): PlaylistEntity? {
        val id = insertPlaylist(playlist)
        return getPlaylistById(id)
    }

    // 플레이리스트 여러 개 추가
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylists(playlists: List<PlaylistEntity>)

    // 플레이리스트 수정
    @Update
    suspend fun updatePlaylist(playlist: PlaylistEntity)

    @Update
    suspend fun updatePlaylists(playlists: List<PlaylistEntity>)

    // 플레이리스트 삭제
    @Delete
    suspend fun deletePlaylist(playlist: PlaylistEntity)

    // 특정 ID의 플레이리스트 조회
    @Query("SELECT * FROM PlaylistEntity WHERE playlist_id = :id")
    suspend fun getPlaylistById(id: Long): PlaylistEntity?

    // 모든 플레이리스트 조회
    @Query("SELECT * FROM PlaylistEntity ORDER BY display_order ASC")
    fun getAllPlaylists(): Flow<List<PlaylistEntity>>

    // 특정 이름의 플레이리스트 조회
    @Query("SELECT * FROM PlaylistEntity WHERE playlist_name LIKE '%' || :name || '%' ORDER BY playlist_name ASC")
    fun searchPlaylists(name: String): Flow<List<PlaylistEntity>>

    // 플레이리스트 전체 삭제
    @Query("DELETE FROM PlaylistEntity")
    suspend fun clearPlaylists()

    @Transaction
    @Query("SELECT * FROM PlaylistEntity WHERE playlist_id = :id")
    fun getPlaylistWithSongs(id: Long): Flow<PlaylistSongs>

}