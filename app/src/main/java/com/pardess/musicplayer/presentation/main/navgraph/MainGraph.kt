package com.pardess.musicplayer.presentation.main.navgraph

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.pardess.musicplayer.data.entity.join.FavoriteSong
import com.pardess.musicplayer.data.entity.join.HistorySong
import com.pardess.musicplayer.domain.model.Song
import com.pardess.musicplayer.presentation.home.HomeScreen
import com.pardess.musicplayer.presentation.main.MainScreen
import com.pardess.musicplayer.presentation.main.MainUiEffect
import com.pardess.musicplayer.presentation.main.MainViewModel
import com.pardess.musicplayer.presentation.main.favorite.FavoriteUiEffect
import com.pardess.musicplayer.presentation.main.favorite.FavoriteScreen
import com.pardess.musicplayer.presentation.main.favorite.FavoriteViewModel
import com.pardess.musicplayer.presentation.main.favorite.RemoveFavoriteDialog
import com.pardess.musicplayer.presentation.main.history.HistoryEffect
import com.pardess.musicplayer.presentation.main.history.HistoryScreen
import com.pardess.musicplayer.presentation.main.history.HistoryViewModel
import com.pardess.musicplayer.presentation.main.history.RemoveHistoryDialog
import com.pardess.musicplayer.presentation.main.playcount.PlayCountScreen
import com.pardess.musicplayer.presentation.main.playcount.PlayCountViewModel
import com.pardess.musicplayer.presentation.navigation.Navigation
import com.pardess.musicplayer.presentation.navigation.Screen
import com.pardess.musicplayer.presentation.playback.PlaybackEvent
import com.pardess.musicplayer.presentation.main.search.SearchScreen
import com.pardess.musicplayer.presentation.main.search.SearchViewModel
import com.pardess.musicplayer.presentation.main.searchbox.SearchBoxEvent
import com.pardess.musicplayer.presentation.main.searchbox.SearchBoxState

fun NavGraphBuilder.mainGraph(
    onNavigateToRoute: (String) -> Unit,
    upPress: () -> Unit,
    allSongState: State<List<Song>>,
    searchBoxState: State<SearchBoxState>,
    onPlaybackEvent: (PlaybackEvent) -> Unit,
    onMainEvent: (SearchBoxEvent) -> Unit,
) {
    navigation(
        route = Navigation.Main.route,
        startDestination = HomeScreen.Main.route,
    ) {
        composable(HomeScreen.Main.route) {

            val viewModel = hiltViewModel<MainViewModel>()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            LaunchedEffect(Unit) {
                viewModel.effectFlow.collect { effect ->
                    when (effect) {
                        is MainUiEffect.Navigate -> {
                            onNavigateToRoute(effect.route)
                        }
                    }
                }
            }

            MainScreen(
                searchBoxState = searchBoxState.value,
                uiState = uiState,
                onEvent = viewModel::onEvent,
                onPlaybackEvent = onPlaybackEvent,
                onSearchEvent = onMainEvent,
            )
        }

        composable(Screen.Favorite.route) {

            val viewModel = hiltViewModel<FavoriteViewModel>()
            val uiState = viewModel.uiState.collectAsStateWithLifecycle()
            val context = LocalContext.current
            var showRemoveDialog by remember { mutableStateOf(false) }
            var dialogFavoriteSong by remember { mutableStateOf<FavoriteSong?>(null) }

            LaunchedEffect(Unit) {
                viewModel.effectFlow.collect { effect ->
                    when (effect) {
                        is FavoriteUiEffect.ShowRemoveDialog -> {
                            showRemoveDialog = true
                            dialogFavoriteSong = effect.favoriteSong
                        }

                        is FavoriteUiEffect.DismissRemoveDialog -> {
                            showRemoveDialog = false
                            dialogFavoriteSong = null
                        }

                        is FavoriteUiEffect.FavoriteUiRemoved -> {
                            showRemoveDialog = false
                            dialogFavoriteSong = null
                            Toast.makeText(context, "즐겨찾기가 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

            Box(Modifier.fillMaxSize()) {
                FavoriteScreen(
                    uiState = uiState,
                    onEvent = viewModel::onEvent,
                    onPlaybackEvent = onPlaybackEvent,
                )
                if (showRemoveDialog) {
                    RemoveFavoriteDialog(
                        modifier = Modifier.fillMaxWidth(),
                        onEvent = { event -> viewModel.onEvent(event) }
                    )
                }
            }
        }

        composable(Screen.History.route) {

            val viewModel = hiltViewModel<HistoryViewModel>()
            val uiState = viewModel.uiState.collectAsStateWithLifecycle()
            val context = LocalContext.current
            var showRemoveDialog by remember { mutableStateOf(false) }
            var dialogHistorySong by remember { mutableStateOf<HistorySong?>(null) }

            LaunchedEffect(Unit) {
                viewModel.effectFlow.collect { effect ->
                    when (effect) {
                        is HistoryEffect.ShowRemoveDialog -> {
                            showRemoveDialog = true
                            dialogHistorySong = effect.historySong
                        }

                        is HistoryEffect.DismissRemoveDialog -> {
                            showRemoveDialog = false
                            dialogHistorySong = null
                        }

                        is HistoryEffect.HistoryRemoved -> {
                            showRemoveDialog = false
                            dialogHistorySong = null
                            Toast.makeText(context, "기록이 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

            Box(Modifier.fillMaxSize()) {
                HistoryScreen(
                    uiState = uiState,
                    onEvent = viewModel::onEvent,
                    onPlaybackEvent = onPlaybackEvent,
                )
                if (showRemoveDialog) {
                    RemoveHistoryDialog(
                        modifier = Modifier.fillMaxWidth(),
                        onEvent = { event -> viewModel.onEvent(event) }
                    )
                }
            }
        }


        composable(Screen.PlayCount.route) {

            val viewModel = hiltViewModel<PlayCountViewModel>()
            val uiState = viewModel.uiState.collectAsStateWithLifecycle()

            PlayCountScreen(
                uiState = uiState,
                onPlaybackEvent = onPlaybackEvent,
            )
        }

        composable(Screen.Search.route) {

            val viewModel = hiltViewModel<SearchViewModel>()
            val uiState = viewModel.searchUiState.collectAsStateWithLifecycle()

            viewModel.setSearchQuery(searchBoxState.value.searchQuery)
            LaunchedEffect(allSongState.value) {
                println("@@@ allSongState.value: ${allSongState.value.size}") // ✅ 디버깅용 로그
                if (allSongState.value.isNotEmpty()) {
                    viewModel.setAllSongs(allSongState.value)
                    viewModel.search(searchBoxState.value.searchQuery)
//                    viewModel.onEvent(SearchEvent.Search(mainUiState.value.searchQuery))
                }
            }
            SearchScreen(
                onNavigateToRoute = onNavigateToRoute,
                upPress = upPress,
                uiState = uiState,
                onEvent = { event ->
                    viewModel.onEvent(
                        event = event,
                        onPlaybackEvent = { song, songs ->
                            onPlaybackEvent(PlaybackEvent.PlaySong(songs.indexOf(song), songs))
                        },
                        onNavigateToRoute = onNavigateToRoute
                    )
                },
            )
        }
    }
}