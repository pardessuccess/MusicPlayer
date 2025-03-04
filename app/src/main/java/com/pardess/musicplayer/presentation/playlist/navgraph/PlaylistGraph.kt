package com.pardess.musicplayer.presentation.playlist.navgraph

import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
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
import com.pardess.musicplayer.presentation.playlist.detail.DetailPlaylistViewModel
import com.pardess.musicplayer.presentation.playlist.detail.PlaylistDetailScreen

fun NavGraphBuilder.playlistGraph(
    onNavigateToRoute: (String) -> Unit,
    upPress: () -> Unit,
    allSongsState: State<List<Song>>,
    onPlaybackEvent: (PlaybackEvent) -> Unit
) {
    navigation(
        route = Navigation.Playlist.route,
        startDestination = HomeScreen.Playlist.route,
    ) {
        composable(HomeScreen.Playlist.route) {
            PlaylistScreen(
                onNavigateToRoute = onNavigateToRoute,
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
