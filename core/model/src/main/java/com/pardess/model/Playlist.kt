package com.pardess.model

data class Playlist(
    val playlistId: Long = 0,
    val playlistName: String,
    val createdAt: Long = System.currentTimeMillis(),
    val pinnedAt: Long? = null,
    val playlistCover: String? = null,
    var displayOrder: Int = 0
)