package com.pardess.musicplayer.domain.repository

import com.pardess.musicplayer.domain.model.Artist
import com.pardess.musicplayer.domain.model.Song
import kotlinx.coroutines.flow.Flow
import com.pardess.musicplayer.data.Result
import com.pardess.musicplayer.domain.model.Album

interface MusicRepository {

    fun getSongs(): Flow<Result<List<Song>>>

    fun getArtists(songs: List<Song>, sortOrder: String): List<Artist>

    fun getAlbums(songs: List<Song>): List<Album>

    fun getAlbumsByArtist(artistId: Long): Flow<List<Album>>

    fun getSongsByArtist(artistId: Long): Flow<List<Song>>

}