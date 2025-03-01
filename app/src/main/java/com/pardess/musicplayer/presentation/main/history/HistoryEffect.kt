package com.pardess.musicplayer.presentation.main.history

import com.pardess.musicplayer.data.entity.join.HistorySong

sealed class HistoryEffect {
    data class ShowRemoveDialog(val historySong: HistorySong) : HistoryEffect()
    object DismissRemoveDialog : HistoryEffect()
    object HistoryRemoved : HistoryEffect() // 삭제가 완료되었음을 알림 (예: 토스트)
}
