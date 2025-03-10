package com.pardess.data.repository

import androidx.media3.common.Player
import com.pardess.data.mapper.toSong
import com.pardess.domain.repository.MediaPlayerListenerRepository
import com.pardess.media_service.MediaControllerManager
import com.pardess.model.PlayerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.time.Duration
import javax.inject.Inject

class MediaPlayerListenerRepositoryImpl @Inject constructor(
    private val mediaControllerManager: MediaControllerManager
): MediaPlayerListenerRepository {

    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private var timerJob: Job? = null
    private val _isSessionReady = MutableStateFlow(false)
    override val isSessionReady: Flow<Boolean> = _isSessionReady.asStateFlow()

    override fun getPlayerStateFlow(): Flow<PlayerState> = callbackFlow {
        // 최초 MediaController 획득
        val mediaController = mediaControllerManager.mediaControllerFlow.first()
        // 초기 플레이어 상태 생성
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

        // 플레이어 이벤트 리스너
        val playerListener = object : Player.Listener {
            override fun onEvents(player: Player, events: Player.Events) {
                currentPlayerUiState.update {
                    it.copy(
                        currentSong = mediaController.currentMediaItem?.toSong(),
                        hasNext = mediaController.hasNextMediaItem(),
                        currentPosition = Duration.ofMillis(mediaController.currentPosition),
                        shuffle = mediaController.shuffleModeEnabled,
                        repeatMode = mediaController.repeatMode
                    )
                }
            }

            override fun onPlaybackStateChanged(state: Int) {
                _isSessionReady.value = (state == Player.STATE_READY)
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                currentPlayerUiState.update { it.copy(isPlaying = isPlaying) }
            }

            override fun onIsLoadingChanged(isLoading: Boolean) {
                currentPlayerUiState.update { it.copy(isLoading = isLoading) }
            }

            override fun onPositionDiscontinuity(
                oldPosition: Player.PositionInfo,
                newPosition: Player.PositionInfo,
                reason: Int
            ) {
                if (reason == Player.DISCONTINUITY_REASON_SEEK) {
                    currentPlayerUiState.update {
                        it.copy(currentPosition = Duration.ofMillis(newPosition.positionMs))
                    }
                }
            }

            override fun onRepeatModeChanged(repeatMode: Int) {
                currentPlayerUiState.update { it.copy(repeatMode = repeatMode) }
            }

            override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
                currentPlayerUiState.update { it.copy(shuffle = shuffleModeEnabled) }
            }
        }

        // 타이머 작업: 재생 중이면 일정 주기로 진행 시간 업데이트
        coroutineScope.launch {
            currentPlayerUiState
                .map { !it.isLoading && it.isPlaying }
                .distinctUntilChanged()
                .collect { isPlaying ->
                    if (isPlaying) {
                        timerJob = coroutineScope.launch {
                            val playerSpeed = Duration.ofMillis(1000)
                            while (isActive && mediaController.currentPosition <= mediaController.contentDuration) {
                                delay(playerSpeed.toMillis())
                                currentPlayerUiState.update {
                                    it.copy(currentPosition = it.currentPosition.plus(playerSpeed))
                                }
                            }
                        }
                    } else {
                        timerJob?.cancel()
                        timerJob = null
                    }
                }
        }

        // Flow 업데이트: currentPlayerUiState 값을 방출
        coroutineScope.launch {
            currentPlayerUiState.collect { send(it) }
        }

        mediaController.addListener(playerListener)

        awaitClose {
            mediaController.removeListener(playerListener)
            coroutineScope.cancel()
        }
    }

}