package com.pardess.musicplayer.utils

import com.pardess.musicplayer.domain.model.enums.PlayerState
import com.pardess.musicplayer.domain.service.MusicController


fun MusicController.isPlaying(): Boolean {
    return this.getPlayerState() == PlayerState.PLAYING
}