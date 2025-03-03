package com.pardess.musicplayer.domain.usecase.media_player

import androidx.media3.common.Player
import androidx.media3.common.Tracks
import com.pardess.musicplayer.data.mapper.toSong
import com.pardess.musicplayer.data.service.MediaControllerManager
import com.pardess.musicplayer.presentation.playback.PlayerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.time.Duration
import javax.inject.Inject

interface MediaPlayerListenerUseCase {
    fun playerStateFlow(): Flow<PlayerState>
}

class MediaPlayerListenerUseCaseImpl @Inject constructor(
    private val mediaControllerManager: MediaControllerManager
) : MediaPlayerListenerUseCase {

    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private var timerJob: Job? = null

    override fun playerStateFlow(): Flow<PlayerState> = callbackFlow {
        val mediaController = mediaControllerManager.mediaControllerFlow.first()
        val currentPlayerUiState = MutableStateFlow(
            if (mediaController.currentMediaItem != null) {
                PlayerState(
                    currentSong = mediaController.currentMediaItem?.toSong(),
                    isLoading = mediaController.isLoading,
                    isPlaying = mediaController.isPlaying,
                    hasNext = mediaController.hasNextMediaItem(),
                    currentPosition = Duration.ofMillis(mediaController.currentPosition),
                    shuffle = mediaController.shuffleModeEnabled,
                    repeatMode = mediaController.repeatMode
                )
            } else {
                PlayerState()
            }
        )

        val playerListener = object : Player.Listener {
            override fun onEvents(player: Player, events: Player.Events) {
                super.onEvents(player, events)
                currentPlayerUiState.update {
                    it.copy(
                        currentSong = mediaController.currentMediaItem?.toSong(),
                        hasNext = mediaController.hasNextMediaItem(),
                        currentPosition = Duration.ofMillis(mediaController.currentPosition),
                        shuffle = mediaController.shuffleModeEnabled,
                        repeatMode = mediaController.repeatMode
                    )
                }
                println("@@@@@ Events ${mediaController.currentMediaItem}")
            }

            override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
                super.onPlayWhenReadyChanged(playWhenReady, reason)
                println("@@@@@ Play when ready changed $playWhenReady")
            }

            override fun onPlaybackStateChanged(state: Int) {
                super.onPlaybackStateChanged(state)
                println("@@@@@ Playback state changed $state")
            }

            override fun onTracksChanged(tracks: Tracks) {
                super.onTracksChanged(tracks)
                println("@@@@@ Tracks changed $tracks")
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                super.onIsPlayingChanged(isPlaying)
                currentPlayerUiState.update { it.copy(isPlaying = isPlaying) }
            }

            override fun onIsLoadingChanged(isLoading: Boolean) {
                super.onIsLoadingChanged(isLoading)
                currentPlayerUiState.update { it.copy(isLoading = isLoading) }
            }

            override fun onPositionDiscontinuity(
                oldPosition: Player.PositionInfo,
                newPosition: Player.PositionInfo,
                reason: Int
            ) {
                super.onPositionDiscontinuity(oldPosition, newPosition, reason)
                if (reason == Player.DISCONTINUITY_REASON_SEEK) {
                    currentPlayerUiState.update {
                        it.copy(currentPosition = Duration.ofMillis(newPosition.positionMs))
                    }
                }
            }

            override fun onRepeatModeChanged(repeatMode: Int) {
                super.onRepeatModeChanged(repeatMode)
                currentPlayerUiState.update { it.copy(repeatMode = repeatMode) }
            }

            override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
                super.onShuffleModeEnabledChanged(shuffleModeEnabled)
                currentPlayerUiState.update { it.copy(shuffle = shuffleModeEnabled) }
            }
        }
        coroutineScope.launch {
            currentPlayerUiState
                .map { it.isLoading.not() && it.isPlaying }
                .distinctUntilChanged()
                .collect { isPlaying ->
                    if (isPlaying) {
                        timerJob = coroutineScope.launch {
                            val startDuration = mediaController.currentPosition
                            val maxDuration = mediaController.contentDuration
                            val playerSpeed = Duration.ofMillis(1000)

                            while (isActive && startDuration <= maxDuration) {
                                delay(playerSpeed.toMillis())
                                // Update time elapsed
                                currentPlayerUiState.update {
                                    it.copy(currentPosition = it.currentPosition + playerSpeed)
                                }
                            }
                        }
                    } else {
                        timerJob?.cancel()
                        timerJob = null
                    }
                }
        }
        coroutineScope.launch {
            currentPlayerUiState
                .onEach { send(it) }.launchIn(this)
        }

        mediaController.addListener(playerListener)

        awaitClose {
            mediaController.removeListener(playerListener)
            coroutineScope.cancel()
        }
    }
}