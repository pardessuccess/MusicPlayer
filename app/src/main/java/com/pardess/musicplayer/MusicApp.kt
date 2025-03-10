package com.pardess.musicplayer

import androidx.compose.runtime.Composable
import com.pardess.designsystem.MusicPlayerTheme
import com.pardess.musicplayer.navigation.MusicNavHost
import com.pardess.musicplayer.navigation.rememberMusicNavController

@Composable
fun MusicApp() {
    MusicPlayerTheme {
        val navController = rememberMusicNavController()
        MusicNavHost(
            navController = navController
        )
    }
}