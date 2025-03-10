package com.pardess.database.entity.join

import androidx.room.Embedded
import androidx.room.Relation
import com.pardess.database.entity.PlaylistEntity
import com.pardess.database.entity.PlaylistSongEntity

data class PlaylistSongsDto(
    @Embedded val playlist : PlaylistEntity,

    @Relation(
        parentColumn = "playlist_id",
        entityColumn = "playlist_creator_id"
    )
    val songs: List<PlaylistSongEntity>
)
