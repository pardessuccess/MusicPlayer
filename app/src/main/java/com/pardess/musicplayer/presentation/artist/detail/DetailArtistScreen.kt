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
import androidx.compose.runtime.State
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
import com.pardess.musicplayer.presentation.UiState
import com.pardess.musicplayer.presentation.artist.detail.component.ArtistAlbumsSection
import com.pardess.musicplayer.presentation.artist.detail.component.ArtistSongsSection
import com.pardess.musicplayer.presentation.artist.detail.component.HorizontalPagerIndicator
import com.pardess.musicplayer.presentation.playback.PlaybackEvent
import com.pardess.musicplayer.presentation.playback.RepeatMode
import com.pardess.musicplayer.ui.theme.BackgroundColor
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest

@Composable
fun DetailArtistScreen(
    onNavigateToRoute: (String) -> Unit,
    upPress: () -> Unit,
    onEvent: (DetailArtistUiEvent) -> Unit,
    onPlaybackEvent: (PlaybackEvent) -> Unit,
    uiState: State<DetailArtistUiState>,
) {

    val songsState = uiState.value.songsState
    val albumsState = uiState.value.albumsState

    val pagerState = rememberPagerState(pageCount = { 2 })

    val songListState = rememberLazyListState()
    val albumListState = rememberLazyGridState()

    val pageTitles = listOf("노래", "앨범")

    var isButtonVisible by remember { mutableStateOf(false) }

    val isSongListIdle by remember {
        derivedStateOf { !songListState.isScrollInProgress }
    }

    LaunchedEffect(isSongListIdle) {
        snapshotFlow { isSongListIdle }
            .collectLatest { isIdle ->
                if (isIdle) {
                    delay(1000L) // ✅ 1초 후 버튼 표시
                    isButtonVisible = true
                } else {
                    isButtonVisible = false
                }
            }
    }

    val transition = updateTransition(
        targetState = (pagerState.currentPage == 0 && isButtonVisible),
        label = "ButtonVisibilityTransition"
    )

    val animatedHeight by transition.animateDp(
        transitionSpec = { tween(durationMillis = 400) }, // ✅ 400ms 애니메이션
        label = "OffsetAnimation"
    ) { isVisible ->
        if (isVisible) 100.dp else 0.dp // ✅ 버튼 표시 or 숨김
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HorizontalPagerIndicator(
            pagerState = pagerState,
            pageCount = pageTitles.size,
            modifier = Modifier.padding(top = 16.dp, bottom = 4.dp),
            pageTitle = pageTitles
        )

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f),
            userScrollEnabled = true
        ) { page ->
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.TopCenter
            ) {
                when (page) {
                    0 -> {
                        when (songsState) {
                            is UiState.Loading -> CircularProgressIndicator(modifier = Modifier.fillMaxSize())
                            is UiState.Error -> Text(
                                "Error: ${songsState.message}",
                                color = Color.Red,
                                modifier = Modifier.align(Alignment.Center)
                            )

                            is UiState.Success -> ArtistSongsSection(
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

                    1 -> {
                        when (albumsState) {
                            is UiState.Loading -> CircularProgressIndicator(modifier = Modifier.fillMaxSize())
                            is UiState.Error -> Text(
                                "Error: ${albumsState.message}",
                                color = Color.Red,
                                modifier = Modifier.align(Alignment.Center)
                            )

                            is UiState.Success -> ArtistAlbumsSection(
                                albums = albumsState.data,
                                albumListState = albumListState,
                                onAlbumClick = { album ->
                                    onEvent(
                                        DetailArtistUiEvent.EnterDetailAlbum(
                                            albumId = album.id
                                        )
                                    )
                                },
                            )
                        }
                    }
                }
            }
        }

        Spacer(
            modifier = Modifier.height(
                WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
            )
        )
    }
}


