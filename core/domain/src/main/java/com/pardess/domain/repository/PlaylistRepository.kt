package com.pardess.domain.repository

import com.pardess.model.Playlist
import com.pardess.model.PlaylistSong
import com.pardess.model.join.PlaylistSongs
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {

    fun getAllPlaylist(): Flow<List<Playlist>>

    suspend fun getPlaylistById(id: Long): Playlist?

    suspend fun createPlaylist(playlist: Playlist): Playlist?

    suspend fun updatePlaylist(playlist: Playlist)

    suspend fun updatePlaylists(playlists: List<Playlist>)

    suspend fun deletePlaylistsByIds(ids: List<Long>)

    suspend fun insertPlaylistSong(playlistSong: PlaylistSong)

    suspend fun insertPlaylistSongs(songs: List<PlaylistSong>)

    suspend fun deletePlaylistSong(playlistSong: PlaylistSong)

    suspend fun deleteSongEntities(songs: List<PlaylistSong>)

    fun getPlaylistWithSongs(playlistId: Long): Flow<PlaylistSongs>



}