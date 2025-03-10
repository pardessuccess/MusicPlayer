package com.pardess.songs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.pardess.designsystem.BackgroundColor
import com.pardess.designsystem.NavigationBarHeight
import com.pardess.designsystem.PointColor
import com.pardess.model.Song
import com.pardess.playback.PlaybackEvent
import com.pardess.ui.SongItem
import my.nanihadesuka.compose.LazyColumnScrollbar
import my.nanihadesuka.compose.ScrollbarSettings


@Composable
fun SongsScreen(
    onNavigateToRoute: (String) -> Unit,
    onPlaybackEvent: (PlaybackEvent) -> Unit,
    allSongs: List<Song>
) {

    val state = rememberLazyListState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
        ) {
            LazyColumnScrollbar(
                state = state,
                settings = ScrollbarSettings.Default.copy(
                    thumbThickness = 20.dp,
                    thumbUnselectedColor = PointColor,
                    enabled = allSongs.size > 20
                )
            ) {
                LazyColumn(
                    state = state,
                    contentPadding = PaddingValues(8.dp)
                ) {
                    items(allSongs) { song ->
                        SongItem(
                            song = song,
                            onClick = {
                                onPlaybackEvent(
                                    PlaybackEvent.PlaySong(
                                        allSongs.indexOf(song),
                                        allSongs
                                    )
                                )
                            }
                        )
                        if (song != allSongs.last()) {
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
        Spacer(
            modifier = Modifier.height(
                NavigationBarHeight + WindowInsets.navigationBars.asPaddingValues()
                    .calculateBottomPadding()
            )
        )
    }
}
