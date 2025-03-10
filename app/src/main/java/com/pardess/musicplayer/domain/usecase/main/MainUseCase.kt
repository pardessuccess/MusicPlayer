package com.pardess.musicplayer.domain.usecase.main

import com.pardess.musicplayer.data.entity.join.FavoriteSong
import com.pardess.musicplayer.domain.model.Album
import com.pardess.musicplayer.domain.model.Artist
import com.pardess.musicplayer.domain.model.SearchHistory
import com.pardess.musicplayer.domain.repository.ManageRepository
import com.pardess.musicplayer.presentation.toSong
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface MainUseCase {

    fun getFavoriteSongs(): Flow<List<FavoriteSong>>
    fun getPopularArtists(): Flow<List<Artist>>
    fun getPopularAlbums(): Flow<List<Album>>
    fun getSearchHistory(): Flow<List<SearchHistory>>
    suspend fun deleteSearchHistory(searchHistoryId: Long)
    suspend fun saveSearchHistory(searchHistory: SearchHistory)

}

class MainUseCaseImpl @Inject constructor(
    private val repository: ManageRepository,
) : MainUseCase {
    override fun getFavoriteSongs(): Flow<List<FavoriteSong>> {
        return repository.getFavoriteSongs()
    }

    override fun getPopularArtists(): Flow<List<Artist>> {
        return getFavoriteSongs().map { favorites ->
            favorites.groupBy { it.song.artistId }
                .map { (artistId, artistFavorites) ->
                    val totalFavorites = artistFavorites.sumOf { it.favoriteCount ?: 0 }
                    val artistName = artistFavorites.first().song.artistName
                    val albums = artistFavorites.groupBy { it.song.albumId }
                        .map { (albumId, albumFavorites) ->
                            Album(
                                id = albumId,
                                title = albumFavorites.first().song.albumName,
                                artistId = artistId,
                                artistName = artistName,
                                year = albumFavorites.first().song.year,
                                songCount = albumFavorites.size,
                                songs = albumFavorites.map { it.song.toSong() }
                            )
                        }
                    Artist(
                        id = artistId,
                        name = artistName,
                        albums = albums,
                        songs = artistFavorites.map { it.song.toSong() }
                    ) to totalFavorites
                }
                .sortedByDescending { it.second }
                .map { it.first }
        }
    }

    override fun getPopularAlbums(): Flow<List<Album>> {
        return getFavoriteSongs().map { favorites ->
            favorites.groupBy { it.song.albumId }
                .map { (albumId, albumFavorites) ->
                    val artistId = albumFavorites.first().song.artistId
                    val artistName = albumFavorites.first().song.artistName
                    val totalFavorites = albumFavorites.sumOf { it.favoriteCount ?: 0 }
                    Album(
                        id = albumId,
                        title = albumFavorites.first().song.albumName,
                        artistId = artistId,
                        artistName = artistName,
                        year = albumFavorites.first().song.year,
                        songCount = albumFavorites.size,
                        songs = albumFavorites.map { it.song.toSong() }
                    ) to totalFavorites
                }
                .sortedByDescending { it.second }
                .map { it.first }
        }
    }

    override fun getSearchHistory(): Flow<List<SearchHistory>> {
        return repository.getSearchHistory()
    }

    override suspend fun deleteSearchHistory(searchHistoryId: Long) {
        repository.deleteSearchHistory(searchHistoryId)
    }

    override suspend fun saveSearchHistory(searchHistory: SearchHistory) {
        repository.saveSearchHistory(searchHistory)
    }
}