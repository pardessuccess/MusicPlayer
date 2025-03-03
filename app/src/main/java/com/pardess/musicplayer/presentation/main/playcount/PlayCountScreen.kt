package com.pardess.musicplayer.presentation.main.playcount

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pardess.musicplayer.data.entity.join.PlayCountSong
import com.pardess.musicplayer.presentation.component.PlayCountSongItem
import com.pardess.musicplayer.presentation.playback.PlaybackEvent
import com.pardess.musicplayer.presentation.toSong
import com.pardess.musicplayer.ui.theme.PointColor
import my.nanihadesuka.compose.LazyColumnScrollbar
import my.nanihadesuka.compose.ScrollbarSettings

@Composable
fun PlayCountScreen(
    uiState: State<PlayCountUiState>,
    onPlaybackEvent: (PlaybackEvent) -> Unit,
) {
    // derivedStateOf를 사용해 필요한 상태만 분리
    val playCountSongs by remember { derivedStateOf { uiState.value.playCountSongs } }

    Column(modifier = Modifier.fillMaxSize()) {
        Header(title = "재생 횟수")
        PlayCountSongList(
            playCountSongs = playCountSongs,
            onPlaybackEvent = onPlaybackEvent,
        )
        Spacer(
            modifier = Modifier.height(
                WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
            )
        )
    }
}

@Composable
fun Header(title: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
    ) {
        Text(
            modifier = Modifier.padding(top = 20.dp, bottom = 16.dp),
            text = title,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black,
            fontSize = 40.sp,
        )
    }
}

@Composable
fun PlayCountSongList(
    playCountSongs: List<PlayCountSong>,
    onPlaybackEvent: (PlaybackEvent) -> Unit,
) {
    val lazyListState = rememberLazyListState()
    LazyColumnScrollbar(
        state = lazyListState,
        settings = ScrollbarSettings.Default.copy(
            thumbThickness = 20.dp,
            enabled = playCountSongs.size > 20,
            thumbUnselectedColor = PointColor
        ),
        modifier = Modifier
    ) {
        LazyColumn(
            state = lazyListState,
            contentPadding = PaddingValues(8.dp)
        ) {
            items(playCountSongs) { playCountSong ->
                PlayCountSongItem(
                    playCountSong = playCountSong,
                    onClick = {
                        onPlaybackEvent(
                            PlaybackEvent.PlaySong(
                                playCountSongs.indexOf(playCountSong),
                                playCountSongs.map { it.song.toSong() }
                            )
                        )
                    },
                )
                if (playCountSong != playCountSongs.last()) {
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.5.dp)
                            .padding(horizontal = 6.dp)
                            .background(Color.Gray.copy(0.2f))
                    )

                }
            }
        }
    }
}