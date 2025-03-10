package com.pardess.artist.album

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pardess.common.Result
import com.pardess.common.Utils.toTime
import com.pardess.common.base.BaseScreen
import com.pardess.model.Album
import com.pardess.playback.PlaybackEvent
import com.pardess.ui.MusicImage
import com.pardess.ui.SongItem

@Composable
fun DetailAlbumScreen(
    onNavigateToRoute: (String) -> Unit,
    onPlaybackEvent: (PlaybackEvent) -> Unit,
) {

    val viewModel: DetailAlbumViewModel = hiltViewModel()

    BaseScreen(
        viewModel = viewModel,
        onEffect = { effect ->
            when (effect) {
                is DetailAlbumUiEffect.NavigateToAlbum -> {
                    onNavigateToRoute(effect.route)
                }

                else -> {}
            }
        }) { uiState, onEvent ->
        DetailAlbumScreen(
            onPlaybackEvent = onPlaybackEvent,
            uiState = uiState,
            onEvent = onEvent
        )
    }
}

@Composable
private fun DetailAlbumScreen(
    onPlaybackEvent: (PlaybackEvent) -> Unit,
    uiState: DetailAlbumUiState,
    onEvent: (DetailAlbumUiEvent) -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        when (uiState.albumState) {
            is Result.Success -> {
                val album = uiState.albumState.data
                val albums =
                    (uiState.albumsState as? Result.Success<List<Album>>)?.data ?: emptyList()

                val fullTime = album.songs.sumOf { it.duration.toMillis() / 1000 }.toTime()

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(12.dp),
                ) {
                    // 앨범 정보 헤더
                    item {
                        AlbumHeader(album, fullTime)
                    }

                    // 노래 리스트
                    items(album.songs.size) { index ->
                        SongItem(
                            song = album.songs[index],
                            onClick = {
                                onPlaybackEvent(
                                    PlaybackEvent.PlaySong(
                                        index,
                                        album.songs
                                    )
                                )
                            }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                    // 다른 앨범 리스트
                    if (albums.isNotEmpty()) {
                        item {
                            Text(
                                modifier = Modifier.padding(start = 6.dp),
                                text = "다른 앨범",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        item {
                            LazyRow(
                                contentPadding = PaddingValues(vertical = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(albums) { album ->
                                    AlbumCard(
                                        album = album,
                                        onClick = { onEvent(DetailAlbumUiEvent.SelectAlbum(album)) }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            is Result.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(50.dp),
                    color = Color.Black
                )
            }

            is Result.Error -> {
                Text(
                    text = "Error",
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.Red
                )
            }
        }
    }
}

@Composable
private fun AlbumHeader(album: Album, fullTime: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 18.dp),
    ) {
        MusicImage(
            filePath = album.songs.first().data,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f),
            type = "album"
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = album.title,
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 36.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = album.artistName,
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.width(8.dp))
            if (album.year != 0){
                Text(text = album.year.toString())
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(text = fullTime, fontSize = 14.sp)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Songs")
    }
}

@Composable
private fun AlbumCard(
    album: Album,
    onClick: () -> Unit
) {
    Card(
        elevation = CardDefaults.elevatedCardElevation(2.dp),
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .width(150.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            MusicImage(
                filePath = album.songs.first().data,
                modifier = Modifier
                    .size(150.dp)
                    .aspectRatio(1f),
                type = "artist"
            )
            Text(
                text = album.title,
                color = Color.Black,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(8.dp),
                maxLines = 2,
                minLines = 2
            )
        }
    }
}
