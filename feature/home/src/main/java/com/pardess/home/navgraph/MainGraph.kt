package com.pardess.home.navgraph

import androidx.compose.runtime.State
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.pardess.home.MainScreen
import com.pardess.home.favorite.FavoriteScreen
import com.pardess.home.history.HistoryScreen
import com.pardess.home.playcount.PlayCountScreen
import com.pardess.home.search.SearchScreen
import com.pardess.model.Song
import com.pardess.navigation.HomeScreen
import com.pardess.navigation.Navigation
import com.pardess.navigation.Screen
import com.pardess.playback.PlaybackEvent
import com.pardess.root.RootUiEvent

fun NavGraphBuilder.mainGraph(
    saveState: (String, String) -> Unit,
    navigate: (String, NavBackStackEntry) -> Unit,
    upPress: () -> Unit,
    allSongsState: State<List<Song>>,
    onHomeUiEvent: (RootUiEvent) -> Unit,
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

