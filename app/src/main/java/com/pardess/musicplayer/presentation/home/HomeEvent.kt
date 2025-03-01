package com.pardess.musicplayer.presentation.home

sealed class HomeEvent {
    object BottomBarExpand : HomeEvent()
    object BottomBarShrink : HomeEvent()
    data class OnNavigateToRoute(val route: String) : HomeEvent()
    object SearchSectionExpand : HomeEvent()
    object SearchSectionShrink : HomeEvent()
}