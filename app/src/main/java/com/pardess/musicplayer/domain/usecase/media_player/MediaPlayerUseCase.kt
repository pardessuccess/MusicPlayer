package com.pardess.musicplayer.domain.usecase.media_player

import android.os.Bundle
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import com.pardess.musicplayer.domain.model.Song
import com.pardess.musicplayer.data.service.MediaControllerManager
import com.pardess.musicplayer.presentation.playback.PlayerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.time.Duration
import javax.inject.Inject

interface MediaPlayerUseCase {
    fun playerStateFlow(): Flow<PlayerState>
    fun initMediaController()
    fun addMediaItems(songs: List<Song>)
    fun play(index: Int)
    fun resume()
    fun pause()
    fun stop()
    fun next()
    fun previous()
    fun repeat()
    fun shuffle()
    fun onSeekingStarted()
    fun onSeekingFinished(duration: Duration)

}

class MediaPlayerUseCaseImpl @Inject constructor(
    private val mediaControllerManager: MediaControllerManager,
    private val mediaPlayerListenerUseCase: MediaPlayerListenerUseCase
) : MediaPlayerUseCase {

    private var mediaController: MediaController? = null

    override fun playerStateFlow(): Flow<PlayerState> {
        return mediaPlayerListenerUseCase.playerStateFlow()
    }

    override fun initMediaController() {
        mediaControllerManager.mediaControllerFlow
            .onEach { controller -> mediaController = controller }
            .launchIn(CoroutineScope(Dispatchers.Main))
    }

    @OptIn(UnstableApi::class)
    override fun addMediaItems(songs: List<Song>) {
        val mediaItems = songs.map {
            val extras = Bundle().apply {
                putInt("trackNumber", it.trackNumber)
                putInt("year", it.year)
                putLong("dateModified", it.dateModified)
                putLong("albumId", it.albumId)
                putLong("artistId", it.artistId)
                putString("composer", it.composer)
                putString("albumArtist", it.albumArtist)
                putBoolean("favorite", it.favorite)
                putLong("id", it.id)
            }
            MediaItem.Builder().setMediaId(it.data)
                .setUri(it.data)
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setTitle(it.title)
                        .setArtist(it.artistName)
                        .setAlbumTitle(it.albumName)
                        .setComposer(it.composer)
                        .setDurationMs(it.duration.toMillis())
                        .setExtras(extras)
                        .build()
                ).build()
        }
        mediaController?.setMediaItems(mediaItems)
    }

    override fun play(index: Int) {
        mediaController?.apply {
            seekToDefaultPosition(index)
            playWhenReady = true
            prepare()
        }
    }

    override fun resume() {
        mediaController?.play()
    }

    override fun pause() {
        mediaController?.pause()
    }

    override fun stop() {
        mediaController?.stop()
        mediaController?.release()
    }

    override fun next() {
        if (mediaController?.hasNextMediaItem() == true) {
            mediaController?.seekToNextMediaItem()
        }
    }

    override fun previous() {
        if (mediaController?.hasPreviousMediaItem() == true) {
            mediaController?.seekToPreviousMediaItem()
        } else {
            mediaController?.seekToDefaultPosition()
        }
    }

    override fun repeat() {
        mediaController?.repeatMode = when (mediaController?.repeatMode) {
            MediaController.REPEAT_MODE_ONE -> MediaController.REPEAT_MODE_ALL
            MediaController.REPEAT_MODE_ALL -> MediaController.REPEAT_MODE_OFF
            else -> MediaController.REPEAT_MODE_ONE
        }
    }

    override fun shuffle() {
        mediaController?.shuffleModeEnabled = !mediaController?.shuffleModeEnabled!!
    }

    override fun onSeekingStarted() {
        mediaController?.seekToDefaultPosition()
    }

    override fun onSeekingFinished(duration: Duration) {
        mediaController?.seekTo(duration.toMillis())
    }

}