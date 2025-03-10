package com.pardess.musicplayer.presentation.main.history

import android.widget.Toast
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pardess.musicplayer.data.entity.join.HistorySong
import com.pardess.musicplayer.presentation.base.BaseScreen
import com.pardess.musicplayer.presentation.common.component.FullWidthButton
import com.pardess.musicplayer.presentation.common.component.SongItem
import com.pardess.musicplayer.presentation.playback.PlaybackEvent
import com.pardess.musicplayer.presentation.toSong
import com.pardess.musicplayer.ui.theme.PointColor
import my.nanihadesuka.compose.LazyColumnScrollbar
import my.nanihadesuka.compose.ScrollbarSettings

@Composable
fun HistoryScreen(
    onPlaybackEvent: (PlaybackEvent) -> Unit,
) {
    val viewModel = hiltViewModel<HistoryViewModel>()
    val context = LocalContext.current

    BaseScreen(
        viewModel = viewModel,
        onEffect = { effect ->
            when (effect) {
                is HistoryEffect.HistoryRemoved -> {
                    Toast.makeText(context, "기록이 초기화되었습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    ) { uiState, onEvent ->
        HistoryScreen(
            uiState = uiState,
            onEvent = onEvent,
            onPlaybackEvent = onPlaybackEvent
        )
    }
}

@Composable
private fun HistoryScreen(
    uiState: HistoryUiState,
    onEvent: (HistoryUiEvent) -> Unit,
    onPlaybackEvent: (PlaybackEvent) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Header(title = "최근 기록")
        HistorySongList(
            modifier = Modifier.weight(1f),
            historySongs = uiState.historySongs,
            onPlaybackEvent = onPlaybackEvent
        )
        Spacer(modifier = Modifier.weight(1f))
        FullWidthButton(
            text = "초기화",
            onClick = { onEvent(HistoryUiEvent.ResetHistory) }
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
        modifier = Modifier.fillMaxWidth(),
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
fun HistorySongList(
    modifier: Modifier = Modifier,
    historySongs: List<HistorySong>,
    onPlaybackEvent: (PlaybackEvent) -> Unit,
) {
    val lazyListState = rememberLazyListState()
    LazyColumnScrollbar(
        state = lazyListState,
        settings = ScrollbarSettings.Default.copy(
            thumbThickness = 20.dp,
            enabled = historySongs.size > 20,
            thumbUnselectedColor = PointColor
        ),
        modifier = modifier
    ) {
        LazyColumn(
            state = lazyListState,
            contentPadding = PaddingValues(8.dp)
        ) {
            items(historySongs) { historySong ->
                SongItem(
                    song = historySong.song.toSong(),
                    onClick = {
                        onPlaybackEvent(
                            PlaybackEvent.PlaySong(
                                historySongs.indexOf(historySong),
                                historySongs.map { it.song.toSong() }
                            )
                        )
                    },
                )
                if (historySong != historySongs.last()) {
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
