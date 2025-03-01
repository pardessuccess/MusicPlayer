package com.pardess.musicplayer.presentation.playback

import com.pardess.musicplayer.domain.model.state.PlayState

data class PlaybackUiState(
    val playState: PlayState = PlayState(),
    val expand: Boolean = false,
)