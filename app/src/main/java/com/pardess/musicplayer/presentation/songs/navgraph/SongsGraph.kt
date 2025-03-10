package com.pardess.musicplayer.presentation.songs.navgraph

import androidx.compose.runtime.State
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.pardess.musicplayer.domain.model.Song
import com.pardess.musicplayer.presentation.base.BaseScreen
import com.pardess.musicplayer.presentation.home.HomeScreen
import com.pardess.musicplayer.presentation.navigation.Navigation
import com.pardess.musicplayer.presentation.playback.PlaybackEvent
import com.pardess.musicplayer.presentation.songs.SongsScreen


fun NavGraphBuilder.songsGraph(
    navigate: (String, NavBackStackEntry) -> Unit,
    upPress: () -> Unit,
    onPlaybackEvent: (PlaybackEvent) -> Unit,
    allSongsState: State<List<Song>>,
) {
    navigation(
        route = Navigation.Songs.route,
        startDestination = HomeScreen.Songs.route,
    ) {
        composable(HomeScreen.Songs.route) { backStackEntry ->
            BaseScreen {
                SongsScreen(
                    onNavigateToRoute = { navigate(it, backStackEntry) },
                    allSongs = allSongsState.value,
                    onPlaybackEvent = onPlaybackEvent
                )
            }
        }
    }
}