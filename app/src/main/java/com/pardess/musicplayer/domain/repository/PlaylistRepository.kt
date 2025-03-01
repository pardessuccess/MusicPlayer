package com.pardess.musicplayer.domain.repository

import com.pardess.musicplayer.data.entity.PlaylistEntity
import com.pardess.musicplayer.data.entity.join.PlaylistSongs
import com.pardess.musicplayer.data.entity.PlaylistSong
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {

    fun getAllPlaylist(): Flow<List<PlaylistEntity>>

    suspend fun getPlaylistById(id: Long): PlaylistEntity?

    suspend fun createPlaylist(playlist: PlaylistEntity): PlaylistEntity?

    suspend fun updatePlaylist(playlist: PlaylistEntity)

    suspend fun updatePlaylists(playlists: List<PlaylistEntity>)

    suspend fun deletePlaylist(playlist: PlaylistEntity)

    suspend fun insertSongEntity(playlistSong: PlaylistSong)

    suspend fun insertSongEntities(songEntities: List<PlaylistSong>)

    suspend fun deleteSongEntity(playlistSong: PlaylistSong)

    suspend fun deleteSongEntities(songEntities: List<PlaylistSong>)

    fun getPlaylistWithSongs(playlistId: Long): Flow<PlaylistSongs>



}