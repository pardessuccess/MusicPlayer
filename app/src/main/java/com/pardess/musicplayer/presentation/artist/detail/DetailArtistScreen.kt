package com.pardess.musicplayer.presentation.artist.detail

import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pardess.musicplayer.presentation.Status
import com.pardess.musicplayer.presentation.artist.detail.component.ArtistAlbumsSection
import com.pardess.musicplayer.presentation.artist.detail.component.ArtistSongsSection
import com.pardess.musicplayer.presentation.artist.detail.component.HorizontalPagerIndicator
import com.pardess.musicplayer.presentation.base.BaseScreen
import com.pardess.musicplayer.presentation.playback.PlaybackEvent
import com.pardess.musicplayer.presentation.playback.RepeatMode
import com.pardess.musicplayer.ui.theme.BackgroundColor
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest

@Composable
fun DetailArtistScreen(
    onNavigateToRoute: (String) -> Unit,
    onPlaybackEvent: (PlaybackEvent) -> Unit,
) {

    val viewModel: DetailArtistViewModel = hiltViewModel()

    BaseScreen(
        viewModel = viewModel,
        onEffect = { effect ->
            when (effect) {
                is DetailArtistUiEffect.NavigateToAlbum -> {
                    onNavigateToRoute(effect.route)
                }
            }
        }) { uiState, onEvent ->
        DetailArtistScreen(
            uiState = uiState,
            onEvent = onEvent,
            onPlaybackEvent = onPlaybackEvent
        )
    }
}

@Composable
private fun DetailArtistScreen(
    uiState: DetailArtistUiState,
    onEvent: (DetailArtistUiEvent) -> Unit,
    onPlaybackEvent: (PlaybackEvent) -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { 2 })

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HorizontalPagerIndicator(
            pagerState = pagerState,
            pageCount = 2,
            modifier = Modifier.padding(top = 16.dp, bottom = 4.dp),
            pageTitle = listOf("노래", "앨범")
        )

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f),
            userScrollEnabled = true
        ) { page ->
            when (page) {
                0 -> ArtistSongsPage(uiState.songsState, onPlaybackEvent)
                1 -> ArtistAlbumsPage(uiState.albumsState, onEvent)
            }
        }

        Spacer(
            modifier = Modifier.height(
                WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
            )
        )
    }
}

@Composable
private fun ArtistSongsPage(
    songsState: Status<List<com.pardess.musicplayer.domain.model.Song>>,
    onPlaybackEvent: (PlaybackEvent) -> Unit
) {
    val songListState = rememberLazyListState()

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        when (songsState) {
            is Status.Loading -> LoadingView()
            is Status.Error -> ErrorView(songsState.message)
            is Status.Success -> ArtistSongsSection(
                songs = songsState.data,
                songListState = songListState,
                onSongClick = { song ->
                    onPlaybackEvent(
                        PlaybackEvent.PlaySong(
                            songsState.data.indexOf(song), songsState.data
                        )
                    )
                    onPlaybackEvent(PlaybackEvent.RepeatMode(RepeatMode.REPEAT_ALL.value))
                }
            )
        }
    }
}

@Composable
private fun ArtistAlbumsPage(
    albumsState: Status<List<com.pardess.musicplayer.domain.model.Album>>,
    onEvent: (DetailArtistUiEvent) -> Unit
) {
    val albumListState = rememberLazyGridState()

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        when (albumsState) {
            is Status.Loading -> LoadingView()
            is Status.Error -> ErrorView(albumsState.message)
            is Status.Success -> ArtistAlbumsSection(
                albums = albumsState.data,
                albumListState = albumListState,
                onAlbumClick = { album ->
                    onEvent(
                        DetailArtistUiEvent.EnterDetailAlbum(albumId = album.id)
                    )
                }
            )
        }
    }
}

@Composable
private fun LoadingView() {
    CircularProgressIndicator()
}

@Composable
private fun ErrorView(message: String) {
    Text(text = "Error: $message", color = Color.Red)
}
