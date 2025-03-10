package com.pardess.home

import android.Manifest
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.pardess.common.base.BaseScreen
import com.pardess.designsystem.BackgroundColor
import com.pardess.designsystem.NavigationBarHeight
import com.pardess.home.component.PopularSection
import com.pardess.home.component.SearchBoxSection
import com.pardess.home.component.SelectButtonSection
import com.pardess.home.component.Top3SongsSection
import com.pardess.playback.PlaybackEvent
import com.pardess.root.RootUiEvent

@Composable
fun MainScreen(
    saveState: (String, String) -> Unit,
    onNavigateToRoute: (String) -> Unit,
    onHomeUiEvent: (RootUiEvent) -> Unit,
    onPlaybackEvent: (PlaybackEvent) -> Unit,
) {
    val viewModel = hiltViewModel<MainViewModel>()
    val context = LocalContext.current
    BaseScreen(
        viewModel = viewModel,
        onEffect = { effect ->
            when (effect) {
                is MainUiEffect.Navigate -> {
                    onNavigateToRoute(effect.route)
                }

                is MainUiEffect.Search -> {
                    saveState("searchQuery", effect.query)
                }

                is MainUiEffect.RecordingMessage -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    ) { uiState, onEvent ->
        MainScreen(
            uiState = uiState,
            onEvent = onEvent,
            onHomeUiEvent = onHomeUiEvent,
            onPlaybackEvent = onPlaybackEvent,
        )
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun MainScreen(
    uiState: MainUiState,
    onEvent: (MainUiEvent) -> Unit,
    onHomeUiEvent: (RootUiEvent) -> Unit,
    onPlaybackEvent: (PlaybackEvent) -> Unit,
) {

    val expand = uiState.searchBoxExpand
    var hasRecordPermission by remember { mutableStateOf(false) }
    val recordPermissionState = rememberPermissionState(Manifest.permission.RECORD_AUDIO)
    hasRecordPermission = recordPermissionState.status.isGranted


    BackHandler(enabled = expand) {
        onEvent(MainUiEvent.SearchBoxShrink)
        onHomeUiEvent(RootUiEvent.SearchBoxShrink)
    }

    val lazyListState = rememberLazyListState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = BackgroundColor)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(color = BackgroundColor),
            state = lazyListState,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Spacer(modifier = Modifier.height(132.dp))
                Top3SongsSection(
                    uiState = uiState,
                    onPlaybackEvent = onPlaybackEvent,
                    favoriteSongs = uiState.favoriteSongs,
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                ) {
                    SelectButtonSection(
                        onEvent = onEvent,
                        onPlaybackEvent = onPlaybackEvent
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    PopularSection(
                        onEvent = onEvent,
                        artists = uiState.popularArtists,
                        albums = uiState.popularAlbums,
                    )
                }
            }

            item {
                Spacer(
                    modifier = Modifier.height(
                        NavigationBarHeight + WindowInsets.navigationBars.asPaddingValues()
                            .calculateBottomPadding()
                    )
                )
            }
        }

        SearchBoxSection(
            uiState = uiState,
            modifier = Modifier.align(Alignment.TopCenter),
            lazyListState = lazyListState,
            searchHistories = uiState.searchHistories,
            expand = expand,
            onEvent = onEvent,
            onHomeUiEvent = onHomeUiEvent
        )
    }
}


