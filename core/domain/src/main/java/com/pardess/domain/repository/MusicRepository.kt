package com.pardess.domain.repository

import com.pardess.model.Album
import com.pardess.model.Artist
import com.pardess.model.Song
import kotlinx.coroutines.flow.Flow
import com.pardess.common.Result

interface MusicRepository {

    fun getSongs(): Flow<Result<List<Song>>>

    fun getArtists(songs: List<Song>, sortOrder: String): List<Artist>

    fun getAlbums(songs: List<Song>): List<Album>

    fun getAlbumsByArtist(artistId: Long): Flow<List<Album>>

    fun getSongsByArtist(artistId: Long): Flow<List<Song>>

}