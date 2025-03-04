package com.pardess.musicplayer.presentation.main.playcount

import androidx.lifecycle.viewModelScope
import com.pardess.musicplayer.data.entity.join.PlayCountSong
import com.pardess.musicplayer.domain.repository.ManageRepository
import com.pardess.musicplayer.presentation.base.BaseUiEffect
import com.pardess.musicplayer.presentation.base.BaseUiEvent
import com.pardess.musicplayer.presentation.base.BaseUiState
import com.pardess.musicplayer.presentation.base.BaseViewModel
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
    manageRepository: ManageRepository
) : BaseViewModel<PlayCountUiState, PlayCountUiEvent, PlayCountUiEffect>(PlayCountUiState()) {

    private val playCountSongs = manageRepository.getPlayCountSongs().stateIn(
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