package com.pardess.playlist.dialog

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.pardess.designsystem.Gray300
import com.pardess.designsystem.PointColor
import com.pardess.model.PlaylistSong
import com.pardess.model.Song
import com.pardess.playlist.detail.DetailPlaylistUiEvent
import com.pardess.ui.MusicImage
import com.pardess.ui.TwoBottomButton
import my.nanihadesuka.compose.LazyColumnScrollbar
import my.nanihadesuka.compose.ScrollbarSettings

@Composable
fun AddSongToPlaylistDialog(
    modifier: Modifier = Modifier,
    onEvent: (DetailPlaylistUiEvent) -> Unit,
    playlistId: Long,
    songs: List<Song>,
) {

    val lazyListState = rememberLazyListState()

    Dialog(
        onDismissRequest = { onEvent(DetailPlaylistUiEvent.SetShowAddSongDialog(false)) }
    ) {
        Column(
            modifier = modifier.background(
                shape = RoundedCornerShape(20.dp),
                color = Color.White
            )
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxHeight(0.9f)
            ) {
                Spacer(modifier = Modifier.height(5.dp))
                Text("음악 등록", fontSize = 45.sp)
                Spacer(modifier = Modifier.height(5.dp))
                LazyColumnScrollbar(
                    modifier = Modifier.weight(1f),
                    state = lazyListState,
                    settings = ScrollbarSettings.Default.copy(
                        thumbThickness = 20.dp,
                        enabled = songs.size > 20,
                        thumbUnselectedColor = PointColor
                    ),
                ) {
                    LazyColumn(
                        state = lazyListState,
                        contentPadding = PaddingValues(4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        items(songs.size) { index ->
                            var checked by rememberSaveable { mutableStateOf(false) }
                            SelectedSongItem(
                                checked = checked,
                                setChecked = {
                                    checked = it
                                    onEvent(
                                        DetailPlaylistUiEvent.ToggleSongSelection(
                                            PlaylistSong(0L, playlistId, songs[index]),
                                            it
                                        )
                                    )
                                },
                                songs[index]
                            )
                            Spacer(modifier = Modifier.height(5.dp))
                        }
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                TwoBottomButton(
                    modifier = Modifier,
                    text1 = "등록",
                    text2 = "뒤로",
                    onClick1 = {
                        onEvent(DetailPlaylistUiEvent.SaveSongToPlaylist)
                        onEvent(DetailPlaylistUiEvent.SetShowAddSongDialog(false))
                    },
                    onClick2 = {
                        onEvent(DetailPlaylistUiEvent.SetShowAddSongDialog(false))
                    }
                )
            }
        }
    }
}

@Composable
fun SelectedSongItem(
    checked: Boolean,
    setChecked: (Boolean) -> Unit,
    song: Song
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (checked) PointColor else Gray300, label = ""
    )
    val borderSize by animateDpAsState(
        targetValue = if (checked) 0.dp else 1.dp, label = ""
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(20.dp)
            )
            .border(
                width = borderSize,
                color = Color(0xFFCCCCCC),
                shape = RoundedCornerShape(20.dp)
            )
            .clip(RoundedCornerShape(20.dp))
            .clickable {
                setChecked(!checked)
            }
            .padding(horizontal = 10.dp, vertical = 2.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(2f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            MusicImage(
                song.data,
                Modifier
                    .padding(end = 8.dp)
                    .size(80.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(
                modifier = Modifier.padding(end = 8.dp)
            ) {
                Text(
                    text = song.title,
                    fontSize = 30.sp,
                    lineHeight = 30.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = song.artistName, fontSize = 20.sp, color = Color.Gray,
                    maxLines = 1
                )
            }
        }
    }
}