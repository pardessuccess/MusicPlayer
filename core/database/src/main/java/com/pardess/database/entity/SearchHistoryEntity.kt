package com.pardess.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "search_history")
class SearchHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val type: Int,
    val image: String?,
    val text: String,
    val timestamp: Long,
)
