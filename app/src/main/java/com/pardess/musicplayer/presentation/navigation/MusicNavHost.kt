package com.pardess.musicplayer.presentation.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.pardess.musicplayer.Constants
import com.pardess.musicplayer.domain.model.Song
import com.pardess.musicplayer.presentation.home.HomeScreen
import com.pardess.musicplayer.presentation.home.MusicBottomNavigationBar
import com.pardess.musicplayer.presentation.songs.SongsScreen
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.pardess.musicplayer.domain.model.Album
import com.pardess.musicplayer.domain.model.Artist
import com.pardess.musicplayer.domain.model.enums.PlayerState
import com.pardess.musicplayer.presentation.home.HomeEvent
import com.pardess.musicplayer.presentation.home.HomeUiState
import com.pardess.musicplayer.presentation.home.HomeViewModel
import com.pardess.musicplayer.presentation.navigation.navgraph.artistGraph
import com.pardess.musicplayer.presentation.main.navgraph.mainGraph
import com.pardess.musicplayer.presentation.navigation.navgraph.playlistGraph
import com.pardess.musicplayer.presentation.main.searchbox.SearchBoxEvent
import com.pardess.musicplayer.presentation.main.searchbox.SearchBoxState
import com.pardess.musicplayer.presentation.main.searchbox.SearchBoxViewModel
import com.pardess.musicplayer.presentation.playback.Playback
import com.pardess.musicplayer.presentation.playback.PlaybackEvent
import com.pardess.musicplayer.presentation.playback.PlaybackViewModel
import com.pardess.musicplayer.ui.theme.BackgroundColor
import com.pardess.musicplayer.ui.theme.PointColor


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MusicNavHost(
    navController: MusicNavController
) {

    val mediaPermissions = rememberMultiplePermissionsState(Constants.MEDIA_PERMISSIONS)

    LaunchedEffect(Unit) {
        if (!mediaPermissions.allPermissionsGranted) {
            mediaPermissions.launchMultiplePermissionRequest()
        }
    }
    if (!mediaPermissions.allPermissionsGranted) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("앱을 사용하기 위해서는 미디어 권한이 필요합니다.")
                Text("미디어 권한을 허락해주세요!")
                Button(
                    onClick = {
                        mediaPermissions.launchMultiplePermissionRequest()
                    }
                ) {
                    Text("권한 허락")
                }
            }
        }
        return
    }

    val playbackViewModel = hiltViewModel<PlaybackViewModel>()
    val searchBoxViewModel = hiltViewModel<SearchBoxViewModel>()
    val homeViewModel = hiltViewModel<HomeViewModel>()

    val homeState = homeViewModel.uiState.collectAsStateWithLifecycle()

    val mainState = searchBoxViewModel.uiState.collectAsStateWithLifecycle()

    val allSongState =
        playbackViewModel.allSongState.collectAsStateWithLifecycle()

    val playbackUiState = playbackViewModel.playbackUiState.collectAsStateWithLifecycle()

    val navigationBarHeight = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    // 현재 백 스택에서 가장 최신의 `destination`을 가져옴 (null 방지)
    val navBackStackEntry by navController.navController.currentBackStackEntryAsState()

    // 현재 경로를 가져오되, null이면 기본 경로 설정
    val currentRoute = navBackStackEntry?.destination?.route ?: HomeScreen.Main.route

    val bottomBarHeight by animateDpAsState(
        targetValue = if (playbackUiState.value.expand || !HomeScreen.entries.any { it.route == currentRoute } || mainState.value.expand) navigationBarHeight else 100.dp + navigationBarHeight,
        animationSpec = tween(400), label = "Playback Bar Height"
    )

    BackHandler(enabled = playbackUiState.value.expand || mainState.value.expand) {
        if (mainState.value.expand) {
            searchBoxViewModel.onEvent(SearchBoxEvent.Shrink)
            return@BackHandler
        }
    }

    val systemUiController = rememberSystemUiController()
    LaunchedEffect(playbackUiState.value.expand) {
        if (playbackUiState.value.expand) {
            systemUiController.setSystemBarsColor(
                color = Color.White, // 원하는 색상
            )
            return@LaunchedEffect
        }
        systemUiController.setNavigationBarColor(
            color = PointColor, // 원하는 색상
        )
        systemUiController.setStatusBarColor(
            color = BackgroundColor, // 원하는 색상
        )
    }

    Scaffold(
        bottomBar = {
            MusicBottomNavigationBar(
                modifier = Modifier.height(bottomBarHeight),
                tabs = HomeScreen.entries.toList(),
                currentRoute = currentRoute ?: HomeScreen.Main.route,
                navigateToBottomBarRoute = navController::navigateToBottomBarRoute,
            )
        }
    ) {
        Box(
            modifier = Modifier.padding()
        ) {
            Box(
                modifier = Modifier
                    .padding(
                        top = it.calculateTopPadding(),
                        bottom = if (playbackUiState.value.playState.currentSong != null) 116.dp else 0.dp
                    )
            ) {
                NavHost(
                    navController = navController.navController,
                    startDestination = Navigation.HomeNavigation.route,
                ) {
                    navGraph(
                        navController = navController.navController,
                        onNavigateToRoute = navController::navigateToRoute,
                        upPress = navController::upPress,
                        allSongState = allSongState,
                        onPlaybackEvent = playbackViewModel::onEvent,
                        onMainEvent = { event ->
                            searchBoxViewModel.onEvent(
                                event = event,
                                onNavigateToRoute = navController::navigateToRoute,
                            )
                        },
                        searchBoxState = mainState,
                        homeState = homeState,
                        onHomeEvent = homeViewModel::onEvent
                    )
                }
            }
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = bottomBarHeight)
            ) {
                Playback(
                    playbackUiState = playbackUiState.value,
                    onPlaybackUiEvent = playbackViewModel::onEvent,
                    onFavoriteClick = {
                        playbackViewModel.onEvent(PlaybackEvent.ClickFavorite)
                    },
                    onSliderChange = { newPosition ->
                        playbackViewModel.onEvent(PlaybackEvent.SeekSongToPosition(newPosition.toLong()))
                    },
                    onBarClick = {
                        playbackViewModel.onEvent(PlaybackEvent.ExpandPanel)
                    },
                    playOrToggleSong = {
                        if (playbackUiState.value.playState.playerState == PlayerState.PLAYING)
                            playbackViewModel.onEvent(PlaybackEvent.PauseSong)
                        else
                            playbackViewModel.onEvent(PlaybackEvent.ResumeSong)
                    },
                    playNextSong = {
                        playbackViewModel.onEvent(PlaybackEvent.SkipToNextSong)
                    },
                    playPreviousSong = {
                        playbackViewModel.onEvent(PlaybackEvent.SkipToPreviousSong)
                    },
                    setRepeatMode = {
                        playbackViewModel.onEvent(PlaybackEvent.RepeatMode())
                    },
                    setShuffleMode = {
                        playbackViewModel.onEvent(PlaybackEvent.ShuffleMode)
                    }
                )
            }
        }
    }
}

fun NavGraphBuilder.navGraph(
    navController: NavHostController,
    onNavigateToRoute: (String) -> Unit,
    upPress: () -> Unit,
    allSongState: State<List<Song>>,
    homeState: State<HomeUiState>,
    searchBoxState: State<SearchBoxState>,
    onPlaybackEvent: (PlaybackEvent) -> Unit,
    onMainEvent: (SearchBoxEvent) -> Unit,
    onHomeEvent: (HomeEvent) -> Unit
) {
    navigation(
        route = Navigation.HomeNavigation.route,
        startDestination = Navigation.MainNavigation.route,
    ) {
        mainGraph(
            onNavigateToRoute = { route -> onNavigateToRoute(route) },
            upPress = upPress,
            allSongState = allSongState,
            searchBoxState = searchBoxState,
            onPlaybackEvent = onPlaybackEvent,
            onMainEvent = onMainEvent,
        )
        playlistGraph(
            onNavigateToRoute = { route -> onNavigateToRoute(route) },
            upPress = upPress,
            songState = allSongState,
            onPlaybackEvent = onPlaybackEvent
        )
        artistGraph(
            onNavigateToRoute = { route -> onNavigateToRoute(route) },
            upPress = upPress,
            onPlaybackEvent = onPlaybackEvent,
            allSongState = allSongState,
        )
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







