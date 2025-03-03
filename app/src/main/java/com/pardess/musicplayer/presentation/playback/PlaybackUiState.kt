package com.pardess.musicplayer.presentation.playback

import com.pardess.musicplayer.domain.model.Song
import java.time.Duration

data class PlaybackUiState(
    val playerState: PlayerState = PlayerState(),
    val currentPlaylist: List<Song> = emptyList(),
    val expand: Boolean = false,
)

data class PlayerState(
    val currentSong: Song? = null,
    val isPlaying: Boolean = false,
    val isLoading: Boolean = false,
    val hasNext: Boolean = false,
    val currentPosition: Duration = Duration.ZERO,
    val shuffle: Boolean = false,
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