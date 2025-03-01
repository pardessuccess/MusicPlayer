package com.pardess.musicplayer.presentation.main.favorite

import com.pardess.musicplayer.data.entity.join.FavoriteSong

sealed class FavoriteUiEffect {
    data class ShowRemoveDialog(val favoriteSong: FavoriteSong) : FavoriteUiEffect()
    object DismissRemoveDialog : FavoriteUiEffect()
    object FavoriteUiRemoved : FavoriteUiEffect() // 삭제가 완료되었음을 알림 (예: 토스트)
}
