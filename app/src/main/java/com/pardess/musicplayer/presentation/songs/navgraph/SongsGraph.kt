package com.pardess.musicplayer.presentation.songs.navgraph

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
import com.pardess.musicplayer.presentation.songs.SongsScreen


fun NavGraphBuilder.songsGraph(
    onNavigateToRoute: (String) -> Unit,
    upPress: () -> Unit,
    onPlaybackEvent: (PlaybackEvent) -> Unit,
    allSongState: State<List<Song>>,
) {
    navigation(
        route = Navigation.Songs.route,
        startDestination = HomeScreen.Songs.route,
    ) {
        composable(HomeScreen.Songs.route) {
            SongsScreen(
                navigateToRoute = { route -> onNavigateToRoute(route) },
                upPress = upPress,
                songState = allSongState,
                onPlaybackEvent = onPlaybackEvent
            )
        }
    }
}