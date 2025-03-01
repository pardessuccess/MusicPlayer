package com.pardess.musicplayer.presentation.main.search

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pardess.musicplayer.R
import com.pardess.musicplayer.domain.model.Album
import com.pardess.musicplayer.domain.model.Artist
import com.pardess.musicplayer.presentation.component.MusicImage
import com.pardess.musicplayer.presentation.component.SongItem
import com.pardess.musicplayer.ui.theme.BackgroundColor
import com.pardess.musicplayer.ui.theme.PointColor
import com.pardess.musicplayer.ui.theme.TextColor

@Composable
fun SearchScreen(
    onNavigateToRoute: (String) -> Unit,
    upPress: () -> Unit,
    uiState: State<SearchUiState>,
    onEvent: (SearchEvent) -> Unit,
) {

    val searchResult = uiState.value.searchResult
    println("@@@@$searchResult")
    var searchText by remember { mutableStateOf("") }
    LaunchedEffect(
        uiState.value.searchQuery
    ) {
        searchText = uiState.value.searchQuery
    }

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {
        if (searchResult.songs.isEmpty() && searchResult.artists.isEmpty() && searchResult.albums.isEmpty()) {
            Text(
                text = "\"${uiState.value.searchQuery}\"에 대한 검색 결과가 없습니다.",
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = 20.dp),
                fontSize = 30.sp,
                lineHeight = 40.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
        Column {
            Spacer(modifier = Modifier.height(16.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .padding(horizontal = 16.dp)
                    .background(
                        shape = RoundedCornerShape(20.dp),
                        color = PointColor
                    )
                    .clip(shape = RoundedCornerShape(20.dp))
                    .clickable {

                    },
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    modifier = Modifier,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    BasicTextField(
                        value = searchText,
                        onValueChange = {
                            searchText = it
                        },
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 16.dp)
                            .focusRequester(focusRequester),
                        textStyle = TextStyle(
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextColor
                        ),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Search
                        ),
                        keyboardActions = KeyboardActions(
                            onSearch = {
                                keyboardController?.hide()
                                onEvent(SearchEvent.Search(searchText))
                            }
                        )
                    )
                    if (searchText.isNotEmpty()) {
                        IconButton(
                            modifier = Modifier.size(60.dp),
                            onClick = {
                                searchText = ""
                            }
                        ) {
                            Icon(
                                modifier = Modifier.size(40.dp),
                                painter = painterResource(id = R.drawable.ic_close),
                                contentDescription = "ic_close",
                                tint = TextColor
                            )
                        }
                    } else {
                        IconButton(
                            modifier = Modifier.size(60.dp),
                            onClick = {
                                keyboardController?.show()
                                focusRequester.requestFocus()
                            }
                        ) {
                            Icon(
                                modifier = Modifier.size(40.dp),
                                painter = painterResource(id = R.drawable.ic_keyboard),
                                contentDescription = "edit",
                                tint = TextColor
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(8.dp),
            ) {
                if (searchResult.songs.isNotEmpty()) {
                    item {
                        Text(
                            text = "노래 (${searchResult.songs.size})",
                            fontSize = 35.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    items(searchResult.songs) { song ->
                        SongItem(song = song, onClick = {
                            onEvent(SearchEvent.SelectSong(song))
                        })
                        if (song != searchResult.songs.last()) {
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
                if (searchResult.artists.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "가수 (${searchResult.artists.size})",
                            fontSize = 35.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    items(searchResult.artists) { artist ->
                        ArtistItem(artist = artist, onClick = {
                            onEvent(SearchEvent.SelectArtist(artist))
                        })
                        if (artist != searchResult.artists.last()) {
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
                if (searchResult.albums.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "앨범 (${searchResult.albums.size})",
                            fontSize = 35.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    items(searchResult.albums) { album ->
                        AlbumItem(album = album, onClick = {
                            onEvent(SearchEvent.SelectAlbum(album))
                        })
                        if (searchResult.albums.last() != album) {
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
            Spacer(
                modifier = Modifier.height(
                    WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
                )
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ArtistItem(
    artist: Artist,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
            )
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        MusicImage(
            artist.songs.first().data,
            Modifier
                .padding(end = 8.dp)
                .size(80.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Column(
            modifier = Modifier.padding(end = 8.dp)
        ) {
            Text(text = artist.name, fontWeight = FontWeight.Bold, maxLines = 1)
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AlbumItem(
    album: Album,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
            )
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        MusicImage(
            album.songs.first().data,
            Modifier
                .padding(end = 8.dp)
                .size(80.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Column(
            modifier = Modifier.padding(end = 8.dp)
        ) {
            Text(text = album.title, fontWeight = FontWeight.Bold, maxLines = 1)
            Text(text = album.artistName, fontWeight = FontWeight.Bold, maxLines = 1)
        }
    }
}

