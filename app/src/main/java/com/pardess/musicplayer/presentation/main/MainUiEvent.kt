package com.pardess.musicplayer.presentation.main

sealed class MainUiEvent {

    data class Navigate(val route: String) : MainUiEvent()
//    data class Search(val searchQuery: String) : MainUiEvent()
    data class RemoveSearchHistory(val id: Long) : MainUiEvent()
}