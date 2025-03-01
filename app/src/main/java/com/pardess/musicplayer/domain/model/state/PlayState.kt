package com.pardess.musicplayer.domain.model.state

import com.pardess.musicplayer.domain.model.Song
import com.pardess.musicplayer.domain.model.enums.PlayerState


data class PlayState(
    val playerState: PlayerState? = null,
    val currentPlaylist: List<Song> = emptyList(),
    val currentSong: Song? = null,
    val currentPosition: Long = 0L,
    val totalDuration: Long = 0L,
    val isShuffleEnabled: Boolean = false,
    val repeatMode: Int = 0
)

enum class RepeatMode(val value: Int) {
    REPEAT_OFF(0),
    REPEAT_ONE(1),
    REPEAT_ALL(2);

    companion object {
        fun fromValue(value: Int): RepeatMode {
            return entries.first { it.value == value }
        }

        fun next(mode: RepeatMode): RepeatMode {
            return when (mode) {
                REPEAT_OFF -> REPEAT_ALL
                REPEAT_ALL -> REPEAT_ONE
                REPEAT_ONE -> REPEAT_OFF
            }
        }
    }
}