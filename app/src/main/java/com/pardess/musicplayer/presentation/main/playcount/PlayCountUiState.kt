package com.pardess.musicplayer.presentation.main.playcount

import com.pardess.musicplayer.data.entity.join.FavoriteSong
import com.pardess.musicplayer.data.entity.join.HistorySong
import com.pardess.musicplayer.data.entity.join.PlayCountSong

data class PlayCountUiState(
    val playCountSongs: List<PlayCountSong> = emptyList(),
)