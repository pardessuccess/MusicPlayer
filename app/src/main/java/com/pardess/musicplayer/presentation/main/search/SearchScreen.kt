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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.pardess.musicplayer.R
import com.pardess.musicplayer.domain.model.Album
import com.pardess.musicplayer.domain.model.Artist
import com.pardess.musicplayer.domain.model.Song
import com.pardess.musicplayer.presentation.Status
import com.pardess.musicplayer.presentation.base.BaseScreen
import com.pardess.musicplayer.presentation.common.component.ErrorView
import com.pardess.musicplayer.presentation.common.component.LoadingView
import com.pardess.musicplayer.presentation.common.component.MusicImage
import com.pardess.musicplayer.presentation.common.component.SongItem
import com.pardess.musicplayer.presentation.playback.PlaybackEvent
import com.pardess.musicplayer.ui.theme.BackgroundColor
import com.pardess.musicplayer.ui.theme.PointColor
import com.pardess.musicplayer.ui.theme.TextColor


@Composable
fun SearchScreen(
    onNavigateToRoute: (String) -> Unit,
    onPlaybackEvent: (PlaybackEvent) -> Unit,
    allSongs: List<Song>,
) {
    val viewModel = hiltViewModel<SearchViewModel>()
    if (allSongs.isNotEmpty()) {
        viewModel.setAllSongs(allSongs)
    }

    BaseScreen(
        viewModel = viewModel,
        onEffect = { effect ->
            when (effect) {
                is SearchUiEffect.Navigate -> onNavigateToRoute(effect.route)
            }
        }
    ) { uiState, onEvent ->
        SearchScreen(
            uiState = uiState,
            onEvent = onEvent,
            onPlaybackEvent = onPlaybackEvent
        )
    }
}

@Composable
private fun SearchScreen(
    uiState: SearchUiState,
    onEvent: (SearchUiEvent) -> Unit,
    onPlaybackEvent: (PlaybackEvent) -> Unit,
) {

    var searchText by remember { mutableStateOf("") }
    LaunchedEffect(
        uiState.searchQuery
    ) {
        searchText = uiState.searchQuery
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {
        if (uiState.searchResult is Status.Success && uiState.searchResult.data.songs.isEmpty() && uiState.searchResult.data.artists.isEmpty() && uiState.searchResult.data.albums.isEmpty()) {
            Text(
                text = "\"${uiState.searchQuery}\"에 대한 검색 결과가 없습니다.",
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = 20.dp),
                fontSize = 30.sp,
                lineHeight = 40.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        } else if (uiState.searchResult is Status.Success) {
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
                    SearchInputSection(
                        searchText = searchText,
                        setSearchText = { searchText = it },
                        onEvent = onEvent,
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                SearchResults(
                    searchResult = uiState.searchResult.data,
                    onEvent = onEvent,
                    onPlaybackEvent = onPlaybackEvent
                )
                Spacer(
                    modifier = Modifier.height(
                        WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
                    )
                )
            }
        } else if (uiState.searchResult is Status.Loading) {
            LoadingView()
        } else if (uiState.searchResult is Status.Error) {
            ErrorView(
                message = uiState.searchResult.message
            )
        }
    }
}


@Composable
fun SearchInputSection(
    searchText: String,
    setSearchText: (String) -> Unit,
    onEvent: (SearchUiEvent) -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }

    Row(
        modifier = Modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BasicTextField(
            value = searchText,
            onValueChange = {
                setSearchText(it)
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
                    onEvent(SearchUiEvent.Search(searchText))
                }
            )
        )
        if (searchText.isNotEmpty()) {
            IconButton(
                modifier = Modifier.size(60.dp),
                onClick = {
                    setSearchText("")
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

@Composable
fun SearchResults(
    searchResult: SearchResult,
    onEvent: (SearchUiEvent) -> Unit,
    onPlaybackEvent: (PlaybackEvent) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp)
    ) {
        if (searchResult.songs.isNotEmpty()) {
            item { SectionTitle(title = "노래 (${searchResult.songs.size})") }
            items(searchResult.songs) { song ->
                SongItem(song = song, onClick = {
                    onPlaybackEvent(
                        PlaybackEvent.PlaySong(
                            searchResult.songs.indexOf(song),
                            searchResult.songs
                        )
                    )
                    onEvent(SearchUiEvent.SelectSong(song))
                })
            }
        }
        if (searchResult.artists.isNotEmpty()) {
            item {
                Spacer(Modifier.height(16.dp))
                SectionTitle(title = "가수 (${searchResult.artists.size})")
            }
            items(searchResult.artists) { artist ->
                ArtistItem(
                    artist = artist,
                    onClick = { onEvent(SearchUiEvent.SelectArtist(artist)) })
            }
        }
        if (searchResult.albums.isNotEmpty()) {
            item {
                Spacer(Modifier.height(16.dp))
                SectionTitle(title = "앨범 (${searchResult.albums.size})")
            }
            items(searchResult.albums) { album ->
                AlbumItem(album = album, onClick = { onEvent(SearchUiEvent.SelectAlbum(album)) })
            }
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 35.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(start = 8.dp)
    )
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

//@Composable
//fun SearchScreen(
//    onNavigateToRoute: (String) -> Unit,
//    onPlaybackEvent: (PlaybackEvent) -> Unit,
//    allSongs: List<Song>,
//) {
//    val viewModel = hiltViewModel<SearchViewModel>()
//    if (allSongs.isNotEmpty()) {
//        viewModel.initSearch("", allSongs)
//    }
//    BaseScreen(
//        viewModel = viewModel,
//        onEffect = { effect ->
//            when (effect) {
//                is SearchUiEffect.Navigate -> onNavigateToRoute(effect.route)
//            }
//        }
//    ) { uiState, onEvent ->
//        SearchScreen(
//            uiState = uiState,
//            onEvent = onEvent,
//            onPlaybackEvent = onPlaybackEvent
//        )
//    }
//}