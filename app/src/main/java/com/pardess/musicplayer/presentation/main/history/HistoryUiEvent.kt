package com.pardess.musicplayer.presentation.main.history

import com.pardess.musicplayer.data.entity.join.HistorySong

sealed class HistoryUiEvent {

    object RemoveHistory : HistoryUiEvent()

    data class ShowRemoveDialog(val historySong: HistorySong) : HistoryUiEvent()

    object DismissRemoveDialog : HistoryUiEvent()

}