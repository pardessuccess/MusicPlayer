package com.pardess.musicplayer.presentation.main

import android.Manifest
import android.content.Intent
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.pardess.musicplayer.R
import com.pardess.musicplayer.domain.model.SearchHistory
import com.pardess.musicplayer.presentation.base.BaseScreen
import com.pardess.musicplayer.presentation.home.HomeUiEvent
import com.pardess.musicplayer.presentation.main.component.PopularSection
import com.pardess.musicplayer.presentation.main.component.SearchBoxSection
import com.pardess.musicplayer.presentation.main.component.SearchHistorySection
import com.pardess.musicplayer.presentation.main.component.SelectButtonSection
import com.pardess.musicplayer.presentation.main.component.Top3SongsSection
import com.pardess.musicplayer.presentation.playback.PlaybackEvent
import com.pardess.musicplayer.presentation.playlist.dialog.SpeechStatus
import com.pardess.musicplayer.presentation.playlist.dialog.setSpeechRecognizer
import com.pardess.musicplayer.ui.theme.BackgroundColor
import com.pardess.musicplayer.ui.theme.NavigationBarHeight
import com.pardess.musicplayer.ui.theme.PointColor
import com.pardess.musicplayer.ui.theme.TextColor

@Composable
fun MainScreen(
    saveState: (String, String) -> Unit,
    onNavigateToRoute: (String) -> Unit,
    onHomeUiEvent: (HomeUiEvent) -> Unit,
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
    onHomeUiEvent: (HomeUiEvent) -> Unit,
    onPlaybackEvent: (PlaybackEvent) -> Unit,
) {

    val expand = uiState.searchBoxExpand
    var hasRecordPermission by remember { mutableStateOf(false) }
    val recordPermissionState = rememberPermissionState(Manifest.permission.RECORD_AUDIO)
    hasRecordPermission = recordPermissionState.status.isGranted


    BackHandler(enabled = expand) {
        onEvent(MainUiEvent.SearchBoxShrink)
        onHomeUiEvent(HomeUiEvent.SearchBoxShrink)
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


