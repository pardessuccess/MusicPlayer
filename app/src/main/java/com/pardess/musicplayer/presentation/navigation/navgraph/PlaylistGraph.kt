package com.pardess.musicplayer.presentation.navigation.navgraph

import androidx.compose.runtime.State
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.pardess.musicplayer.domain.model.Song
import com.pardess.musicplayer.presentation.home.HomeScreen
import com.pardess.musicplayer.presentation.navigation.Navigation
import com.pardess.musicplayer.presentation.navigation.Screen
import com.pardess.musicplayer.presentation.playback.PlaybackEvent
import com.pardess.musicplayer.presentation.playlist.PlaylistScreen
import com.pardess.musicplayer.presentation.playlist.PlaylistViewModel
import com.pardess.musicplayer.presentation.playlist.detail.DetailPlaylistViewModel
import com.pardess.musicplayer.presentation.playlist.detail.PlaylistDetailScreen

fun NavGraphBuilder.playlistGraph(
    onNavigateToRoute: (String) -> Unit,
    upPress: () -> Unit,
    songState: State<List<Song>>,
    onPlaybackEvent: (PlaybackEvent) -> Unit
) {
    navigation(
        route = Navigation.PlaylistNavigation.route,
        startDestination = HomeScreen.Playlist.route,
    ) {
        composable(HomeScreen.Playlist.route) {
            val playlistViewModel = hiltViewModel<PlaylistViewModel>()
            val playlistState = playlistViewModel.uiState.collectAsStateWithLifecycle()

            PlaylistScreen(
                onNavigateToRoute = onNavigateToRoute,
                upPress = upPress,
                uiState = playlistState,
                onEvent = playlistViewModel::onEvent,
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
            val uiState = detailPlaylistViewModel.uiState.collectAsStateWithLifecycle()

            PlaylistDetailScreen(
                onNavigateToRoute = onNavigateToRoute,
                upPress = upPress,
                uiState = uiState,
                songState = songState,
                onEvent = detailPlaylistViewModel::onEvent,
                onPlaybackEvent = onPlaybackEvent
            )
        }
    }
}