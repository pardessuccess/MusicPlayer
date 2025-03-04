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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.pardess.musicplayer.Constants
import com.pardess.musicplayer.domain.model.Song
import com.pardess.musicplayer.presentation.artist.navgraph.artistGraph
import com.pardess.musicplayer.presentation.component.FullWidthButton
import com.pardess.musicplayer.presentation.home.HomeScreen
import com.pardess.musicplayer.presentation.home.HomeUiEffect
import com.pardess.musicplayer.presentation.home.HomeUiEvent
import com.pardess.musicplayer.presentation.home.HomeViewModel
import com.pardess.musicplayer.presentation.home.MusicBottomNavigationBar
import com.pardess.musicplayer.presentation.main.navgraph.mainGraph
import com.pardess.musicplayer.presentation.main.searchbox.SearchBoxEvent
import com.pardess.musicplayer.presentation.main.searchbox.SearchBoxState
import com.pardess.musicplayer.presentation.main.searchbox.SearchBoxViewModel
import com.pardess.musicplayer.presentation.playback.Playback
import com.pardess.musicplayer.presentation.playback.PlaybackEvent
import com.pardess.musicplayer.presentation.playback.PlaybackViewModel
import com.pardess.musicplayer.presentation.playlist.navgraph.playlistGraph
import com.pardess.musicplayer.presentation.songs.navgraph.songsGraph
import com.pardess.musicplayer.ui.theme.BackgroundColor
import com.pardess.musicplayer.ui.theme.PointColor


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MusicNavHost(
    navController: MusicNavController
) {
    MediaPermissionHandler {
        val playbackViewModel = hiltViewModel<PlaybackViewModel>()
        val searchBoxViewModel = hiltViewModel<SearchBoxViewModel>()
        val homeViewModel = hiltViewModel<HomeViewModel>()
        val homeUiState = homeViewModel.uiState.collectAsStateWithLifecycle()

        val currentRoute = homeUiState.value.currentRoute

        val searchBoxState = searchBoxViewModel.uiState.collectAsStateWithLifecycle()
        val allSongsState = playbackViewModel.allSongs.collectAsStateWithLifecycle()
        val playbackUiState = playbackViewModel.uiState.collectAsStateWithLifecycle()
        val windowBottomPadding =
            WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

        // 현재 백 스택에서 가장 최신의 `destination`을 가져옴 (null 방지)
        val navBackStackEntry by navController.navController.currentBackStackEntryAsState()

        // 현재 경로를 가져오되, null이면 기본 경로 설정
        homeViewModel.onEvent(
            HomeUiEvent.CurrentRoute(
                navBackStackEntry?.destination?.route ?: HomeScreen.Main.route
            )
        )

        val bottomBarHeight by animateDpAsState(
            targetValue = if (playbackUiState.value.expand || !HomeScreen.entries.any { it.route == currentRoute } || searchBoxState.value.expand) windowBottomPadding else 100.dp + windowBottomPadding,
            animationSpec = tween(400), label = "Playback Bar Height"
        )

        BackHandler(enabled = playbackUiState.value.expand || searchBoxState.value.expand) {
            if (searchBoxState.value.expand) {
                searchBoxViewModel.onEvent(SearchBoxEvent.Shrink)
                return@BackHandler
            }
        }

        val systemUiController = rememberSystemUiController()
        LaunchedEffect(bottomBarHeight) {
            if (bottomBarHeight == windowBottomPadding) {
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
        val lifecycleOwner = LocalLifecycleOwner.current
        LaunchedEffect(homeViewModel) {
            lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.effectFlow.collect { effect ->
                    when (effect) {
                        is HomeUiEffect.NavigateBottom -> {
                            navController.navigateToBottomBarRoute(effect.route)
                        }
                    }
                }
            }
        }

        Scaffold(
            bottomBar = {
                MusicBottomNavigationBar(
                    onEvent = homeViewModel::onEvent,
                    uiState = homeUiState.value,
                    modifier = Modifier.height(bottomBarHeight),
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
                            bottom = if (playbackUiState.value.playerState.currentSong != null) 116.dp else 0.dp
                        )
                ) {
                    NavHost(
                        navController = navController.navController,
                        startDestination = Navigation.Home.route,
                    ) {
                        navGraph(
                            onNavigateToRoute = navController::navigateToRoute,
                            upPress = navController::upPress,
                            allSongsState = allSongsState,
                            onPlaybackEvent = playbackViewModel::onEvent,
                            onSearchBoxEvent = { event ->
                                searchBoxViewModel.onEvent(
                                    event = event,
                                    onNavigateToRoute = navController::navigateToRoute,
                                )
                            },
                            searchBoxState = searchBoxState,
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
                        onEvent = playbackViewModel::onEvent,
                        onPlaybackUiEvent = playbackViewModel::onEvent,
                    )
                }
            }
        }
    }
}

fun NavGraphBuilder.navGraph(
    onNavigateToRoute: (String) -> Unit,
    upPress: () -> Unit,
    allSongsState: State<List<Song>>,
    searchBoxState: State<SearchBoxState>,
    onPlaybackEvent: (PlaybackEvent) -> Unit,
    onSearchBoxEvent: (SearchBoxEvent) -> Unit,
) {
    navigation(
        route = Navigation.Home.route,
        startDestination = Navigation.Main.route,
    ) {
        mainGraph(
            onNavigateToRoute = { route -> onNavigateToRoute(route) },
            upPress = upPress,
            allSongsState = allSongsState,
            searchBoxState = searchBoxState,
            onPlaybackEvent = onPlaybackEvent,
            onSearchBoxEvent = onSearchBoxEvent,
        )
        playlistGraph(
            onNavigateToRoute = { route -> onNavigateToRoute(route) },
            upPress = upPress,
            allSongsState = allSongsState,
            onPlaybackEvent = onPlaybackEvent
        )
        artistGraph(
            onNavigateToRoute = { route -> onNavigateToRoute(route) },
            upPress = upPress,
            onPlaybackEvent = onPlaybackEvent,
            allSongsState = allSongsState,
        )
        songsGraph(
            onNavigateToRoute = { route -> onNavigateToRoute(route) },
            upPress = upPress,
            onPlaybackEvent = onPlaybackEvent,
            allSongsState = allSongsState,
        )
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MediaPermissionHandler(
    onPermissionGranted: @Composable () -> Unit
) {
    val mediaPermissions = rememberMultiplePermissionsState(Constants.MEDIA_PERMISSIONS)

    LaunchedEffect(Unit) {
        if (!mediaPermissions.allPermissionsGranted) {
            mediaPermissions.launchMultiplePermissionRequest()
        }
    }

    if (!mediaPermissions.allPermissionsGranted) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("앱을 사용하기 위해서는 미디어 권한이 필요합니다.")
                Text("미디어 권한을 허락해주세요!")
                FullWidthButton(
                    text = "권한 허락",
                    onClick = {
                        mediaPermissions.launchMultiplePermissionRequest()
                    }
                )
            }
        }
    } else {
        onPermissionGranted()
    }
}


