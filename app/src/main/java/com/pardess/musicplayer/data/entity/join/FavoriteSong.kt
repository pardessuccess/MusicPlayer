package com.pardess.musicplayer.data.entity.join

import androidx.room.ColumnInfo
import androidx.room.Embedded
import com.pardess.musicplayer.data.entity.SongEntity

data class FavoriteSong(
    @Embedded val song: SongEntity,
    @ColumnInfo(name = "count") val favoriteCount: Int?
)