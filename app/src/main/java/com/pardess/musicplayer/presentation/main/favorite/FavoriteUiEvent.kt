package com.pardess.musicplayer.presentation.main.favorite

import com.pardess.musicplayer.data.entity.join.FavoriteSong

sealed class FavoriteUiEvent {

    data class ShowRemoveDialog(val favoriteSong: FavoriteSong) : FavoriteUiEvent()
    object DismissRemoveDialog : FavoriteUiEvent()
    object RemoveFavorite : FavoriteUiEvent()


}