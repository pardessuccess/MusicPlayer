package com.pardess.home.playcount

import androidx.lifecycle.viewModelScope
import com.pardess.common.base.BaseUiEffect
import com.pardess.common.base.BaseUiEvent
import com.pardess.common.base.BaseUiState
import com.pardess.common.base.BaseViewModel
import com.pardess.domain.usecase.main.MainDetailUseCase
import com.pardess.model.join.PlayCountSong
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class PlayCountUiState(
    val playCountSongs: List<PlayCountSong> = emptyList()
) : BaseUiState

sealed class PlayCountUiEffect : BaseUiEffect

sealed class PlayCountUiEvent : BaseUiEvent

@HiltViewModel
class PlayCountViewModel @Inject constructor(
    useCase: MainDetailUseCase
) : BaseViewModel<PlayCountUiState, PlayCountUiEvent, PlayCountUiEffect>(PlayCountUiState()) {

    private val playCountSongs = useCase.getPlayCountSongs().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        emptyList()
    )

    init {
        collectState(playCountSongs) { copy(playCountSongs = it) }
    }

    override fun onEvent(event: PlayCountUiEvent) {

    }
}