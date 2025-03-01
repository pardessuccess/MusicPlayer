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
import com.pardess.musicplayer.presentation.artist.ArtistScreen
import com.pardess.musicplayer.presentation.artist.ArtistViewModel
import com.pardess.musicplayer.presentation.artist.album.DetailAlbumScreen
import com.pardess.musicplayer.presentation.artist.album.DetailAlbumViewModel
import com.pardess.musicplayer.presentation.artist.detail.DetailArtistScreen
import com.pardess.musicplayer.presentation.artist.detail.DetailArtistViewModel
import com.pardess.musicplayer.presentation.home.HomeScreen
import com.pardess.musicplayer.presentation.navigation.Navigation
import com.pardess.musicplayer.presentation.navigation.Screen
import com.pardess.musicplayer.presentation.playback.PlaybackEvent


fun NavGraphBuilder.artistGraph(
    onNavigateToRoute: (String) -> Unit,
    upPress: () -> Unit,
    onPlaybackEvent: (PlaybackEvent) -> Unit,
    allSongState: State<List<Song>>,
) {
    navigation(
        route = Navigation.ArtistNavigation.route,
        startDestination = HomeScreen.Artist.route,
    ) {
        composable(HomeScreen.Artist.route) {

            val viewModel = hiltViewModel<ArtistViewModel>()
            viewModel.setArtists(allSongState.value)
            val artistState = viewModel.artistState.collectAsStateWithLifecycle()

            ArtistScreen(
                onNavigateToRoute = onNavigateToRoute,
                upPress = upPress,
                artistState = artistState
            )
        }
        composable(
            route = Screen.DetailArtist.route + "/{artistId}",
            arguments =
            listOf(
                navArgument("artistId") {
                    type = NavType.LongType
                },
            )
        ) {
            val viewModel = hiltViewModel<DetailArtistViewModel>()
            val uiState = viewModel.uiState.collectAsStateWithLifecycle()

            DetailArtistScreen(
                onNavigateToRoute = onNavigateToRoute,
                upPress = upPress,
                onEvent = { event ->
                    viewModel.onEvent(
                        event = event,
                        onNavigateToRoute = onNavigateToRoute
                    )
                },
                onPlaybackEvent = onPlaybackEvent,
                uiState = uiState,
            )
        }
        composable(
            route = Screen.DetailArtist.route + "/{artistId}/{albumId}",
            arguments =
            listOf(
                navArgument("artistId") {
                    type = NavType.LongType
                },
                navArgument("albumId") {
                    type = NavType.LongType
                }
            )
        ) {
            val viewModel = hiltViewModel<DetailAlbumViewModel>()
            val uiState = viewModel.uiState.collectAsStateWithLifecycle()
            DetailAlbumScreen(
                onNavigateToRoute = onNavigateToRoute,
                upPress = upPress,
                onPlaybackEvent = onPlaybackEvent,
                uiState = uiState
            )
        }
    }
}