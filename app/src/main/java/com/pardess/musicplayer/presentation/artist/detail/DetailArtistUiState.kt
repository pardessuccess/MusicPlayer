package com.pardess.musicplayer.presentation.artist.detail

import com.pardess.musicplayer.domain.model.Album
import com.pardess.musicplayer.domain.model.Song
import com.pardess.musicplayer.presentation.UiState

data class DetailArtistUiState(
    val songsState: UiState<List<Song>> = UiState.Loading,
    val albumsState: UiState<List<Album>> = UiState.Loading
)
