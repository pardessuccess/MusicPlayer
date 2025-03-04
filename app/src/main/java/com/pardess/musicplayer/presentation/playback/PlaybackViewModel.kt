package com.pardess.musicplayer.presentation.playback

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pardess.musicplayer.data.Result
import com.pardess.musicplayer.domain.model.Song
import com.pardess.musicplayer.domain.repository.ManageRepository
import com.pardess.musicplayer.domain.repository.MusicRepository
import com.pardess.musicplayer.domain.repository.PrefRepository
import com.pardess.musicplayer.domain.usecase.media_player.MediaPlayerUseCase
import com.pardess.musicplayer.presentation.toEntity
import com.pardess.musicplayer.utils.Utils.rearrangeList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Duration
import javax.inject.Inject

@HiltViewModel
class PlaybackViewModel @Inject constructor(
    private val musicRepository: MusicRepository,
    private val manageRepository: ManageRepository,
    private val prefRepository: PrefRepository,
    private val useCase: MediaPlayerUseCase
) : ViewModel() {

    private var _uiState = MutableStateFlow(PlaybackUiState())
    val uiState = _uiState.asStateFlow()

    private val playerState: StateFlow<PlayerState> = useCase.playerStateFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = PlayerState()
        )

    val allSongs = musicRepository.getSongs().map { result ->
        if (result is Result.Error) {
            return@map emptyList<Song>()
        }
        result.data!!
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun onEvent(event: PlaybackEvent) {
        when (event) {
            is PlaybackEvent.PlaySong -> {
                viewModelScope.launch {
                    val shuffle =
                        prefRepository.getShuffleMode().first()  // Flow에서 마지막 값 가져오기
                    val repeatMode = prefRepository.getRepeatMode().first()
                    useCase.shuffle(shuffle)
                    useCase.repeat(repeatMode)
                }
                useCase.play(event.index)
                rearrangeList(event.playlist, event.index).let { useCase.addMediaItems(it) }
            }

            PlaybackEvent.PauseSong -> useCase.pause()
            PlaybackEvent.ResumeSong -> useCase.resume()
            PlaybackEvent.SkipToNextSong -> useCase.next()
            PlaybackEvent.SkipToPreviousSong -> useCase.previous()
            is PlaybackEvent.RepeatMode -> useCase.repeat(event.repeatMode)
            is PlaybackEvent.ShuffleMode -> useCase.shuffle(event.shuffle)
            is PlaybackEvent.SeekSongToPosition -> useCase.onSeekingFinished(Duration.ofMillis(event.position))
            PlaybackEvent.ExpandPanel -> _uiState.update { it.copy(expand = !it.expand) }
            PlaybackEvent.Favorite -> {
                _uiState.value.playerState.currentSong?.let { song ->
                    viewModelScope.launch {
                        manageRepository.upsertFavorite(song.toEntity())
                    }
                }
            }

            PlaybackEvent.PlayRandom -> {}
        }
    }

    private var currentSongId: Long? = null
    private var historyUpdated = false
    private var playCountUpdated = false

    init {
        useCase.initMediaController()
        viewModelScope.launch {
            playerState.collectLatest { playerState ->
                _uiState.update { it.copy(playerState = playerState) }
                val currentSong = playerState.currentSong

                if (uiState.value.playerState.shuffle != playerState.shuffle) {
                    prefRepository.setShuffleMode(playerState.shuffle)
                }

                if (uiState.value.playerState.repeatMode != playerState.repeatMode) {
                    prefRepository.setRepeatMode(playerState.repeatMode)
                }

                if (currentSong != null) {
                    // 새로운 곡이 시작되었을 경우 update 플래그 리셋
                    if (currentSongId != currentSong.id) {
                        currentSongId = currentSong.id
                        historyUpdated = false
                        playCountUpdated = false
                    }
                    val historyThresholdMillis = 5_000L // 5초
                    val playCountThresholdMillis = currentSong.duration.toMillis() / 10

                    if (!historyUpdated && playerState.currentPosition.toMillis() >= historyThresholdMillis) {
                        historyUpdated = true
                        viewModelScope.launch {
                            // 곡 재생 히스토리 추가 (manageRepository에 메서드 구현 필요)
                            manageRepository.insertHistory(
                                currentSong.toEntity(),
                                System.currentTimeMillis()
                            )
                        }
                    }

                    // 재생 카운트 업데이트: 아직 카운트 증가하지 않았고, 재생 위치가 전체 길이의 50% 이상이면 업데이트
                    if (!playCountUpdated && playerState.currentPosition.toMillis() >= playCountThresholdMillis) {
                        playCountUpdated = true
                        viewModelScope.launch {
                            // 재생 카운트 증가 (manageRepository에 메서드 구현 필요)
                            manageRepository.upsertPlayCount(currentSong.toEntity())
                        }
                    }
                }
            }
        }
    }
}