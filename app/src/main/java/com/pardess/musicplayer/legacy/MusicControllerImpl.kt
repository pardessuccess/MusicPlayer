//package com.pardess.musicplayer.data.service
//
//import android.content.ComponentName
//import android.content.Context
//import android.os.Bundle
//import androidx.media3.common.MediaItem
//import androidx.media3.common.MediaMetadata
//import androidx.media3.common.Player
//import androidx.media3.common.util.UnstableApi
//import androidx.media3.session.MediaController
//import androidx.media3.session.SessionToken
//import com.google.common.util.concurrent.ListenableFuture
//import com.google.common.util.concurrent.MoreExecutors
//import com.pardess.musicplayer.data.mapper.toSong
//import com.pardess.musicplayer.domain.model.Song
//import com.pardess.musicplayer.domain.model.enums.PlayerState
//import com.pardess.musicplayer.legacy.MusicController
//import com.pardess.musicplayer.utils.toPlayerState
//
//class MusicControllerImpl(context: Context) : MusicController {
//
//    private var mediaControllerFuture: ListenableFuture<MediaController>
//    private val mediaController: MediaController?
//        get() = if (mediaControllerFuture.isDone) mediaControllerFuture.get() else null
//
//    init {
//        val sessionToken = SessionToken(context, ComponentName(context, MusicService::class.java))
//        mediaControllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
//        mediaControllerFuture.addListener({ controllerListener() }, MoreExecutors.directExecutor())
//
//        println("@@@@@ INIT MusicControllerImpl ")
//    }
//
//    private var currentListener: Player.Listener? = null
//
//    override var mediaControllerCallback: (
//        (
//        playerState: PlayerState,
//        currentMusic: Song?,
//        currentPosition: Long,
//        totalDuration: Long,
//        isShuffleEnabled: Boolean,
//        repeatMode: Int
//    ) -> Unit)? = null
//
//    override var serviceCallback: ((playerState: PlayerState, currentSong: Song?, currentPosition: Long, totalDuration: Long, isShuffleEnabled: Boolean, repeatMode: Int) -> Unit)? =
//        null
//
//    override fun callbackOnes() {
//        mediaController?.let {
//            with(it) {
//                mediaControllerCallback?.invoke(
//                    playbackState.toPlayerState(isPlaying),
//                    currentMediaItem?.toSong(),
//                    currentPosition.coerceAtLeast(0L),
//                    duration.coerceAtLeast(0L),
//                    shuffleModeEnabled,
//                    repeatMode
//
//                )
//            }
//        }
//    }
//
//    override fun controllerListener() {
//        mediaController?.let { controller ->
//            controller.apply {
//                this.addListener(object : Player.Listener {
//                    override fun onEvents(player: Player, events: Player.Events) {
//                        super.onEvents(player, events)
//                        mediaControllerCallback?.invoke(
//                            playbackState.toPlayerState(isPlaying),
//                            currentMediaItem?.toSong(),
//                            currentPosition.coerceAtLeast(0L),
//                            duration.coerceAtLeast(0L),
//                            shuffleModeEnabled,
//                            repeatMode
//                        )
//                    }
//                })
//            }
//
//        }
//    }
//
//    override fun getPlayerState(): PlayerState {
//        return mediaController?.playbackState?.toPlayerState(mediaController?.isPlaying ?: false)
//            ?: PlayerState.STOPPED
//    }
//
//    @UnstableApi
//    override fun addMediaItems(songs: List<Song>) {
//        val mediaItems = songs.map {
//            val extras = Bundle().apply {
//                putInt("trackNumber", it.trackNumber)
//                putInt("year", it.year)
//                putLong("dateModified", it.dateModified)
//                putLong("albumId", it.albumId)
//                putLong("artistId", it.artistId)
//                putString("composer", it.composer)
//                putString("albumArtist", it.albumArtist)
//                putBoolean("favorite", it.favorite)
//                putLong("id", it.id)
//            }
//            MediaItem.Builder().setMediaId(it.data)
//                .setUri(it.data)
//                .setMediaMetadata(
//                    MediaMetadata.Builder()
//                        .setTitle(it.title)
//                        .setArtist(it.artistName)
//                        .setAlbumTitle(it.albumName)
//                        .setComposer(it.composer)
//                        .setDurationMs(it.duration.toMillis())
//                        .setExtras(extras)
//                        .build()
//                ).build()
//        }
//        mediaController?.setMediaItems(mediaItems)
//    }
//
//    override fun play(index: Int) {
//        mediaController?.apply {
//            seekToDefaultPosition(index)
//            playWhenReady = true
//            prepare()
//        }
//    }
//
//    override fun resume() {
//        mediaController?.play()
//    }
//
//    override fun pause() {
//        mediaController?.pause()
//    }
//
//    override fun getCurrentPosition(): Long {
//        return mediaController?.currentPosition ?: 0L
//    }
//
//    override fun destroy() {
//        mediaController?.release()
//    }
//
//    override fun skipToNextSong() {
//        mediaController?.seekToNext()
//    }
//
//    override fun skipToPreviousSong() {
//        mediaController?.seekToPrevious()
//    }
//
//    override fun getCurrentSong(): Song? {
//        return mediaController?.currentMediaItem?.toSong()
//    }
//
//    override fun seekTo(position: Long) {
//        mediaController?.seekTo(position)
//    }
//
//
//    override fun setShuffleModeEnabled(shuffleMode: Boolean) {
//        mediaController?.shuffleModeEnabled = shuffleMode
//    }
//
//    override fun setRepeatMode(repeatMode: Int) {
//        mediaController?.repeatMode = repeatMode
//    }
//}