package com.pardess.musicplayer.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.pardess.musicplayer.presentation.navigation.MusicNavController
import com.pardess.musicplayer.presentation.navigation.MusicNavHost
import com.pardess.musicplayer.presentation.navigation.rememberMusicNavController
import com.pardess.musicplayer.ui.theme.MusicPlayerTheme

@Composable
fun MusicApp() {
    MusicPlayerTheme {
        val navController = rememberMusicNavController()
        MusicNavHost(
            navController = navController
        )
    }
}