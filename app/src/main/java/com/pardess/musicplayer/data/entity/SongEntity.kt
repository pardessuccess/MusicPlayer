package com.pardess.musicplayer.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SongEntity(
    @PrimaryKey
    val id: Long,
    val title: String,
    val year: Int,
    val duration: Long,
    val data: String,
    val albumId: Long,
    val albumName: String,
    val artistId: Long,
    val artistName: String,
)
