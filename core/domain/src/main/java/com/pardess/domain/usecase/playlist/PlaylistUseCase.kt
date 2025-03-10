package com.pardess.domain.usecase.playlist

import com.pardess.domain.repository.PlaylistRepository
import com.pardess.model.Playlist
import com.pardess.model.PlaylistSong
import com.pardess.model.join.PlaylistSongs
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

interface PlaylistUseCase {
    fun getPlaylists(): Flow<List<Playlist>>
    suspend fun createPlaylist(playlistName: String): Playlist
    suspend fun deletePlaylist(playlistIds: List<Long>)
    suspend fun changePlaylistOrder(changedPlaylists: List<Playlist>)
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
    override fun getPlaylists(): Flow<List<Playlist>> {
        return repository.getAllPlaylist()
    }

    override suspend fun createPlaylist(playlistName: String): Playlist {
        val currentPlaylists = repository.getAllPlaylist().firstOrNull() ?: emptyList()
        val newDisplayOrder = currentPlaylists.size
        val newPlaylist = Playlist(
            playlistName = playlistName,
            displayOrder = newDisplayOrder,
        )
        repository.createPlaylist(newPlaylist)
        return newPlaylist
    }

    override suspend fun deletePlaylist(playlistIds: List<Long>) {
        repository.deletePlaylistsByIds(playlistIds)
    }

    override suspend fun changePlaylistOrder(changedPlaylists: List<Playlist>) {
        changedPlaylists.forEachIndexed { index, playlist ->
            playlist.displayOrder = index
            repository.updatePlaylist(playlist)
        }
    }

    override fun getPlaylistSongs(playlistId: Long): Flow<PlaylistSongs> {
        return repository.getPlaylistWithSongs(playlistId)
    }

    override suspend fun savePlaylistSongs(selectedSongs: List<PlaylistSong>) {
        repository.insertPlaylistSongs(selectedSongs)
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

