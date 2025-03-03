//package com.pardess.musicplayer.data.service
//
//import android.app.NotificationManager
//import android.content.Intent
//import android.os.Build
//import androidx.annotation.OptIn
//import androidx.annotation.RequiresApi
//import androidx.media3.common.MediaItem
//import androidx.media3.common.util.UnstableApi
//import androidx.media3.exoplayer.ExoPlayer
//import androidx.media3.session.MediaSession
//import androidx.media3.session.MediaSessionService
//import androidx.media3.session.legacy.PlaybackStateCompat
//import com.google.common.util.concurrent.Futures
//import com.google.common.util.concurrent.ListenableFuture
//import com.pardess.musicplayer.data.datasource.database.dao.FavoriteDao
//import com.pardess.musicplayer.data.service.notification.PlaybackNotificationOver24
//import com.pardess.musicplayer.legacy.MusicController
//import dagger.hilt.android.AndroidEntryPoint
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.Job
//import kotlinx.coroutines.MainScope
//import kotlinx.coroutines.launch
//import javax.inject.Inject
//
//@AndroidEntryPoint
//class MusicService : MediaSessionService(), CoroutineScope by MainScope() {
//
//    @Inject
//    lateinit var mediaSession: MediaSession
//
//    @Inject
//    lateinit var playbackNotification: PlaybackNotificationOver24
//
//    @Inject
//    lateinit var musicController: MusicController
//
//    @Inject
//    lateinit var exoPlayer: ExoPlayer
//
//    @Inject
//    lateinit var favoriteDao: FavoriteDao
//
//    private var lastNotifiedState: String? = null
//
//    override fun onUpdateNotification(session: MediaSession, startInForegroundRequired: Boolean) {
//        if (session.player.mediaMetadata.title == null) {
//            // ✅ 플레이 중이 아니거나 미디어 정보가 없는 경우 노티피케이션 제거
//            stopForeground(true)
//            val notificationManager: NotificationManager =
//                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
//            notificationManager.cancel(NOTIFICATION_ID)
//        } else {
//            // ✅ 플레이 중일 때만 업데이트
//            updateNotification()
//        }
//    }
//
//    private fun updateNotification() {
//        val currentState =
//            "${musicController.getPlayerState()}-${musicController.getCurrentSong()?.title}"
//
//        if (lastNotifiedState != currentState) {
//            val notification = playbackNotification.build()
//            val notificationManager: NotificationManager =
//                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
//            notificationManager.notify(NOTIFICATION_ID, notification)
//            lastNotifiedState = currentState
//        }
//    }
//
//    override fun onCreate() {
//        super.onCreate()
//        playbackNotification.updateMetadata(mediaSession) {
//            updateNotification()
//        }
//        startForeground(NOTIFICATION_ID, playbackNotification.build())
//    }
//
//    private inner class MediaSessionCallback : MediaSession.Callback {
//        override fun onAddMediaItems(
//            mediaSession: MediaSession,
//            controller: MediaSession.ControllerInfo,
//            mediaItems: MutableList<MediaItem>
//        ): ListenableFuture<MutableList<MediaItem>> {
//            val updatedMediaItems = mediaItems.map {
//                it.buildUpon().setUri(it.mediaId).build()
//            }.toMutableList()
//            return Futures.immediateFuture(updatedMediaItems)
//        }
//    }
//
//    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        super.onStartCommand(intent, flags, startId)
//
//        when (intent?.action) {
//            ACTION_QUIT -> {
//                stopForeground(true) // 종료 시 알림 제거
//                stopSelf()
//            }
//
//            ACTION_PAUSE -> {
//                musicController.pause()
//            }
//
//            ACTION_STOP -> {
//                musicController.pause()
//            }
//
//            ACTION_NEXT -> {
//                musicController.skipToNextSong()
//            }
//
//            ACTION_PREVIOUS -> {
//                musicController.skipToPreviousSong()
//            }
//
//            ACTION_FAVORITE -> {
//                val currentSong = musicController.getCurrentSong()
//                currentSong?.let {
//                    launch(Dispatchers.IO) {
//                        println("@@@@@ FAVORITE CLICKED")
//                        favoriteDao.increaseOrInsertFavorite(it.id)
//                    }
//                }
//            }
//        }
//
//        updateNotification()
//        return START_STICKY
//    }
//
//    override fun onDestroy() {
//        mediaSession.run {
//            exoPlayer.release()
//            release()
//        }
//        musicController.destroy()
//        super.onDestroy()
//    }
//
//    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession =
//        mediaSession
//
//    companion object {
//        const val NOTIFICATION_CHANNEL_ID = "playback_notification"
//        const val NOTIFICATION_ID = 1
//
//        const val ACTION_QUIT = "PardessMusic.quitservice"
//        const val ACTION_PAUSE = "PardessMusic.pause"
//        const val ACTION_STOP = "PardessMusic.stop"
//        const val ACTION_TOGGLE_PAUSE = "PardessMusic.togglepause"
//        const val ACTION_NEXT = "PardessMusic.skip"
//        const val ACTION_PREVIOUS = "PardessMusic.rewind"
//        const val ACTION_FAVORITE = "PardessMusic.favorite"
//    }
//
//}