package com.pardess.artist.navgraph

import androidx.compose.runtime.State
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.pardess.artist.ArtistScreen
import com.pardess.artist.album.DetailAlbumScreen
import com.pardess.artist.detail.DetailArtistScreen
import com.pardess.model.Song
import com.pardess.navigation.HomeScreen
import com.pardess.navigation.Navigation
import com.pardess.navigation.Screen
import com.pardess.playback.PlaybackEvent


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