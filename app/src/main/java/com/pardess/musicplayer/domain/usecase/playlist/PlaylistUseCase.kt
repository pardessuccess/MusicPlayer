package com.pardess.musicplayer.domain.usecase.playlist

import com.pardess.musicplayer.data.entity.PlaylistEntity
import com.pardess.musicplayer.data.entity.PlaylistSong
import com.pardess.musicplayer.data.entity.join.PlaylistSongs
import com.pardess.musicplayer.domain.repository.PlaylistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

interface PlaylistUseCase {
    fun getPlaylists(): Flow<List<PlaylistEntity>>
    suspend fun createPlaylist(playlistName: String): PlaylistEntity
    suspend fun deletePlaylist(playlistIds: List<Long>)
    suspend fun changePlaylistOrder(changedPlaylists: List<PlaylistEntity>)
    fun getPlaylistSongs(playlistId: Long): Flow<PlaylistSongs>
    suspend fun savePlaylistSongs(selectedSongs: List<PlaylistSong>)
    suspend fun deletePlaylistSongs(
        selectedSongs: List<PlaylistSong>,
        currentPlaylistSongs: List<PlaylistSong>
    )
}

class PlaylistUseCaseImpl @Inject constructor(
    private val repository: PlaylistRepository
) : PlaylistUseCase {
    override fun getPlaylists(): Flow<List<PlaylistEntity>> {
        return repository.getAllPlaylist()
    }

    override suspend fun createPlaylist(playlistName: String): PlaylistEntity {
        val currentPlaylists = repository.getAllPlaylist().firstOrNull() ?: emptyList()
        val newDisplayOrder = currentPlaylists.size
        val newPlaylist = PlaylistEntity(
            playlistName = playlistName,
            displayOrder = newDisplayOrder,
        )
        repository.createPlaylist(newPlaylist)
        return newPlaylist
    }

    override suspend fun deletePlaylist(playlistIds: List<Long>) {
        repository.deletePlaylistsByIds(playlistIds)
    }

    override suspend fun changePlaylistOrder(changedPlaylists: List<PlaylistEntity>) {
        changedPlaylists.forEachIndexed { index, playlist ->
            playlist.displayOrder = index
            repository.updatePlaylist(playlist)
        }
    }

    override fun getPlaylistSongs(playlistId: Long): Flow<PlaylistSongs> {
        return repository.getPlaylistWithSongs(playlistId)
    }

    override suspend fun savePlaylistSongs(selectedSongs: List<PlaylistSong>) {
        repository.insertSongEntities(selectedSongs)
    }

    override suspend fun deletePlaylistSongs(
        selectedSongs: List<PlaylistSong>,
        currentPlaylistSongs: List<PlaylistSong>
    ) {
        val songsToDelete = currentPlaylistSongs.filter { song ->
            selectedSongs.any { it.songPrimaryKey == song.songPrimaryKey }
        }
        repository.deleteSongEntities(songsToDelete)
    }
}

