package com.pardess.musicplayer.presentation.main.navgraph

import androidx.compose.runtime.State
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.pardess.musicplayer.domain.model.Song
import com.pardess.musicplayer.presentation.home.HomeScreen
import com.pardess.musicplayer.presentation.home.HomeUiEvent
import com.pardess.musicplayer.presentation.main.MainScreen
import com.pardess.musicplayer.presentation.main.favorite.FavoriteScreen
import com.pardess.musicplayer.presentation.main.history.HistoryScreen
import com.pardess.musicplayer.presentation.main.playcount.PlayCountScreen
import com.pardess.musicplayer.presentation.main.search.SearchScreen
import com.pardess.musicplayer.presentation.navigation.Navigation
import com.pardess.musicplayer.presentation.navigation.Screen
import com.pardess.musicplayer.presentation.playback.PlaybackEvent

fun NavGraphBuilder.mainGraph(
    saveState: (String, String) -> Unit,
    navigate: (String, NavBackStackEntry) -> Unit,
    upPress: () -> Unit,
    allSongsState: State<List<Song>>,
    onHomeUiEvent: (HomeUiEvent) -> Unit,
    onPlaybackEvent: (PlaybackEvent) -> Unit,
) {
    navigation(
        route = Navigation.Main.route,
        startDestination = HomeScreen.Main.route,
    ) {
        composable(HomeScreen.Main.route) { backStackEntry ->
            MainScreen(
                onNavigateToRoute = { navigate(it, backStackEntry) },
                saveState = saveState,
                onHomeUiEvent = onHomeUiEvent,
                onPlaybackEvent = onPlaybackEvent,
            )
        }
        composable(Screen.Search.route) { backStackEntry ->
            SearchScreen(
                onNavigateToRoute = { navigate(it, backStackEntry) },
                onPlaybackEvent = onPlaybackEvent,
                allSongs = allSongsState.value,
            )
        }
        composable(Screen.Favorite.route) {
            FavoriteScreen(
                onPlaybackEvent = onPlaybackEvent,
            )
        }

        composable(Screen.History.route) {
            HistoryScreen(
                onPlaybackEvent = onPlaybackEvent,
            )
        }

        composable(Screen.PlayCount.route) {
            PlayCountScreen(
                onPlaybackEvent = onPlaybackEvent,
            )
        }
    }
}

