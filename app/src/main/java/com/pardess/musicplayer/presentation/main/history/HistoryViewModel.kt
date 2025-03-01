package com.pardess.musicplayer.presentation.main.history

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
class HistoryViewModel @Inject constructor(
    private val manageRepository: ManageRepository
) : ViewModel() {

    // 내부 상태를 불변 객체로 관리
    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState = _uiState.asStateFlow()

    private val _effectChannel = Channel<HistoryEffect>(Channel.BUFFERED)
    val effectFlow = _effectChannel.receiveAsFlow()

    private var currentHistory: HistorySong? = null

    init {
        viewModelScope.launch {
            manageRepository.getHistorySongs().collect { historySongs ->
                _uiState.update { currentState ->
                    currentState.copy(historySongs = historySongs)
                }
            }
        }
    }

    fun onEvent(event: HistoryUiEvent) {
        when (event) {
            is HistoryUiEvent.RemoveHistory -> {
                removeHistory()
            }

            HistoryUiEvent.DismissRemoveDialog -> {
                currentHistory = null
                viewModelScope.launch {
                    _effectChannel.send(HistoryEffect.DismissRemoveDialog)
                }
            }

            is HistoryUiEvent.ShowRemoveDialog -> {
                currentHistory = event.historySong
                viewModelScope.launch {
                    _effectChannel.send(HistoryEffect.ShowRemoveDialog(event.historySong))
                }
            }
        }
    }

    private fun removeHistory() {
        val historySong = currentHistory ?: return
        viewModelScope.launch {
            manageRepository.removeHistory(historySong.lastPlayed!!)
            currentHistory = null
            _effectChannel.send(HistoryEffect.HistoryRemoved)
        }
    }
}