package com.pardess.model.join

import com.pardess.model.Song

data class FavoriteSong(
    val song: Song,
    val favoriteCount: Int?
)
