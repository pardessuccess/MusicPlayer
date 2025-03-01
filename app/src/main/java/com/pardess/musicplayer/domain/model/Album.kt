package com.pardess.musicplayer.domain.model

data class Album(
    val id: Long = 0L,
    val title: String = "",
    val artistId: Long = 0L,
    val artistName: String = "",
    val year: Int = 0,
    val songCount: Int = 0,
    val songs: List<Song> = emptyList()
)