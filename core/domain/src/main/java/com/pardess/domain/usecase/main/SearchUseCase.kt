package com.pardess.domain.usecase.main

import com.pardess.common.Result
import com.pardess.common.Utils.getAlbumsFromSongs
import com.pardess.common.Utils.getArtistsFromSongs
import com.pardess.domain.Utils.extractChosung
import com.pardess.domain.Utils.isChosungOnly
import com.pardess.domain.Utils.normalizeText
import com.pardess.model.Album
import com.pardess.model.Artist
import com.pardess.model.SearchHistory
import com.pardess.model.Song
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

interface SearchUseCase {
    suspend fun saveSearchHistory(searchHistory: SearchHistory)
    fun setAllSongs(songs: List<Song>): List<Song>
    fun searchSongs(query: String, allSongs: List<Song>): Flow<Result<List<Song>>>
    fun searchArtists(query: String, allSongs: List<Song>): Flow<Result<List<Artist>>>
    fun searchAlbums(query: String, allSongs: List<Song>): Flow<Result<List<Album>>>
}

class SearchUseCaseImpl @Inject constructor(
    private val homeUseCase: HomeUseCase
) : SearchUseCase {
    override suspend fun saveSearchHistory(searchHistory: SearchHistory) {
        homeUseCase.saveSearchHistory(searchHistory)
    }

    override fun setAllSongs(songs: List<Song>): List<Song> {
        return songs
    }

    override fun searchSongs(query: String, allSongs: List<Song>): Flow<Result<List<Song>>> = flow {
        emit(Result.Loading)
        val normalizedQuery = normalizeText(query)
        val useChoSungSearch = isChosungOnly(query)
        val result = allSongs.filter {
            val title = normalizeText(it.title)
            if (useChoSungSearch) extractChosung(title).contains(query)
            else title.contains(normalizedQuery)
        }
        emit(Result.Success(result))
    }.flowOn(Dispatchers.IO)

    override fun searchArtists(query: String, allSongs: List<Song>): Flow<Result<List<Artist>>> =
        flow {
            emit(Result.Loading)
            val normalizedQuery = normalizeText(query)
            val result = allSongs.filter { normalizeText(it.artistName).contains(normalizedQuery) }
                .getArtistsFromSongs()
            emit(Result.Success(result))
        }.flowOn(Dispatchers.IO)

    override fun searchAlbums(query: String, allSongs: List<Song>): Flow<Result<List<Album>>> =
        flow {
            emit(Result.Loading)
            val normalizedQuery = normalizeText(query)
            val result = allSongs.filter { normalizeText(it.albumName).contains(normalizedQuery) }
                .getAlbumsFromSongs()
            emit(Result.Success(result))
        }.flowOn(Dispatchers.IO)
}