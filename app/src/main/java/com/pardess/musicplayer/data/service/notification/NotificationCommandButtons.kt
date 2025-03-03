package com.pardess.musicplayer.data.service.notification

import android.os.Bundle
import androidx.media3.common.Player
import androidx.media3.session.SessionCommand
import  com.pardess.musicplayer.R

private const val ACTION_REPEAT = "action_repeat"
private const val ACTION_SHUFFLE = "action_shuffle"
private const val ACTION_PLAY_AND_PAUSE = "action_play_and_pause"
private const val ACTION_PREVIOUS = "action_previous"
private const val ACTION_NEXT = "action_next"

enum class NotificationCommandButtons(
    val customAction: String,
    val displayName: String,
    val iconResId: (Int) -> Int,
    val sessionCommand: SessionCommand,
) {
    REPEAT(
        customAction = ACTION_REPEAT,
        displayName = "SeekRewind",
        iconResId = { repeatMode ->
            when (repeatMode) {
                Player.REPEAT_MODE_OFF -> R.drawable.ic_repeat_off
                Player.REPEAT_MODE_ONE -> R.drawable.ic_repeat_one
                Player.REPEAT_MODE_ALL -> R.drawable.ic_repeat_all
                else -> R.drawable.ic_repeat_off
            }
        },
        sessionCommand = SessionCommand(ACTION_REPEAT, Bundle.EMPTY)
    ),
//    PREVIOUS(
//        customAction = ACTION_PREVIOUS,
//        displayName = "Previous",
//        iconResId = { R.drawable.ic_skip_previous },
//        sessionCommand = SessionCommand(ACTION_PREVIOUS, Bundle.EMPTY)
//    ),
//    PLAY_AND_PAUSE(
//        customAction = ACTION_PLAY_AND_PAUSE,
//        displayName = "PlayPause",
//        iconResId = { isPlaying ->
//            if (isPlaying == 0) {
//                R.drawable.ic_round_pause
//            } else {
//                R.drawable.ic_round_play_arrow
//            }
//        },
//        sessionCommand = SessionCommand(ACTION_PLAY_AND_PAUSE, Bundle.EMPTY)
//    ),
//    NEXT(
//        customAction = ACTION_NEXT,
//        displayName = "Next",
//        iconResId = { R.drawable.ic_skip_next },
//        sessionCommand = SessionCommand(ACTION_NEXT, Bundle.EMPTY)
//    ),
    SHUFFLE(
        customAction = ACTION_SHUFFLE,
        displayName = "SeekForward",
        iconResId = { shuffleMode ->
            when (shuffleMode) {
                0 -> R.drawable.ic_shuffle_on
                else -> R.drawable.ic_shuffle_off
            }
        },
        sessionCommand = SessionCommand(ACTION_SHUFFLE, Bundle.EMPTY)
    ),

}