package com.pardess.musicplayer.data.repository

import android.content.Context
import com.pardess.musicplayer.data.Result
import com.pardess.musicplayer.data.SongSortOrder
import com.pardess.musicplayer.data.query.SongFlow
import com.pardess.musicplayer.domain.model.Album
import com.pardess.musicplayer.domain.model.Artist
import com.pardess.musicplayer.domain.model.Song
import com.pardess.musicplayer.domain.repository.MusicRepository
import com.pardess.musicplayer.utils.Utils.getAlbumsFromSongs
import com.pardess.musicplayer.utils.Utils.getArtistsFromSongs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class MusicRepositoryImpl(
    context: Context,
) : MusicRepository {

    private val contentResolver = context.contentResolver

    override fun getSongs(): Flow<Result<List<Song>>> {
        return SongFlow(
            contentResolver = contentResolver,
            sortOrder = SongSortOrder.SONG_ARTIST
        ).flowData().map { Result.Success(it) }.flowOn(Dispatchers.IO)
    }

    override fun getArtists(songs: List<Song>, sortOrder: String): List<Artist> {
        return songs.getArtistsFromSongs()
    }

    override fun getAlbums(songs: List<Song>): List<Album> {
        return songs.getAlbumsFromSongs()
    }

    override fun getAlbumsByArtist(artistId: Long): Flow<List<Album>> {
        return getSongs().map { result ->
            when(result) {
                is Result.Success ->  result.data!!.filter { it.artistId == artistId }.getAlbumsFromSongs()
                is Result.Error -> emptyList()
            }
        }.flowOn(Dispatchers.IO)
    }

    override fun getSongsByArtist(artistId: Long): Flow<List<Song>> {
        return getSongs().map { result ->
            when (result) {
                is Result.Success -> result.data!!.filter { it.artistId == artistId }
                is Result.Error -> emptyList()
            }
        }.flowOn(Dispatchers.IO)
    }


}