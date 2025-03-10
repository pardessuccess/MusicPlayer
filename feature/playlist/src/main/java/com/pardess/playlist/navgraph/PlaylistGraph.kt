package com.pardess.playlist.navgraph

import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.pardess.model.Song
import com.pardess.navigation.HomeScreen
import com.pardess.navigation.Navigation
import com.pardess.navigation.Screen
import com.pardess.playback.PlaybackEvent
import com.pardess.playlist.PlaylistScreen
import com.pardess.playlist.detail.DetailPlaylistViewModel
import com.pardess.playlist.detail.PlaylistDetailScreen

fun NavGraphBuilder.playlistGraph(
    navigate: (String, NavBackStackEntry) -> Unit,
    upPress: () -> Unit,
    allSongsState: State<List<Song>>,
    onPlaybackEvent: (PlaybackEvent) -> Unit
) {
    navigation(
        route = Navigation.Playlist.route,
        startDestination = HomeScreen.Playlist.route,
    ) {
        composable(HomeScreen.Playlist.route) { backStackEntry ->
            PlaylistScreen(
                onNavigateToRoute = { navigate(it, backStackEntry) },
            )
        }
        composable(
            route = Screen.DetailPlaylist.route + "/{playlistId}",
            arguments = listOf(navArgument("playlistId") {
                type = NavType.LongType
            }
            )
        ) {
            val detailPlaylistViewModel = hiltViewModel<DetailPlaylistViewModel>()
            val uiState by detailPlaylistViewModel.uiState.collectAsStateWithLifecycle()

            PlaylistDetailScreen(
                allSongs = allSongsState.value,
                onPlaybackEvent = onPlaybackEvent
            )
        }
    }
}
