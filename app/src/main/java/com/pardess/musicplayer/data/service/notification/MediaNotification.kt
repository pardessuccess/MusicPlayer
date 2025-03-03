package com.pardess.musicplayer.data.service.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaNotification
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaStyleNotificationHelper
import com.pardess.musicplayer.R
//import com.pardess.musicplayer.data.service.MusicService.Companion.NOTIFICATION_CHANNEL_ID
import com.pardess.musicplayer.presentation.MainActivity
import javax.inject.Inject

interface Notifier {
    @UnstableApi
    fun createMediaNotification(
        actionFactory: MediaNotification.ActionFactory,
    ): MediaNotification
}

class MediaNotificationManager @Inject constructor(
    private val context: Context,
    private val mediaSession: MediaSession
) : Notifier {

    companion object {
        const val NOTIFICATION_CHANNEL_ID = 100
    }

    @UnstableApi
    override fun createMediaNotification(actionFactory: MediaNotification.ActionFactory): MediaNotification {
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        ensureNotificationChannel(notificationManager)

        val mediaItem = mediaSession.player.currentMediaItem

        val notificationBuilder = NotificationCompat.Builder(
            context,
            NOTIFICATION_CHANNEL_ID.toString()
        ).apply {
            priority = NotificationCompat.PRIORITY_DEFAULT
            setSmallIcon(R.drawable.ic_songs_tab)
            setContentTitle(mediaItem?.mediaMetadata?.title)
            setContentIntent(createNotifyPendingIntent())
            setDeleteIntent(
                actionFactory.createMediaActionPendingIntent(
                    mediaSession,
                    Player.COMMAND_STOP.toLong()
                )
            )
            setStyle(
                MediaStyleNotificationHelper.MediaStyle(mediaSession)
                    .setShowActionsInCompactView(0, 1, 2)
            )
            setOngoing(false)

            val orderedButtons = listOf(
                NotificationCommandButtons.SHUFFLE,
                NotificationCommandButtons.REPEAT
            )
            orderedButtons.forEach { commandButton ->
                addAction(
                    actionFactory.createCustomAction(
                        mediaSession,
                        IconCompat.createWithResource(
                            context,
                            when (commandButton) {
                                NotificationCommandButtons.REPEAT -> commandButton.iconResId(
                                    mediaSession.player.repeatMode
                                )

                                NotificationCommandButtons.SHUFFLE -> commandButton.iconResId(if (mediaSession.player.shuffleModeEnabled) 0 else 1)
                            }
                        ),
                        commandButton.displayName,
                        commandButton.customAction,
                        commandButton.sessionCommand.customExtras
                    )
                )
            }
        }
        return MediaNotification(
            NOTIFICATION_CHANNEL_ID.toInt(),
            notificationBuilder.build()
        )
    }


    private fun createNotifyPendingIntent(): PendingIntent =
        PendingIntent.getActivity(
            context,
            0,
            Intent().apply {
                action = Intent.ACTION_VIEW
                component = ComponentName(context, MainActivity::class.java)
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

    private fun ensureNotificationChannel(notificationManager: NotificationManager) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O ||
            notificationManager.getNotificationChannel(NOTIFICATION_CHANNEL_ID.toString()) != null
        ) {
            return
        }

        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID.toString(),
            "Playback Notification",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "MediaPlayer"
            lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
            importance = NotificationManager.IMPORTANCE_LOW
        }

        notificationManager.createNotificationChannel(channel)
    }


}