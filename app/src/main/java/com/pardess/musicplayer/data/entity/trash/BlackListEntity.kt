package com.pardess.musicplayer.data.entity.trash

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class BlackListEntity(
    @PrimaryKey
    val path: String
)
