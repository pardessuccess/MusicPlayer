package com.pardess.data.repository

import com.pardess.data.mapper.toDomain
import com.pardess.data.mapper.toEntity
import com.pardess.database.dao.PlaylistDao
import com.pardess.database.entity.PlaylistEntity
import com.pardess.database.entity.PlaylistSongEntity
import com.pardess.database.entity.join.PlaylistSongsDto
import com.pardess.domain.repository.PlaylistRepository
import com.pardess.model.Playlist
import com.pardess.model.PlaylistSong
import com.pardess.model.join.PlaylistSongs
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PlaylistRepositoryImpl @Inject constructor(
    private val playlistDao: PlaylistDao
) : PlaylistRepository {
    override fun getAllPlaylist(): Flow<List<Playlist>> {
        return playlistDao.getAllPlaylists().map {
            it.map { playlistEntity ->
                playlistEntity.toDomain()
            }
        }
    }

    override suspend fun getPlaylistById(id: Long): Playlist? {
        return playlistDao.getPlaylistById(id)?.toDomain()
    }

    override suspend fun createPlaylist(playlist: Playlist): Playlist? {
        return playlistDao.insertAndGetPlaylist(playlist.toEntity())?.toDomain()
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        playlistDao.updatePlaylist(playlist.toEntity())
    }

    override suspend fun updatePlaylists(playlists: List<Playlist>) {
        playlistDao.updatePlaylists(playlists.map { it.toEntity() })
    }

    override suspend fun deletePlaylistsByIds(ids: List<Long>) {
        playlistDao.deletePlaylistsByIds(ids)
    }

    override suspend fun insertPlaylistSong(playlistSong: PlaylistSong) {
        playlistDao.insertSongEntity(playlistSong.toEntity())
    }

    override suspend fun insertPlaylistSongs(songs: List<PlaylistSong>) {
        playlistDao.insertSongEntities(songs.map { it.toEntity() })
    }

    override suspend fun deletePlaylistSong(playlistSong: PlaylistSong) {
        playlistDao.deleteSongEntity(playlistSong.toEntity())
    }

    override suspend fun deleteSongEntities(songs: List<PlaylistSong>) {
        playlistDao.deleteSongEntities(songs.map { it.toEntity() })
    }

    override fun getPlaylistWithSongs(playlistId: Long): Flow<PlaylistSongs> {
        return playlistDao.getPlaylistWithSongs(playlistId).map {
            it.toDomain()
        }
    }


}