package com.pardess.home.history

import androidx.lifecycle.viewModelScope
import com.pardess.common.base.BaseUiEffect
import com.pardess.common.base.BaseUiEvent
import com.pardess.common.base.BaseUiState
import com.pardess.common.base.BaseViewModel
import com.pardess.domain.usecase.main.MainDetailUseCase
import com.pardess.model.join.HistorySong
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
    private val useCase: MainDetailUseCase
) : BaseViewModel<HistoryUiState, HistoryUiEvent, HistoryEffect>(HistoryUiState()) {

    private val historySongs = useCase.getHistorySongs().stateIn(
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
            useCase.resetHistory()
            sendEffect(HistoryEffect.HistoryRemoved)
        }
    }
}