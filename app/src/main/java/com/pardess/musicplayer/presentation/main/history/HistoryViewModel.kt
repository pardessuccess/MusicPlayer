package com.pardess.musicplayer.presentation.main.history

import androidx.lifecycle.viewModelScope
import com.pardess.musicplayer.data.entity.join.HistorySong
import com.pardess.musicplayer.domain.repository.ManageRepository
import com.pardess.musicplayer.presentation.base.BaseUiEffect
import com.pardess.musicplayer.presentation.base.BaseUiEvent
import com.pardess.musicplayer.presentation.base.BaseUiState
import com.pardess.musicplayer.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class HistoryUiEvent : BaseUiEvent {
    object ResetHistory : HistoryUiEvent()
}

data class HistoryUiState(
    val historySongs: List<HistorySong> = emptyList(),
) : BaseUiState

sealed class HistoryEffect : BaseUiEffect {
    object HistoryRemoved : HistoryEffect() // 삭제가 완료되었음을 알림 (예: 토스트)
}

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val manageRepository: ManageRepository
) : BaseViewModel<HistoryUiState, HistoryUiEvent, HistoryEffect>(HistoryUiState()) {

    private val historySongs = manageRepository.getHistorySongs().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        emptyList()
    )

    init {
        collectState(historySongs) { historySongs ->
            copy(historySongs = historySongs)
        }
    }

    override fun onEvent(event: HistoryUiEvent) {
        when (event) {
            is HistoryUiEvent.ResetHistory -> {
                resetHistory()
            }
        }
    }

    private fun resetHistory() {
        viewModelScope.launch {
            manageRepository.resetHistory()
            sendEffect(HistoryEffect.HistoryRemoved)
        }
    }
}