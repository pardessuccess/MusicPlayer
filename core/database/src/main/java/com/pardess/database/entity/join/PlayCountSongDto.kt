package com.pardess.database.entity.join

import androidx.room.ColumnInfo
import androidx.room.Embedded
import com.pardess.database.entity.SongEntity

data class PlayCountSongDto(
    @Embedded val song: SongEntity,
    @ColumnInfo(name = "count") val playCount: Int?
)