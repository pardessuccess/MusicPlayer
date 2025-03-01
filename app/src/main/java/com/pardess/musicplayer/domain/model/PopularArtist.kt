package com.pardess.musicplayer.domain.model

data class PopularArtist(
    val id: Long,
    val name: String = "없음",
    val albums: List<Album> = emptyList(),
    val songs: List<Song> = emptyList(),
)
