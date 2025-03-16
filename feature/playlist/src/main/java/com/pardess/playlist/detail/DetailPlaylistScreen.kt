package com.pardess.playlist.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pardess.common.base.BaseScreen
import com.pardess.designsystem.BackgroundColor
import com.pardess.designsystem.PointColor
import com.pardess.model.Song
import com.pardess.playback.PlaybackEvent
import com.pardess.playlist.R
import com.pardess.playlist.dialog.AddSongToPlaylistDialog
import com.pardess.ui.CheckSongItem
import my.nanihadesuka.compose.LazyColumnScrollbar
import my.nanihadesuka.compose.ScrollbarSettings

@Composable
fun PlaylistDetailScreen(
    allSongs: List<Song>,
    onPlaybackEvent: (PlaybackEvent) -> Unit
) {
    val viewModel = hiltViewModel<DetailPlaylistViewModel>()

    BaseScreen(
        viewModel = viewModel,
        onEffect = {
        }
    ) { uiState, onEvent ->
        PlaylistDetailScreen(
            uiState = uiState,
            allSongs = allSongs,
            onEvent = onEvent,
            onPlaybackEvent = onPlaybackEvent
        )
    }
}

@Composable
private fun PlaylistDetailScreen(
    uiState: DetailPlaylistUiState,
    allSongs: List<Song>,
    onEvent: (DetailPlaylistUiEvent) -> Unit,
    onPlaybackEvent: (PlaybackEvent) -> Unit
) {
    val lazyListState = rememberLazyListState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {
        Column {
            PlaylistHeader(uiState)
            PlaylistSongList(
                modifier = Modifier.weight(1f),
                uiState,
                lazyListState,
                onEvent,
                onPlaybackEvent
            )
        }
        PlaylistDialogs(uiState, onEvent, allSongs)
    }
}

/**
 * 🔹 Playlist 헤더 (제목)
 */
@Composable
fun PlaylistHeader(uiState: DetailPlaylistUiState) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
    ) {
        Text(
            modifier = Modifier
                .padding(top = 20.dp, bottom = 16.dp),
            text = uiState.playlist?.playlistName ?: "Playlist Name",
            fontWeight = FontWeight.SemiBold,
            color = Color.Black,
            fontSize = 40.sp,
            maxLines = 1,
        )
    }
}

/**
 * 🔹 Playlist 노래 리스트
 */
@Composable
fun PlaylistSongList(
    modifier: Modifier = Modifier,
    uiState: DetailPlaylistUiState,
    lazyListState: androidx.compose.foundation.lazy.LazyListState,
    onEvent: (DetailPlaylistUiEvent) -> Unit,
    onPlaybackEvent: (PlaybackEvent) -> Unit
) {
    LazyColumnScrollbar(
        state = lazyListState,
        settings = ScrollbarSettings.Default.copy(
            thumbThickness = 20.dp,
            enabled = uiState.playlistSongs.size > 20,
            thumbUnselectedColor = PointColor
        ),
        modifier = modifier
    ) {
        LazyColumn(
            state = lazyListState,
            contentPadding = PaddingValues(8.dp)
        ) {
            items(
                items = uiState.playlistSongs,
                key = { song -> song.songPrimaryKey }
            ) { playlistSong ->
                val checked = uiState.selectedSongs.contains(playlistSong)
                CheckSongItem(
                    checked = checked,
                    onClick = {
                        if (uiState.deleteMode) {
                            onEvent(
                                DetailPlaylistUiEvent.ToggleSongSelection(
                                    playlistSong = playlistSong,
                                    isSelected = !checked
                                )
                            )
                        } else {
                            onPlaybackEvent(
                                PlaybackEvent.PlaySong(
                                    uiState.playlistSongs.indexOf(playlistSong),
                                    uiState.playlistSongs.map { it.song }
                                )
                            )
                        }
                    },
                    playlistSong = playlistSong
                )
            }
            item { PlaylistActions(uiState, onEvent) }
        }
    }
}

/**
 * 🔹 Playlist 편집 액션 (삭제 모드 토글 / 추가 및 삭제 버튼)
 */
@Composable
fun PlaylistActions(
    uiState: DetailPlaylistUiState,
    onEvent: (DetailPlaylistUiEvent) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .clickable { onEvent(DetailPlaylistUiEvent.ToggleDeleteMode) }
                .weight(1f)
        ) {
            Icon(
                modifier = Modifier
                    .size(80.dp)
                    .align(Alignment.Center),
                painter = painterResource(R.drawable.ic_remove),
                contentDescription = null
            )
        }
        Box(
            modifier = Modifier
                .clickable {
                    if (uiState.deleteMode) {
                        onEvent(DetailPlaylistUiEvent.DeleteSelectedSongs)
                    } else onEvent(DetailPlaylistUiEvent.SetShowAddSongDialog(true))
                }
                .weight(1f)
        ) {
            Icon(
                modifier = Modifier
                    .size(80.dp)
                    .align(Alignment.Center),
                painter = painterResource(
                    if (uiState.deleteMode) R.drawable.ic_check else R.drawable.ic_add
                ),
                contentDescription = null
            )
        }
    }
}

/**
 * 🔹 다이얼로그 관리 (곡 추가 / 삭제)
 */
@Composable
fun PlaylistDialogs(
    uiState: DetailPlaylistUiState,
    onEvent: (DetailPlaylistUiEvent) -> Unit,
    allSongs: List<Song>
) {
    if (uiState.isShowAddSongDialog) {
        AddSongToPlaylistDialog(
            onEvent = onEvent,
            playlistId = uiState.playlist?.playlistId ?: -1,
            songs = allSongs
        )
    }
}