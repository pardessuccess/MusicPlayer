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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
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

    private var _playbackUiState = MutableStateFlow(PlaybackUiState())
    val playbackUiState = _playbackUiState.asStateFlow()

    private var currentSongId: Long? = null
    private var historyUpdated = false
    private var playCountUpdated = false

    private val playerState: StateFlow<PlayerState> = useCase.playerStateFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = PlayerState()
        )

    val allSongState = musicRepository.getSongs().map { result ->
        if (result is Result.Error) {
            return@map emptyList<Song>()
        }
        result.data!!
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = emptyList()
    )

    fun onEvent(event: PlaybackEvent) {
        when (event) {
            is PlaybackEvent.PlaySong -> {
                useCase.play(event.index)
                rearrangeList(event.playlist, event.index).let { useCase.addMediaItems(it) }
            }
            PlaybackEvent.PauseSong -> useCase.pause()
            PlaybackEvent.ResumeSong -> useCase.resume()
            PlaybackEvent.SkipToNextSong -> useCase.next()
            PlaybackEvent.SkipToPreviousSong -> useCase.previous()
            is PlaybackEvent.RepeatMode -> useCase.repeat()
            PlaybackEvent.ShuffleMode -> useCase.shuffle()
            is PlaybackEvent.SeekSongToPosition -> useCase.onSeekingFinished(Duration.ofMillis(event.position))
            PlaybackEvent.ExpandPanel -> _playbackUiState.update { it.copy(expand = !it.expand) }
            PlaybackEvent.ClickFavorite -> {
                _playbackUiState.value.playerState.currentSong?.let { song ->
                    viewModelScope.launch {
                        manageRepository.upsertFavorite(song.toEntity())
                    }
                }
            }
            PlaybackEvent.PlayRandom -> {}
        }
    }

    init {
        useCase.initMediaController()
        viewModelScope.launch {
            playerState.collectLatest { playerState ->
                println("@@@@@ playerStateFlow $playerState")
                _playbackUiState.update { it.copy(playerState = playerState) }
            }
        }
    }
}


fun <T> rearrangeList(list: List<T>, index: Int): List<T> {
    if (index < 0 || index >= list.size) return list // 유효하지 않은 인덱스 처리

    val front = list.subList(0, index) // 앞쪽 리스트 (index 앞 요소들)
    val back = list.subList(index, list.size) // index 포함 뒷쪽 리스트

    return back + front // 뒷쪽 리스트에 앞쪽 리스트를 붙여서 반환
}