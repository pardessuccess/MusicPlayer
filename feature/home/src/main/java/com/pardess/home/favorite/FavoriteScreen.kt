package com.pardess.home.favorite

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import com.pardess.common.base.BaseScreen
import com.pardess.designsystem.PointColor
import com.pardess.model.join.FavoriteSong
import com.pardess.playback.PlaybackEvent
import com.pardess.ui.FavoriteSongItem
import my.nanihadesuka.compose.LazyColumnScrollbar
import my.nanihadesuka.compose.ScrollbarSettings

@Composable
fun FavoriteScreen(
    onPlaybackEvent: (PlaybackEvent) -> Unit,
) {
    val viewModel = hiltViewModel<FavoriteViewModel>()
    val context = LocalContext.current

    BaseScreen(
        viewModel = viewModel,
        onEffect = { effect ->
            when (effect) {
                is FavoriteUiEffect.FavoriteDelete -> {
                    Toast.makeText(context, "즐겨찾기가 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    ) { uiState, onEvent ->
        FavoriteScreen(
            uiState = uiState,
            onEvent = onEvent,
            onPlaybackEvent = onPlaybackEvent
        )
    }
}

@Composable
private fun FavoriteScreen(
    uiState: FavoriteUiState,
    onEvent: (FavoriteUiEvent) -> Unit,
    onPlaybackEvent: (PlaybackEvent) -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Header(title = "좋아하는 노래")
            FavoriteSongList(
                favoriteSongs = uiState.favoriteSongs,
                onPlaybackEvent = onPlaybackEvent,
                onLongClick = { favoriteSong ->
                    onEvent(
                        FavoriteUiEvent.ShowRemoveDialog(
                            favoriteSong
                        )
                    )
                }
            )
            Spacer(
                modifier = Modifier.height(
                    WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
                )
            )
        }
        if (uiState.showRemoveDialog) {
            RemoveFavoriteDialog(
                modifier = Modifier.fillMaxWidth(),
                onEvent = onEvent
            )
        }
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
fun FavoriteSongList(
    favoriteSongs: List<FavoriteSong>,
    onPlaybackEvent: (PlaybackEvent) -> Unit,
    onLongClick: (FavoriteSong) -> Unit
) {
    val lazyListState = rememberLazyListState()
    LazyColumnScrollbar(
        state = lazyListState,
        settings = ScrollbarSettings.Default.copy(
            thumbThickness = 20.dp,
            enabled = favoriteSongs.size > 20,
            thumbUnselectedColor = PointColor
        ),
        modifier = Modifier
    ) {
        LazyColumn(
            state = lazyListState,
            contentPadding = PaddingValues(8.dp)
        ) {
            items(favoriteSongs) { favoriteSong ->
                FavoriteSongItem(
                    favoriteSong = favoriteSong,
                    onClick = {
                        onPlaybackEvent(
                            PlaybackEvent.PlaySong(
                                favoriteSongs.indexOf(favoriteSong),
                                favoriteSongs.map { it.song }
                            )
                        )
                    },
                    onLongClick = { onLongClick(favoriteSong) }
                )
                if (favoriteSong != favoriteSongs.last()) {
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
