package com.pardess.musicplayer.presentation.main.favorite

import com.pardess.musicplayer.data.entity.join.FavoriteSong

data class FavoriteUiState(

    val favoriteSongs: List<FavoriteSong> = emptyList(),

)