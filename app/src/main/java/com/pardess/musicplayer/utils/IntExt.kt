package com.pardess.musicplayer.utils

import androidx.media3.common.Player
import com.pardess.musicplayer.domain.model.enums.PlayerState


fun Int.toPlayerState(isPlaying: Boolean): PlayerState {
    return when (this) {
        Player.STATE_IDLE -> PlayerState.STOPPED
        Player.STATE_BUFFERING -> PlayerState.LOADING
        Player.STATE_READY -> if (isPlaying) PlayerState.PLAYING else PlayerState.PAUSED
        Player.STATE_ENDED -> PlayerState.STOPPED
        else -> PlayerState.STOPPED
    }
}