package com.pardess.data.repository

import android.content.Context
import com.pardess.domain.repository.MusicRepository
import com.pardess.media_query.SongFlow
import com.pardess.media_query.utils.SongSortOrder
import com.pardess.model.Album
import com.pardess.model.Artist
import com.pardess.model.Song
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import com.pardess.common.Result
import com.pardess.common.Utils.getAlbumsFromSongs
import com.pardess.common.Utils.getArtistsFromSongs

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
                is Result.Success ->  result.data.filter { it.artistId == artistId }.getAlbumsFromSongs()
                is Result.Error -> emptyList()
                Result.Loading -> emptyList()
            }
        }.flowOn(Dispatchers.IO)
    }

    override fun getSongsByArtist(artistId: Long): Flow<List<Song>> {
        return getSongs().map { result ->
            when (result) {
                is Result.Success -> result.data.filter { it.artistId == artistId }
                is Result.Error -> emptyList()
                Result.Loading -> emptyList()
            }
        }.flowOn(Dispatchers.IO)
    }


}