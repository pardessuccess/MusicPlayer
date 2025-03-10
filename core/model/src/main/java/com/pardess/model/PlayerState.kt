package com.pardess.model

import java.time.Duration

data class PlayerState(
    val currentSong: Song? = null,
    val isPlaying: Boolean = false,
    val isLoading: Boolean = false,
    val hasNext: Boolean = false,
    val currentPosition: Duration = Duration.ZERO,
    val shuffle: Boolean = false,
    val repeatMode: Int = 0
)