package com.pardess.musicplayer.presentation.main.search

import com.pardess.musicplayer.domain.model.Album
import com.pardess.musicplayer.domain.model.Artist
import com.pardess.musicplayer.domain.model.Song

data class SearchUiState(
    val allSongs: List<Song> = emptyList(),
    val searchQuery: String = "",
    val searchResult: SearchResult = SearchResult()
)

data class SearchResult(
    val songs: List<Song> = emptyList(),
    val artists: List<Artist> = emptyList(),
    val albums: List<Album> = emptyList()
)