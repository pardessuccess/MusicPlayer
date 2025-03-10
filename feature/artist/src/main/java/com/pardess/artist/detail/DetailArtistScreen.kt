package com.pardess.artist.detail

import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pardess.artist.detail.component.ArtistAlbumsSection
import com.pardess.artist.detail.component.ArtistSongsSection
import com.pardess.artist.detail.component.HorizontalPagerIndicator
import com.pardess.common.base.BaseScreen
import com.pardess.designsystem.BackgroundColor
import com.pardess.playback.PlaybackEvent
import com.pardess.common.Result
import com.pardess.model.Album
import com.pardess.model.Song
import com.pardess.playback.RepeatMode
import com.pardess.ui.ErrorView
import com.pardess.ui.LoadingView

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

                else -> {}
            }
        }) { uiState, onEvent ->
        DetailArtistScreen(
            uiState = uiState,
            onEvent = onEvent,
            onPlaybackEvent = onPlaybackEvent
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
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
    songsState: Result<List<Song>>,
    onPlaybackEvent: (PlaybackEvent) -> Unit
) {
    val songListState = rememberLazyListState()

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        when (songsState) {
            is Result.Loading -> LoadingView()
            is Result.Error -> ErrorView(
                modifier = Modifier.align(Alignment.Center),
                songsState.message
            )

            is Result.Success -> ArtistSongsSection(
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
    albumsState: Result<List<Album>>,
    onEvent: (DetailArtistUiEvent) -> Unit
) {
    val albumListState = rememberLazyGridState()

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        when (albumsState) {
            is Result.Loading -> LoadingView()
            is Result.Error -> ErrorView(
                modifier = Modifier.align(Alignment.Center),
                albumsState.message
            )

            is Result.Success -> ArtistAlbumsSection(
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
