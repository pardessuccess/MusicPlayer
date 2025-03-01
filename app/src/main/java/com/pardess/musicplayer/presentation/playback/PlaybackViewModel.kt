package com.pardess.musicplayer.presentation.playback

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pardess.musicplayer.data.Result
import com.pardess.musicplayer.domain.model.Song
import com.pardess.musicplayer.domain.model.enums.PlayerState
import com.pardess.musicplayer.domain.model.state.RepeatMode
import com.pardess.musicplayer.domain.repository.ManageRepository
import com.pardess.musicplayer.domain.repository.MusicRepository
import com.pardess.musicplayer.domain.repository.PrefRepository
import com.pardess.musicplayer.domain.service.MusicController
import com.pardess.musicplayer.presentation.toEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class PlaybackViewModel @Inject constructor(
    private val musicController: MusicController,
    private val musicRepository: MusicRepository,
    private val manageRepository: ManageRepository,
    private val prefRepository: PrefRepository
) : ViewModel() {

    private var _playbackUiState = MutableStateFlow(PlaybackUiState())
    val playbackUiState = _playbackUiState.asStateFlow()

    private var currentSongId: Long? = null
    private var historyUpdated = false
    private var playCountUpdated = false

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

    private fun setMediaControllerCallback() {
        musicController.mediaControllerCallback =
            { playerState, currentSong, currentPosition, totalDuration, shuffleModeEnabled, repeatMode ->

                // 노래가 바뀌면 플래그 초기화
                if (currentSong?.id != currentSongId) {
                    currentSongId = currentSong?.id
                    historyUpdated = false
                    playCountUpdated = false
                }

                if (_playbackUiState.value.playState.isShuffleEnabled != shuffleModeEnabled) {
                    _playbackUiState.value = _playbackUiState.value.copy(
                        playState = _playbackUiState.value.playState.copy(isShuffleEnabled = shuffleModeEnabled)
                    )
                    viewModelScope.launch {
                        prefRepository.setShuffleMode(shuffleModeEnabled)
                    }
                }

                if (_playbackUiState.value.playState.repeatMode != repeatMode) {
                    _playbackUiState.value = _playbackUiState.value.copy(
                        playState = _playbackUiState.value.playState.copy(repeatMode = repeatMode)
                    )

                    // 상태가 변경된 경우에만 PrefRepository에 저장
                    viewModelScope.launch {
                        prefRepository.setRepeatMode(repeatMode)
                    }
                }

                // 10초 이상 재생되었으면 history 업데이트 (한 번만)
                if (currentSong != null && !historyUpdated && currentPosition >= 10_000L) {
                    historyUpdated = true
                    viewModelScope.launch {
                        manageRepository.insertHistory(
                            currentSong.toEntity(),
                            System.currentTimeMillis()
                        )
                    }
                }

                // 노래가 거의 끝났을 때(예: 전체 길이에서 5초 이하 남았을 때) playCount 업데이트 (한 번만)
                if (currentSong != null && !playCountUpdated && totalDuration > 0 && (totalDuration - currentPosition) <= 5000L) {
                    playCountUpdated = true
                    viewModelScope.launch {
                        manageRepository.upsertPlayCount(currentSong.toEntity())
                    }
                }

                println("@@@@@" + currentSong.toString())

                _playbackUiState.value = _playbackUiState.value.copy(
                    playState = _playbackUiState.value.playState.copy(
                        playerState = playerState,
                        currentSong = currentSong,
                        currentPosition = currentPosition,
                        totalDuration = totalDuration,
                        isShuffleEnabled = shuffleModeEnabled,
                        repeatMode = repeatMode
                    )
                )
                if (_playbackUiState.value.playState.playerState == PlayerState.PLAYING) {
                    viewModelScope.launch {
                        while (true) {
                            delay(1.seconds)
                            _playbackUiState.value = _playbackUiState.value.copy(
                                playState = _playbackUiState.value.playState.copy(
                                    currentPosition = musicController.getCurrentPosition()
                                )
                            )
                        }
                    }
                }
            }
    }

    fun onEvent(event: PlaybackEvent) {
        when (event) {
            is PlaybackEvent.PlaySong -> play(event.song, event.playlist)
            PlaybackEvent.PauseSong -> pause()
            PlaybackEvent.ResumeSong -> resume()
            PlaybackEvent.SkipToNextSong -> skipToNext()
            PlaybackEvent.SkipToPreviousSong -> skipToPrevious()
            is PlaybackEvent.RepeatMode -> setRepeatMode(event.repeatMode)
            PlaybackEvent.ShuffleMode -> setShuffleMode()
            is PlaybackEvent.SeekSongToPosition -> seekToPosition(event.position)
            PlaybackEvent.ExpandPanel -> toggleExpand()
            PlaybackEvent.ClickFavorite -> clickFavorite()
            PlaybackEvent.PlayRandom -> playRandom()
        }
        getCallbackOnes()
    }

    private fun playRandom() = viewModelScope.launch {
        val shuffleModeEnabled = prefRepository.getShuffleMode().first()  // Flow에서 마지막 값 가져오기
        val repeatMode = prefRepository.getRepeatMode().first()         // Flow에서 마지막 값 가져오기
        println(allSongState.value.size)

        allSongState.value.let {
            if (it.isNotEmpty()) {
                val songs = allSongState.value.shuffled().slice(0..it.size - 1)
                musicController.addMediaItems(songs)
                musicController.setShuffleModeEnabled(shuffleModeEnabled)
                musicController.setRepeatMode(repeatMode)
                musicController.play(0)
                songs[0].toEntity().let { song ->
                    manageRepository.insertHistory(song, System.currentTimeMillis())
                    manageRepository.upsertPlayCount(song)
                }
            }
        }
    }

    private fun clickFavorite() {
        _playbackUiState.value.playState.currentSong?.let { song ->
            viewModelScope.launch {
                println("@@@@" + song.toString())
                manageRepository.upsertFavorite(song.toEntity())
            }
        }
    }

    private fun play(song: Song, songs: List<Song>) = viewModelScope.launch {
        val shuffleModeEnabled = prefRepository.getShuffleMode().first()  // Flow에서 마지막 값 가져오기
        val repeatMode = prefRepository.getRepeatMode().first()         // Flow에서 마지막 값 가져오기
        musicController.addMediaItems(songs)
        musicController.setShuffleModeEnabled(shuffleModeEnabled)
        musicController.setRepeatMode(repeatMode)
        songs.indexOf(song).let { musicController.play(it) }
        song.toEntity().let { song ->
            manageRepository.insertHistory(song, System.currentTimeMillis())
            manageRepository.upsertPlayCount(song)
        }
    }

    private fun pause() {
        musicController.pause()
    }

    private fun resume() {
        musicController.resume()
    }

    private fun skipToNext() {
        musicController.skipToNextSong()
    }

    private fun skipToPrevious() {
        if (musicController.getCurrentPosition() > 5000) {
            musicController.seekTo(0)
            return
        }
        musicController.skipToPreviousSong()
    }

    private fun setRepeatMode(repeatMode: Int? = null) {
        if (repeatMode != null) {
            musicController.setRepeatMode(repeatMode)
            return
        } else {
            val currentMode = RepeatMode.fromValue(_playbackUiState.value.playState.repeatMode)
            val nextMode = RepeatMode.next(currentMode)
            musicController.setRepeatMode(nextMode.value)
        }
    }

    private fun setShuffleMode() {
        musicController.setShuffleModeEnabled(!_playbackUiState.value.playState.isShuffleEnabled)
    }


    private fun getCallbackOnes() {
        musicController.callbackOnes()
    }

    private fun seekToPosition(position: Long) {
        musicController.seekTo(position)
    }

    private fun toggleExpand() {
        _playbackUiState.value =
            _playbackUiState.value.copy(expand = !_playbackUiState.value.expand)
    }


    init {
        viewModelScope.launch {
            println("@@@ VIEWMODEL INIT")
            setMediaControllerCallback()
            getCallbackOnes()
        }
    }

}