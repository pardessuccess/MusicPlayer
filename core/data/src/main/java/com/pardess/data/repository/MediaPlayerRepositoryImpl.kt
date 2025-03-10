package com.pardess.data.repository

import android.os.Bundle
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import com.pardess.domain.repository.MediaPlayerListenerRepository
import com.pardess.domain.repository.MediaPlayerRepository
import com.pardess.media_service.MediaControllerManager
import com.pardess.model.PlayerState
import com.pardess.model.Song
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.time.Duration
import javax.inject.Inject

class MediaPlayerRepositoryImpl @Inject constructor(
    private val mediaControllerManager: MediaControllerManager,
    private val mediaPlayerListenerRepository: MediaPlayerListenerRepository
) : MediaPlayerRepository {

    private var mediaController: MediaController? = null

    override fun getPlayerStateFlow(): Flow<PlayerState> {
        return mediaPlayerListenerRepository.getPlayerStateFlow()
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

    override fun play() {
        mediaController?.apply {
            if (connectedToken == null) return
            seekToDefaultPosition(0)
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

    override fun repeat(repeatMode: Int) {
        mediaController?.repeatMode = repeatMode
    }

    override fun shuffle(shuffle: Boolean) {
        mediaController?.shuffleModeEnabled = shuffle
    }

    override fun onSeekingStarted() {
        mediaController?.seekToDefaultPosition()
    }

    override fun onSeekingFinished(duration: Duration) {
        mediaController?.seekTo(duration.toMillis())
    }
}
