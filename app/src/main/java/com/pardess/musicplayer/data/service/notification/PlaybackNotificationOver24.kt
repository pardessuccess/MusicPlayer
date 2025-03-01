package com.pardess.musicplayer.data.service.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_LOW
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.annotation.DrawableRes
import androidx.annotation.OptIn
import androidx.core.app.NotificationCompat
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaStyleNotificationHelper
import coil3.ImageLoader
import coil3.request.ImageRequest
import coil3.size.Scale
import coil3.toBitmap
import com.pardess.musicplayer.R
import com.pardess.musicplayer.data.service.MusicService
import com.pardess.musicplayer.data.service.MusicService.Companion.ACTION_PREVIOUS
import com.pardess.musicplayer.data.service.MusicService.Companion.ACTION_TOGGLE_PAUSE
import com.pardess.musicplayer.data.service.MusicService.Companion.NOTIFICATION_CHANNEL_ID
import com.pardess.musicplayer.domain.service.MusicController
import com.pardess.musicplayer.presentation.MainActivity
import com.pardess.musicplayer.utils.Utils.isOverM
import com.pardess.musicplayer.utils.isPlaying

class PlaybackNotificationOver24(
    val context: Context,
    private val musicController: MusicController,
) : NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID) {
    val notificationManager: NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {

        println("@@@@@ NOTI INIT")

        val notificationChannel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            "Playback Notification",
            IMPORTANCE_LOW
        ).apply {
            description = "Playback Notification"
            lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
        }
        notificationManager.createNotificationChannel(notificationChannel)
    }

    private fun getNextAction(context: Context): NotificationCompat.Action {
        val actionIntent =
            Intent(context, MusicService::class.java).apply { action = MusicService.ACTION_NEXT }
        val pendingIntent = PendingIntent.getService(context, 0, actionIntent, FLAG_IMMUTABLE)
        return NotificationCompat.Action(R.drawable.ic_skip_next, "Next", pendingIntent)
    }

    private fun getPreviousAction(context: Context): NotificationCompat.Action {
        val actionIntent =
            Intent(context, MusicService::class.java).apply { action = ACTION_PREVIOUS }
        val pendingIntent = PendingIntent.getService(context, 0, actionIntent, FLAG_IMMUTABLE)
        return NotificationCompat.Action(R.drawable.ic_skip_previous, "", pendingIntent)
    }

    @OptIn(UnstableApi::class)
    fun updateMetadata(mediaSession: MediaSession, onUpdate: () -> Unit) {

        val action = Intent(context, MainActivity::class.java)
        action.putExtra(MainActivity.EXPAND_PANEL, true)
        action.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        val clickIntent =
            PendingIntent.getActivity(
                context,
                0,
                action,
                PendingIntent.FLAG_UPDATE_CURRENT or if (isOverM())
                    FLAG_IMMUTABLE
                else 0
            )
        val serviceName = ComponentName(context, MusicService::class.java)
        val intent = Intent(MusicService.ACTION_QUIT)
        intent.component = serviceName
        val deleteIntent = PendingIntent.getService(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or (if (isOverM())
                FLAG_IMMUTABLE
            else 0)
        )

        val isPlaying = musicController.isPlaying()

        val playPauseAction =
            getPlayPauseAction(
                context,
                if (isPlaying) R.drawable.ic_pause_white_48dp else R.drawable.ic_play_arrow_white_48dp
            )
        val previousAction = getPreviousAction(context)
        val nextAction = getNextAction(context)
        setSmallIcon(R.drawable.ic_songs_tab)
        setContentIntent(clickIntent)
        setDeleteIntent(deleteIntent)
        setShowWhen(false)
        addAction(previousAction)
        addAction(playPauseAction)
        addAction(nextAction)
        setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

        setContentTitle(mediaSession.player.mediaMetadata.title)
        setContentText(mediaSession.player.mediaMetadata.artist)
        setSubText(mediaSession.player.mediaMetadata.albumTitle)
        setStyle(
            MediaStyleNotificationHelper.MediaStyle(mediaSession)
                .setShowActionsInCompactView(1, 2, 3)
        )
        val bigNotificationImageSize = 112
        val imageLoader = ImageLoader(context)
        val request = ImageRequest.Builder(context)
            .data(mediaSession.player.mediaMetadata.artworkUri)
            .size(bigNotificationImageSize, bigNotificationImageSize) // 이미지 크기 설정
            .scale(Scale.FILL)
            .target(
                onSuccess = { drawable ->
                    // ✅ 성공 시 Bitmap으로 변환하여 LargeIcon 설정
                    setLargeIcon(drawable.toBitmap())
                    onUpdate()
                },
                onError = {
                    // ✅ 실패 시 기본 이미지로 설정
                    setLargeIcon(
                        BitmapFactory.decodeResource(
                            context.resources,
                            R.drawable.default_audio_art
                        )
                    )
                    onUpdate()
                }
            )
            .build()
        imageLoader.enqueue(request)
    }

    private fun getPlayPauseAction(
        context: Context,
        @DrawableRes playButtonResId: Int
    ): NotificationCompat.Action {
        val actionIntent =
            Intent(context, MusicService::class.java).apply { action = ACTION_TOGGLE_PAUSE }
        val pendingIntent = PendingIntent.getService(context, 0, actionIntent, FLAG_IMMUTABLE)
        return NotificationCompat.Action(playButtonResId, "Play/Pause", pendingIntent)
    }


}