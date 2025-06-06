package com.pardess.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class PlayCountEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val songId: Long,
    val count: Int,
)
