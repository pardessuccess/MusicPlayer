package com.pardess.model.join

import com.pardess.model.Song

data class PlayCountSong(
    val song: Song,
    val playCount: Int?
)
