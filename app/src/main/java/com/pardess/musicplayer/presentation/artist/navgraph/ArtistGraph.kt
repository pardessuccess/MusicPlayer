package com.pardess.musicplayer.presentation.artist.navgraph

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
import com.pardess.musicplayer.domain.model.Song
import com.pardess.musicplayer.presentation.artist.ArtistScreen
import com.pardess.musicplayer.presentation.artist.album.DetailAlbumScreen
import com.pardess.musicplayer.presentation.artist.album.DetailAlbumViewModel
import com.pardess.musicplayer.presentation.artist.detail.DetailArtistScreen
import com.pardess.musicplayer.presentation.artist.detail.DetailArtistViewModel
import com.pardess.musicplayer.presentation.home.HomeScreen
import com.pardess.musicplayer.presentation.navigation.Navigation
import com.pardess.musicplayer.presentation.navigation.Screen
import com.pardess.musicplayer.presentation.playback.PlaybackEvent


fun NavGraphBuilder.artistGraph(
    navigate: (String, NavBackStackEntry) -> Unit,
    upPress: () -> Unit,
    onPlaybackEvent: (PlaybackEvent) -> Unit,
    allSongsState: State<List<Song>>,
) {
    navigation(
        route = Navigation.Artist.route,
        startDestination = HomeScreen.Artist.route,
    ) {
        composable(HomeScreen.Artist.route) { backStackEntry ->
            ArtistScreen(
                onNavigateToRoute = { navigate(it, backStackEntry) },
                upPress = upPress,
                allSongs = allSongsState.value,
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
        ) { backStackEntry ->
            DetailArtistScreen(
                onNavigateToRoute = { navigate(it, backStackEntry) },
                onPlaybackEvent = onPlaybackEvent,
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
        ) { backStackEntry ->
            DetailAlbumScreen(
                onNavigateToRoute = { navigate(it, backStackEntry) },
                onPlaybackEvent = onPlaybackEvent,
            )
        }
    }
}