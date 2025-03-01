package com.pardess.musicplayer.presentation.artist.detail.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.pardess.musicplayer.domain.model.Album
import com.pardess.musicplayer.domain.model.Song
import com.pardess.musicplayer.presentation.artist.album.AlbumItem
import com.pardess.musicplayer.presentation.artist.detail.DetailArtistUiEvent
import com.pardess.musicplayer.presentation.component.SongItem
import my.nanihadesuka.compose.LazyColumnScrollbar
import my.nanihadesuka.compose.LazyVerticalGridScrollbar


@Composable
fun ArtistAlbumsSection(
    albums: List<Album>,
    albumListState: LazyGridState,
    onAlbumClick: (Album) -> Unit,
) {
    LazyVerticalGridScrollbar(
        state = albumListState,
    ) {
        LazyVerticalGrid(
            state = albumListState,
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(albums) { album ->
                AlbumItem(
                    album = album,
                    onClickAlbum = { onAlbumClick(album) }
                )
            }
        }
    }
}


@Composable
fun ArtistSongsSection(
    songs: List<Song>,
    songListState: LazyListState,
    onSongClick: (Song) -> Unit
) {
    LazyColumnScrollbar(
        state = songListState,
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            state = songListState,
            contentPadding = PaddingValues(8.dp),
        ) {
            items(songs) { song ->
                SongItem(
                    song = song,
                    onClick = { onSongClick(song) }
                )
                if (song != songs.last()) {
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
