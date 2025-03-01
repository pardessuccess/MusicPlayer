package com.pardess.musicplayer.presentation.main

sealed class MainUiEffect {
    data class Navigate(val route: String) : MainUiEffect()
}