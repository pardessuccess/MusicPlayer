package com.pardess.playback

import androidx.lifecycle.viewModelScope
import com.pardess.common.base.BaseUiEffect
import com.pardess.common.base.BaseUiEvent
import com.pardess.common.base.BaseUiState
import com.pardess.common.base.BaseViewModel
import com.pardess.domain.usecase.playback.PlaybackUseCase
import com.pardess.model.PlayerState
import com.pardess.model.Song
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Duration
import javax.inject.Inject


sealed class PlaybackEvent : BaseUiEvent {
    data class PlaySong(
        val index: Int,
        val playlist: List<Song>
    ) : PlaybackEvent()

    object PlayRandom : PlaybackEvent()
    object PauseSong : PlaybackEvent()
    object ResumeSong : PlaybackEvent()
    object SkipToNextSong : PlaybackEvent()
    object SkipToPreviousSong : PlaybackEvent()
    data class RepeatMode(val repeatMode: Int) : PlaybackEvent()
    data class ShuffleMode(val shuffle: Boolean) : PlaybackEvent()
    data class SeekSongToPosition(val position: Long) : PlaybackEvent()
    object PlaybackExpand : PlaybackEvent()
    object Favorite : PlaybackEvent()
}


data class PlaybackUiState(
    val playerState: PlayerState = PlayerState(),
    val currentPlaylist: List<Song> = emptyList(),
    val expand: Boolean = false,
) : BaseUiState

enum class RepeatMode(val value: Int) {
    REPEAT_OFF(0),
    REPEAT_ONE(1),
    REPEAT_ALL(2);

    companion object {
        fun fromValue(value: Int): RepeatMode {
            return entries.first { it.value == value }
        }

        fun next(mode: RepeatMode): RepeatMode {
            return when (mode) {
                REPEAT_OFF -> REPEAT_ALL
                REPEAT_ALL -> REPEAT_ONE
                REPEAT_ONE -> REPEAT_OFF
            }
        }
    }
}

sealed class PlaybackUiEffect : BaseUiEffect {
    object SongChanged : PlaybackUiEffect()
    object FavoriteUpdated : PlaybackUiEffect()
}

@HiltViewModel
class PlaybackViewModel @Inject constructor(
    private val useCase: PlaybackUseCase
) : BaseViewModel<PlaybackUiState, PlaybackEvent, PlaybackUiEffect>(PlaybackUiState()) {

        private val playerState: StateFlow<PlayerState> = useCase.playerStateFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = PlayerState()
        )

    val allSongs = useCase.getAllSongs()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    override fun onEvent(event: PlaybackEvent) {
        when (event) {
            is PlaybackEvent.PlaySong -> {
                viewModelScope.launch {
                    useCase.onPlay(event.index, event.playlist)
                }
            }

            PlaybackEvent.PauseSong -> useCase.onPause()
            PlaybackEvent.ResumeSong -> useCase.onResume()
            PlaybackEvent.SkipToNextSong -> useCase.onSkipToNext()
            PlaybackEvent.SkipToPreviousSong -> useCase.onSkipToPrevious()
            is PlaybackEvent.RepeatMode -> {
                viewModelScope.launch {
                    useCase.onRepeatMode(event.repeatMode)
                }
            }

            is PlaybackEvent.ShuffleMode -> {
                viewModelScope.launch {
                    useCase.onShuffleMode(event.shuffle)
                }
            }

            is PlaybackEvent.SeekSongToPosition -> useCase.onSeekPosition(Duration.ofMillis(event.position))
            PlaybackEvent.PlaybackExpand -> updateState { copy(expand = !expand) }
            PlaybackEvent.Favorite -> {
                viewModelScope.launch {
                    playerState.value.currentSong?.let { useCase.saveFavoriteSong(it) }
                }
            }

            PlaybackEvent.PlayRandom -> {
                viewModelScope.launch {
                    useCase.onPlayRandom()
                }
            }
        }
    }

    private var currentSongId: Long? = null
    private var historyUpdated = false
    private var playCountUpdated = false

    init {
        useCase.initMediaController()
        collectState(playerState) { newPlayerState ->
            viewModelScope.launch {
                val result = useCase.handlePlaybackState(
                    currentUiState = uiState.value.playerState,
                    newPlayerState = newPlayerState,
                    currentSongId = currentSongId,
                    historyUpdated = historyUpdated,
                    playCountUpdated = playCountUpdated
                )
                println(playerState.toString())

                // 업데이트된 플래그 반영
                currentSongId = result.currentSongId
                historyUpdated = result.historyUpdated
                playCountUpdated = result.playCountUpdated
            }
            copy(playerState = newPlayerState)
        }
    }
}
