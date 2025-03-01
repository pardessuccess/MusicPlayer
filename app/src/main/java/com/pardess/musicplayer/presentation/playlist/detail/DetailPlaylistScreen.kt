package com.pardess.musicplayer.presentation.playlist.detail

import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pardess.musicplayer.R
import com.pardess.musicplayer.domain.model.Song
import com.pardess.musicplayer.presentation.component.CheckSongItem
import com.pardess.musicplayer.presentation.playback.PlaybackEvent
import com.pardess.musicplayer.presentation.playlist.dialog.AddSongToPlaylistDialog
import com.pardess.musicplayer.presentation.playlist.dialog.DeleteSongDialog
import com.pardess.musicplayer.presentation.toSong
import com.pardess.musicplayer.ui.theme.BackgroundColor
import com.pardess.musicplayer.ui.theme.PointColor
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import my.nanihadesuka.compose.LazyColumnScrollbar
import my.nanihadesuka.compose.ScrollbarSettings

@Composable
fun PlaylistDetailScreen(
    onNavigateToRoute: (String) -> Unit,
    upPress: () -> Unit,
    uiState: State<DetailPlaylistUiState>,
    songState: State<List<Song>>,
    onEvent: (DetailPlaylistUiEvent) -> Unit,
    onPlaybackEvent: (PlaybackEvent) -> Unit
) {

    val lazyListState = rememberLazyListState()

    val playlistSongs = uiState.value.playlistSongs
    val playlist = uiState.value.playlist
    val deleteMode = uiState.value.deleteMode
    val selectedSongs = uiState.value.selectedSongs

    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(BackgroundColor)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
            ) {
                Text(
                    modifier = Modifier
                        .padding(top = 20.dp, bottom = 16.dp)
                        .basicMarquee(1),
                    text = playlist?.playlistName ?: "Playlist Name",
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black,
                    fontSize = 40.sp,
                    maxLines = 1,
                )
            }
            LazyColumnScrollbar(
                state = lazyListState,
                settings = ScrollbarSettings.Default.copy(
                    thumbThickness = 20.dp,
                    enabled = playlistSongs.size > 20,
                    thumbUnselectedColor = PointColor
                ),
                modifier = Modifier.weight(1f)
            ) {
                LazyColumn(
                    state = lazyListState,
                    contentPadding = PaddingValues(8.dp)
                ) {
                    items(
                        items = playlistSongs,
                        key = { song -> song.songPrimaryKey }
                    ) { playlistSong ->
                        var checked = selectedSongs.contains(playlistSong)
                        CheckSongItem(
                            checked = checked,
                            onClick = {
                                if (deleteMode) {
                                    checked = !checked
                                    onEvent(
                                        DetailPlaylistUiEvent.ToggleSongSelection(
                                            playlistSong = playlistSong,
                                            isSelected = checked
                                        )
                                    )
                                } else {
                                    onPlaybackEvent(
                                        PlaybackEvent.PlaySong(
                                            song = playlistSong.song.toSong(),
                                            playlist = playlistSongs.map { it.song.toSong() })
                                    )
                                }
                            },
                            playlistSong = playlistSong
                        )
                        if (playlistSong != playlistSongs.last()) {
                            Spacer(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(1.5.dp)
                                    .padding(horizontal = 6.dp)
                                    .background(Color.Gray.copy(0.2f))
                            )
                        }
                    }

                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Box(
                                modifier = Modifier
                                    .clickable {
                                        onEvent(DetailPlaylistUiEvent.ToggleDeleteMode)
                                    }
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
                                        if (deleteMode) {
                                            onEvent(DetailPlaylistUiEvent.DeleteSelectedSongs)
                                            onEvent(DetailPlaylistUiEvent.ToggleDeleteMode)
                                        } else onEvent(
                                            DetailPlaylistUiEvent.SetShowAddSongDialog(
                                                true
                                            )
                                        )
                                    }
                                    .weight(1f)
                            ) {
                                Icon(
                                    modifier = Modifier
                                        .size(80.dp)
                                        .align(Alignment.Center),
                                    painter = painterResource(if (deleteMode) R.drawable.ic_check else R.drawable.ic_add),
                                    contentDescription = null
                                )
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.weight(1f))
            Spacer(
                modifier = Modifier.height(
                    WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
                )
            )
        }

        if (uiState.value.dialogState.isShowAddSongDialog) {
            AddSongToPlaylistDialog(
                onEvent = onEvent,
                playlistId = uiState.value.playlist?.playlistId ?: -1,
                songs = songState.value
            )
        }

        if (uiState.value.dialogState.isShowDeleteSongDialog) {
            DeleteSongDialog(
                onEvent = onEvent,
            )
        }
    }
}