package com.pardess.musicplayer.presentation.artist.album

import com.pardess.musicplayer.domain.model.Album
import com.pardess.musicplayer.presentation.UiState

data class DetailAlbumUiState(
    val albumState: UiState<Album> = UiState.Loading,
    val albumsState: UiState<List<Album>> = UiState.Loading,
)
