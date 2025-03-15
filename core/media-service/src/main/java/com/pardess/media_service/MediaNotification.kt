package com.pardess.media_service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaNotification
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaStyleNotificationHelper
import javax.inject.Inject

interface Notifier {
    @UnstableApi
    fun createMediaNotification(
        actionFactory: MediaNotification.ActionFactory,
    ): MediaNotification
}

class MediaNotificationManager @Inject constructor(
    private val context: Context,
    private val mediaSession: MediaSession,
    private val pendingIntentProvider: PendingIntentProvider
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
            setContentIntent(pendingIntentProvider.getMainActivityPendingIntent(context))
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
                NotificationCommandButtons.FAVORITE
            )
            orderedButtons.forEach { commandButton ->
                addAction(
                    actionFactory.createCustomAction(
                        mediaSession,
                        IconCompat.createWithResource(
                            context,
                            commandButton.iconResId
                        ),
                        commandButton.displayName,
                        commandButton.customAction,
                        commandButton.sessionCommand.customExtras
                    )
                )
            }
        }
        return MediaNotification(
            NOTIFICATION_CHANNEL_ID,
            notificationBuilder.build()
        )
    }
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