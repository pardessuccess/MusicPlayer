package com.pardess.musicplayer.presentation.main.playcount

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pardess.musicplayer.data.entity.join.HistorySong
import com.pardess.musicplayer.domain.repository.ManageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayCountViewModel @Inject constructor(
    private val manageRepository: ManageRepository
) : ViewModel() {

    // 내부 상태를 불변 객체로 관리
    private val _uiState = MutableStateFlow(PlayCountUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            manageRepository.getPlayCountSongs().collect { playCountSongs ->
                _uiState.update { currentState ->
                    currentState.copy(playCountSongs = playCountSongs)
                }
            }
        }
    }
}