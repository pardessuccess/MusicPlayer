package com.pardess.media_service

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
import com.pardess.database.dao.FavoriteDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

class CustomMediaSessionCallback @Inject constructor(
    private val favoriteDao: FavoriteDao
) : MediaSession.Callback {

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

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

            val favoriteButton = NotificationCommandButtons.FAVORITE.let {
                CommandButton.Builder()
                    .setDisplayName(it.displayName)
                    .setIconResId(it.iconResId)
                    .setSessionCommand(it.sessionCommand)
                    .build()
            }

            MediaSession.ConnectionResult.AcceptedResultBuilder(session)
                .setAvailableSessionCommands(sessionCommandBuilder.build())
                .setCustomLayout(listOf(favoriteButton))
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
            NotificationCommandButtons.FAVORITE.customAction -> {
                scope.launch {
                    session.player.currentMediaItem?.mediaMetadata?.extras?.getLong("id")
                        ?.let { favoriteDao.increaseOrInsertFavorite(it) }
                }
            }
        }
        return Futures.immediateFuture(SessionResult(SessionResult.RESULT_SUCCESS))
    }

    override fun onDisconnected(session: MediaSession, controller: MediaSession.ControllerInfo) {
        super.onDisconnected(session, controller)
        scope.cancel()
    }

}