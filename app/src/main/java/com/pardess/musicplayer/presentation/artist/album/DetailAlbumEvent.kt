package com.pardess.musicplayer.presentation.artist.album

import com.pardess.musicplayer.domain.model.Album

sealed class DetailAlbumEvent {

    data class SelectAlbum(val album: Album) : DetailAlbumEvent()

    data class SetAlbums(val albums: List<Album>) : DetailAlbumEvent()

}