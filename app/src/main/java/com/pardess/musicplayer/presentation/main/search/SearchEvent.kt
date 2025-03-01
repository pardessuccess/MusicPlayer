package com.pardess.musicplayer.presentation.main.search

import com.pardess.musicplayer.domain.model.Album
import com.pardess.musicplayer.domain.model.Artist
import com.pardess.musicplayer.domain.model.Song

sealed class SearchEvent {

    data class Search(val searchQuery: String) : SearchEvent()

    data class SelectSong(val song: Song) : SearchEvent()

    data class SelectArtist(val artist: Artist) : SearchEvent()

    data class SelectAlbum(val album: Album) : SearchEvent()

}