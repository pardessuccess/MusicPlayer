package com.pardess.musicplayer.domain.usecase.main

import com.pardess.musicplayer.domain.model.Album
import com.pardess.musicplayer.domain.model.Artist
import com.pardess.musicplayer.domain.model.SearchHistory
import com.pardess.musicplayer.domain.model.Song
import com.pardess.musicplayer.presentation.Status
import com.pardess.musicplayer.utils.Utils.extractChosung
import com.pardess.musicplayer.utils.Utils.getAlbumsFromSongs
import com.pardess.musicplayer.utils.Utils.getArtistsFromSongs
import com.pardess.musicplayer.utils.Utils.isChosungOnly
import com.pardess.musicplayer.utils.Utils.normalizeText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

interface SearchUseCase {
    suspend fun saveSearchHistory(searchHistory: SearchHistory)
    fun setAllSongs(songs: List<Song>): List<Song>
    fun searchSongs(query: String, allSongs: List<Song>): Flow<Status<List<Song>>>
    fun searchArtists(query: String, allSongs: List<Song>): Flow<Status<List<Artist>>>
    fun searchAlbums(query: String, allSongs: List<Song>): Flow<Status<List<Album>>>
}

class SearchUseCaseImpl @Inject constructor(
    private val mainUseCase: MainUseCase
) : SearchUseCase {
    override suspend fun saveSearchHistory(searchHistory: SearchHistory) {
        mainUseCase.saveSearchHistory(searchHistory)
    }

    override fun setAllSongs(songs: List<Song>): List<Song> {
        return songs
    }

    override fun searchSongs(query: String, allSongs: List<Song>): Flow<Status<List<Song>>> = flow {
        emit(Status.Loading)
        val normalizedQuery = normalizeText(query)
        val useChoSungSearch = isChosungOnly(query)
        val result = allSongs.filter {
            val title = normalizeText(it.title)
            if (useChoSungSearch) extractChosung(title).contains(query)
            else title.contains(normalizedQuery)
        }
        emit(Status.Success(result))
    }.flowOn(Dispatchers.IO)

    override fun searchArtists(query: String, allSongs: List<Song>): Flow<Status<List<Artist>>> =
        flow {
            emit(Status.Loading)
            val normalizedQuery = normalizeText(query)
            val result = allSongs.filter { normalizeText(it.artistName).contains(normalizedQuery) }
                .getArtistsFromSongs()
            emit(Status.Success(result))
        }.flowOn(Dispatchers.IO)

    override fun searchAlbums(query: String, allSongs: List<Song>): Flow<Status<List<Album>>> =
        flow {
            emit(Status.Loading)
            val normalizedQuery = normalizeText(query)
            val result = allSongs.filter { normalizeText(it.albumName).contains(normalizedQuery) }
                .getAlbumsFromSongs()
            emit(Status.Success(result))
        }.flowOn(Dispatchers.IO)
}