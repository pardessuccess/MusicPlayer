package com.pardess.musicplayer.presentation.artist.detail

sealed class DetailArtistUiEvent {
    data class EnterDetailAlbum(val albumId: Long) : DetailArtistUiEvent()
}