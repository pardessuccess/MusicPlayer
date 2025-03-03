package com.pardess.musicplayer.presentation.artist.album

import androidx.compose.foundation.background
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
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pardess.musicplayer.domain.model.Album
import com.pardess.musicplayer.presentation.UiState
import com.pardess.musicplayer.presentation.component.MusicImage
import com.pardess.musicplayer.presentation.component.SongItem
import com.pardess.musicplayer.presentation.navigation.Screen
import com.pardess.musicplayer.presentation.playback.PlaybackEvent
import com.pardess.musicplayer.ui.theme.PointColor


@Composable
fun DetailAlbumScreen(
    onNavigateToRoute: (String) -> Unit,
    upPress: () -> Unit,
    onPlaybackEvent: (PlaybackEvent) -> Unit,
    uiState: State<DetailAlbumUiState>
) {

    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        when (uiState.value.albumState) {
            is UiState.Success -> {
                val album = (uiState.value.albumState as UiState.Success<Album>).data
                val albums = (uiState.value.albumsState as UiState.Success<List<Album>>).data

                val fullTime = "album.songs.sumOf { it.duration }.toTime()"

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(12.dp),
                ) {
                    // 앨범 정보 헤더
                    item {
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
                                Text(text = album.year.toString())
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = fullTime, fontSize = 14.sp)
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(text = "Songs")
                        }
                    }

                    // 노래 리스트
                    items(album.songs) { song ->
                        SongItem(
                            song = song,
                            onClick = {
                                onPlaybackEvent(
                                    PlaybackEvent.PlaySong(
                                        album.songs.indexOf(song),
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
                                    RowAlbumItem(
                                        album = album,
                                        onClickAlbum = {
                                            onNavigateToRoute(Screen.DetailArtist.route + "/${album.artistId}/${album.id}")
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            is UiState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.Black
                )
            }

            is UiState.Error -> {
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
fun RowAlbumItem(
    album: Album,
    onClickAlbum: () -> Unit,
) {
    Card(

    ) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .width(150.dp)
                .clickable {
                    onClickAlbum()
                },
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


@Composable
fun AlbumItem(
    album: Album,
    onClickAlbum: () -> Unit,
) {
    Card(
        elevation = CardDefaults.elevatedCardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(PointColor)
                .clickable {
                    onClickAlbum()
                },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            MusicImage(
                filePath = album.songs.first().data,
                modifier = Modifier
                    .fillMaxSize()
                    .aspectRatio(1f),
                type = "artist"
            )

            Text(
                text = album.title,
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(8.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
