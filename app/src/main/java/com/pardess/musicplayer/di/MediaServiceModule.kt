package com.pardess.musicplayer.di

import android.content.ComponentName
import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaController
import androidx.media3.session.MediaSession
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.pardess.musicplayer.data.service.MediaPlayerService
import com.pardess.musicplayer.data.service.CustomMediaSessionCallback
import com.pardess.musicplayer.data.service.ConnectedMediaController
import com.pardess.musicplayer.data.service.MediaControllerManager
import com.pardess.musicplayer.data.service.notification.MediaNotificationManager
import com.pardess.musicplayer.data.service.notification.Notifier
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class MediaServiceBinds {

    @OptIn(UnstableApi::class)
    @Binds
    abstract fun bindsNotifier(
        mediaNotification: MediaNotificationManager
    ): Notifier

    @Binds
    abstract fun bindsMediaControllerManager(
        mediaControllerManager: ConnectedMediaController
    ): MediaControllerManager
}

@Module
@InstallIn(SingletonComponent::class)
object MediaServiceModule {

    @Singleton
    @Provides
    fun providesAudioAttributes() = AudioAttributes.Builder()
        .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
        .setUsage(C.USAGE_MEDIA)
        .build()

    @Singleton
    @Provides
    fun providesExoPlayer(
        @ApplicationContext context: Context,
        audioAttributes: AudioAttributes
    ): ExoPlayer {
        return ExoPlayer.Builder(context)
            .setAudioAttributes(audioAttributes, true)
            .build()
    }

    @Singleton
    @Provides
    fun providesMediaSession(
        @ApplicationContext context: Context,
        exoPlayer: ExoPlayer
    ): MediaSession {
        return MediaSession.Builder(context, exoPlayer)
            .setCallback(CustomMediaSessionCallback())
            .build()
    }

    @Singleton
    @Provides
    fun providesMediaNotificationManager(
        @ApplicationContext context: Context,
        mediaSession: MediaSession
    ): MediaNotificationManager {
        return MediaNotificationManager(context, mediaSession)
    }

    @OptIn(UnstableApi::class)
    @Singleton
    @Provides
    fun providesSessionToken(
        @ApplicationContext context: Context
    ): SessionToken =
        SessionToken(context, ComponentName(context, MediaPlayerService::class.java))

    @Singleton
    @Provides
    fun providesListenableFutureMediaController(
        @ApplicationContext context: Context,
        sessionToken: SessionToken
    ): ListenableFuture<MediaController> =
        MediaController
            .Builder(context, sessionToken)
            .buildAsync()


}