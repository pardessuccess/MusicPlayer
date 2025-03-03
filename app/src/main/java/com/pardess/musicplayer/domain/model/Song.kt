package com.pardess.musicplayer.domain.model

import java.time.Duration

data class Song(
    val id: Long,
    val title: String,
    val trackNumber: Int,
    val year: Int,
    val duration: Duration = Duration.ofSeconds(0),
    val data: String,
    val dateModified: Long,
    val albumId: Long,
    val albumName: String,
    val artistId: Long,
    val artistName: String,
    val composer: String?,
    val albumArtist: String?,
    val favorite: Boolean = false
){
    companion object {

        @JvmStatic
        val emptySong = Song(
            id = -1,
            title = "",
            trackNumber = -1,
            year = -1,
            duration = Duration.ofSeconds(0),
            data = "",
            dateModified = -1,
            albumId = -1,
            albumName = "",
            artistId = -1,
            artistName = "",
            composer = "",
            albumArtist = ""
        )
    }
}

