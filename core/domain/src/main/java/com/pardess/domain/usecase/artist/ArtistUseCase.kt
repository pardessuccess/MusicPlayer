package com.pardess.domain.usecase.artist

import com.pardess.domain.repository.MusicRepository
import com.pardess.model.Album
import com.pardess.model.Song
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject
import com.pardess.common.Result

interface ArtistUseCase {
    fun getSongsByArtist(artistId: Long): Flow<Result<List<Song>>>
    fun getAlbumsByArtist(artistId: Long): Flow<Result<List<Album>>>
    fun getAlbum(artistId: Long, albumId: Long): Flow<Result<Album>>
}

class ArtistUseCaseImpl @Inject constructor(
    private val repository: MusicRepository
) : ArtistUseCase {
    override fun getSongsByArtist(artistId: Long): Flow<Result<List<Song>>> {
        return repository.getSongsByArtist(artistId).map {
            delay(1000)
            Result.Success(it) as Result<List<Song>>
        }.onStart { emit(Result.Loading) }.catch {
            emit(Result.Error(it.message ?: "Unknown Error"))
        }
    }

    override fun getAlbumsByArtist(artistId: Long): Flow<Result<List<Album>>> {
        return repository.getAlbumsByArtist(artistId).map {
            Result.Success(it) as Result<List<Album>>
        }.onStart { emit(Result.Loading) }.catch {
            emit(Result.Error(it.message ?: "Unknown Error"))
        }
    }

    override fun getAlbum(
        artistId: Long,
        albumId: Long,
    ): Flow<Result<Album>> {
        return getAlbumsByArtist(artistId).map { result ->
            if (result is Result.Success) {
                delay(1000)
                val album = result.data.firstOrNull { it.id == albumId }
                album?.let { Result.Success(it) } ?: Result.Error("앨범을 찾을 수 없습니다.")
            } else {
                Result.Loading
            }
        }
    }
}