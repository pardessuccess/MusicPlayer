package com.pardess.media_service

import android.content.Intent
import android.os.Bundle
import androidx.annotation.OptIn
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.CommandButton
import androidx.media3.session.MediaNotification
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.google.common.collect.ImmutableList
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@UnstableApi
@AndroidEntryPoint
class MediaPlayerService : MediaSessionService() {

    @Inject
    lateinit var notifier: Notifier

    @Inject
    lateinit var mediaNotificationManager: MediaNotificationManager

    @Inject
    lateinit var mediaSession: MediaSession

    // 마지막에 사용한 ActionFactory를 저장할 필드
    private var lastActionFactory: MediaNotification.ActionFactory? = null

    @OptIn(UnstableApi::class)
    override fun onCreate() {
        super.onCreate()
        println("@@@ MediaPlayerService onCreate" + { mediaSession })
        setMediaNotificationProvider(object : MediaNotification.Provider {
            override fun createNotification(
                mediaSession: MediaSession,
                mediaButtonPreferences: ImmutableList<CommandButton>,
                actionFactory: MediaNotification.ActionFactory,
                onNotificationChangedCallback: MediaNotification.Provider.Callback
            ): MediaNotification {
                lastActionFactory = actionFactory
                return notifier.createMediaNotification(actionFactory)
            }

            override fun handleCustomCommand(
                session: MediaSession,
                action: String,
                extras: Bundle
            ): Boolean = false
        })
    }
//
//    @OptIn(UnstableApi::class)
//    override fun onUpdateNotification(session: MediaSession, startInForegroundRequired: Boolean) {
//        // 저장된 ActionFactory를 사용해 최신 알림 생성
//        lastActionFactory?.let { actionFactory ->
//            val updatedNotification = notifier.createMediaNotification(actionFactory)
//            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//            if (startInForegroundRequired) {
//                // 포그라운드 상태로 업데이트
//                startForeground(MediaNotificationManager.NOTIFICATION_CHANNEL_ID, updatedNotification.notification)
//            } else {
//                notificationManager.notify(MediaNotificationManager.NOTIFICATION_CHANNEL_ID, updatedNotification.notification)
//            }
//        }
//    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        val player = mediaSession.player
        if (!player.playWhenReady ||
            player.mediaItemCount == 0 ||
            player.playbackState == Player.STATE_ENDED
        ) {
            // Stop the service if not playing, continue playing in the background
            // otherwise.
            stopSelf()
        }
    }

    override fun onDestroy() {
        mediaSession.run {
            player.release()
            release()
        }
        super.onDestroy()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession =
        mediaSession


}