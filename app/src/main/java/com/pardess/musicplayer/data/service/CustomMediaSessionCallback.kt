package com.pardess.musicplayer.data.service

import android.os.Bundle
import androidx.annotation.OptIn
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.CommandButton
import androidx.media3.session.MediaSession
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionResult
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import com.pardess.musicplayer.data.service.notification.MediaNotificationManager
import com.pardess.musicplayer.data.service.notification.NotificationCommandButtons
import javax.inject.Inject

internal class CustomMediaSessionCallback : MediaSession.Callback {


    @OptIn(UnstableApi::class)
    override fun onConnect(
        session: MediaSession,
        controller: MediaSession.ControllerInfo
    ): MediaSession.ConnectionResult =
        if (session.isMediaNotificationController(controller)) {
            val sessionCommandBuilder =
                MediaSession.ConnectionResult.DEFAULT_SESSION_COMMANDS.buildUpon()

            // 원하는 순서대로 버튼 리스트 생성
            NotificationCommandButtons.entries.forEach { commandButton ->
                sessionCommandBuilder.add(commandButton.sessionCommand)
            }

            val repeatButton = NotificationCommandButtons.REPEAT.let {
                CommandButton.Builder()
                    .setDisplayName(it.displayName)
                    .setIconResId(it.iconResId(session.player.repeatMode))
                    .setSessionCommand(it.sessionCommand)
                    .build()
            }

            MediaSession.ConnectionResult.AcceptedResultBuilder(session)
                .setAvailableSessionCommands(sessionCommandBuilder.build())
                .setCustomLayout(listOf(repeatButton))
                .build()
        } else {
            MediaSession.ConnectionResult.AcceptedResultBuilder(session).build()
        }


    @OptIn(UnstableApi::class)
    override fun onCustomCommand(
        session: MediaSession,
        controller: MediaSession.ControllerInfo,
        customCommand: SessionCommand,
        args: Bundle
    ): ListenableFuture<SessionResult> {

        println("onCustomCommand: ${customCommand.customAction}")

        when (customCommand.customAction) {
            NotificationCommandButtons.REPEAT.customAction -> {
                var currentRepeatMode = session.player.repeatMode
                println("@@@@ repeatMode: $currentRepeatMode")
                session.player.run {
                    repeatMode = when (currentRepeatMode) {
                        Player.REPEAT_MODE_OFF -> Player.REPEAT_MODE_ONE
                        Player.REPEAT_MODE_ONE -> Player.REPEAT_MODE_ALL
                        Player.REPEAT_MODE_ALL -> Player.REPEAT_MODE_OFF
                        else -> Player.REPEAT_MODE_OFF
                    }
                }
            }

            NotificationCommandButtons.SHUFFLE.customAction -> {
                println("@@@@ SHUFFLE: ${session.player.shuffleModeEnabled}")
                session.player.run {
                    shuffleModeEnabled = !shuffleModeEnabled
                }
//                mediaNotificationManager.updateNotification()
            }
        }

        return Futures.immediateFuture(SessionResult(SessionResult.RESULT_SUCCESS))
    }
}