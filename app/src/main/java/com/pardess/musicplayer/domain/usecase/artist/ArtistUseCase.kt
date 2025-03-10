package com.pardess.musicplayer.domain.usecase.artist

import com.pardess.musicplayer.domain.model.Album
import com.pardess.musicplayer.domain.model.Song
import com.pardess.musicplayer.domain.repository.MusicRepository
import com.pardess.musicplayer.presentation.Status
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

interface ArtistUseCase {
    fun getSongsByArtist(artistId: Long): Flow<Status<List<Song>>>
    fun getAlbumsByArtist(artistId: Long): Flow<Status<List<Album>>>
    fun getAlbum(artistId: Long, albumId: Long): Flow<Status<Album>>
}

class ArtistUseCaseImpl @Inject constructor(
    private val repository: MusicRepository
) : ArtistUseCase {
    override fun getSongsByArtist(artistId: Long): Flow<Status<List<Song>>> {
        return repository.getSongsByArtist(artistId).map {
            delay(1000)
            Status.Success(it) as Status<List<Song>>
        }.onStart { emit(Status.Loading) }.catch {
            emit(Status.Error(it.message ?: "Unknown Error"))
        }
    }

    override fun getAlbumsByArtist(artistId: Long): Flow<Status<List<Album>>> {
        return repository.getAlbumsByArtist(artistId).map {
            Status.Success(it) as Status<List<Album>>
        }.onStart { emit(Status.Loading) }.catch {
            emit(Status.Error(it.message ?: "Unknown Error"))
        }
    }

    override fun getAlbum(
        artistId: Long,
        albumId: Long,
    ): Flow<Status<Album>> {
        return getAlbumsByArtist(artistId).map { status ->
            if (status is Status.Success) {
                delay(1000)
                val album = status.data.firstOrNull { it.id == albumId }
                album?.let { Status.Success(it) } ?: Status.Error("앨범을 찾을 수 없습니다.")
            } else {
                Status.Loading
            }
        }
    }
}