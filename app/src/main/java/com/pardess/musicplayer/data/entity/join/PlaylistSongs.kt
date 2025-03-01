package com.pardess.musicplayer.data.entity.join

import androidx.room.Embedded
import androidx.room.Relation
import com.pardess.musicplayer.data.entity.PlaylistEntity
import com.pardess.musicplayer.data.entity.PlaylistSong

data class PlaylistSongs(
    @Embedded val playlist : PlaylistEntity,

    @Relation(
        parentColumn = "playlist_id",
        entityColumn = "playlist_creator_id"
    )
    val songs: List<PlaylistSong>
)
