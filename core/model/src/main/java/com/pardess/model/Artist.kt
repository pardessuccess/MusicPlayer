package com.pardess.model

data class Artist(
    val id: Long,
    val name: String = "없음",
    val albums: List<Album> = emptyList(),
    val songs: List<Song> = emptyList(),

    )
