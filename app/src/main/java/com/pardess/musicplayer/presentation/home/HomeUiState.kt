package com.pardess.musicplayer.presentation.home

data class HomeUiState(
    val bottomBarExpand: Boolean = true,
    val bottomBarHeight: Int = 0,
    val currentRoute: String? = null,
    val searchBoxExpand: Boolean = false,
)