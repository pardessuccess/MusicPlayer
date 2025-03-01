package com.pardess.musicplayer.data.repository

import com.pardess.musicplayer.data.datasource.database.dao.PlaylistDao
import com.pardess.musicplayer.data.entity.PlaylistEntity
import com.pardess.musicplayer.data.entity.join.PlaylistSongs
import com.pardess.musicplayer.data.entity.PlaylistSong
import com.pardess.musicplayer.domain.repository.PlaylistRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PlaylistRepositoryImpl @Inject constructor(
    private val playlistDao: PlaylistDao
) : PlaylistRepository {
    override fun getAllPlaylist(): Flow<List<PlaylistEntity>> {
        return playlistDao.getAllPlaylists()
    }

    override suspend fun getPlaylistById(id: Long): PlaylistEntity? {
        return playlistDao.getPlaylistById(id)
    }

    override suspend fun createPlaylist(playlist: PlaylistEntity): PlaylistEntity? {
        return playlistDao.insertAndGetPlaylist(playlist)
    }

    override suspend fun updatePlaylist(playlist: PlaylistEntity) {
        playlistDao.updatePlaylist(playlist)
    }

    override suspend fun updatePlaylists(playlists: List<PlaylistEntity>) {
        playlistDao.updatePlaylists(playlists)
    }

    override suspend fun deletePlaylist(playlist: PlaylistEntity) {
        playlistDao.deletePlaylist(playlist)
    }

    override suspend fun insertSongEntity(playlistSong: PlaylistSong) {
        playlistDao.insertSongEntity(playlistSong)
    }

    override suspend fun insertSongEntities(songEntities: List<PlaylistSong>) {
        playlistDao.insertSongEntities(songEntities)
    }

    override suspend fun deleteSongEntity(playlistSong: PlaylistSong) {
        playlistDao.deleteSongEntity(playlistSong)
    }

    override suspend fun deleteSongEntities(songEntities: List<PlaylistSong>) {
        playlistDao.deleteSongEntities(songEntities)
    }

    override fun getPlaylistWithSongs(playlistId: Long): Flow<PlaylistSongs> {
        return playlistDao.getPlaylistWithSongs(playlistId)
    }


}