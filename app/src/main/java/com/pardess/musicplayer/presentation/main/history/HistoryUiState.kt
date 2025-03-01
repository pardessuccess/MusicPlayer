package com.pardess.musicplayer.presentation.main.history

import com.pardess.musicplayer.data.entity.join.FavoriteSong
import com.pardess.musicplayer.data.entity.join.HistorySong

data class HistoryUiState(
    val historySongs: List<HistorySong> = emptyList(),
)