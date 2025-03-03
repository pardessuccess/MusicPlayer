//package com.pardess.musicplayer.legacy
//
//import dagger.Module
//import dagger.hilt.InstallIn
//import dagger.hilt.android.components.ServiceComponent
//
//
//@Module
//@InstallIn(ServiceComponent::class)
//object ServiceModule {
////
////    @ServiceScoped
////    @Provides
////    fun provideAudioAttributes() = AudioAttributes.Builder()
////        .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
////        .setUsage(C.USAGE_MEDIA)
////        .build()
////
////    @ServiceScoped
////    @Provides
////    fun provideMediaSession(
////        @ApplicationContext context: Context,
////        exoPlayer: ExoPlayer
////    ): MediaSession = MediaSession.Builder(context, exoPlayer)
////        .setCallback(CustomMediaSessionCallback())
////        .build()
////
////
////    @ServiceScoped
////    @Provides
////    fun provideExoPlayer(
////        @ApplicationContext context: Context,
////        audioAttributes: AudioAttributes
////    ) = ExoPlayer.Builder(context).build().apply {
////        setAudioAttributes(audioAttributes, true)
////        setHandleAudioBecomingNoisy(true)
////    }
//
//}