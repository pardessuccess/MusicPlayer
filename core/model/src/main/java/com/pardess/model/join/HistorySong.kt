package com.pardess.model.join

import com.pardess.model.Song

data class HistorySong(
    val song: Song,
    val lastPlayed: Long?
)
