package com.pardess.musicplayer.presentation.main

import com.pardess.musicplayer.data.entity.SearchHistoryEntity
import com.pardess.musicplayer.data.entity.SongEntity
import com.pardess.musicplayer.data.entity.join.FavoriteSong
import com.pardess.musicplayer.domain.model.Album
import com.pardess.musicplayer.domain.model.Artist
import com.pardess.musicplayer.domain.model.SearchHistory

data class MainUiState(

    val song1st: SongEntity? = null,
    val song2nd: SongEntity? = null,
    val song3rd: SongEntity? = null,
    val favoriteSongs: List<FavoriteSong> = emptyList(),
    val popularArtists: List<Artist> = emptyList(),
    val popularAlbums: List<Album> = emptyList(),
    val searchHistories: List<SearchHistory> = emptyList(),
    val showGuideText: Boolean = false

    )