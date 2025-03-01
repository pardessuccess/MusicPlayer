package com.pardess.musicplayer.presentation.main.searchbox

sealed class SearchBoxEvent {
    data class Search(val searchQuery: String) : SearchBoxEvent()
    object Expand : SearchBoxEvent()
    object Shrink : SearchBoxEvent()
}