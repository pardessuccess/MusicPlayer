package com.pardess.musicplayer.presentation.main

import android.Manifest
import android.content.Intent
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.Space
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.pardess.musicplayer.R
import com.pardess.musicplayer.data.entity.SongEntity
import com.pardess.musicplayer.data.entity.join.FavoriteSong
import com.pardess.musicplayer.domain.model.Album
import com.pardess.musicplayer.domain.model.Artist
import com.pardess.musicplayer.domain.model.SearchHistory
import com.pardess.musicplayer.domain.model.SearchType
import com.pardess.musicplayer.domain.model.Song
import com.pardess.musicplayer.presentation.component.AutoResizeText
import com.pardess.musicplayer.presentation.component.FontSizeRange
import com.pardess.musicplayer.presentation.component.MusicImage
import com.pardess.musicplayer.presentation.main.component.PopularSection
import com.pardess.musicplayer.presentation.main.component.SelectButtonSection
import com.pardess.musicplayer.presentation.navigation.Screen
import com.pardess.musicplayer.presentation.playback.PlaybackEvent
import com.pardess.musicplayer.presentation.playlist.dialog.setSpeechRecognizer
import com.pardess.musicplayer.presentation.main.searchbox.SearchBoxEvent
import com.pardess.musicplayer.presentation.main.searchbox.SearchBoxState
import com.pardess.musicplayer.presentation.toSong
import com.pardess.musicplayer.ui.theme.BackgroundColor
import com.pardess.musicplayer.ui.theme.NavigationBarHeight
import com.pardess.musicplayer.ui.theme.PointColor
import com.pardess.musicplayer.ui.theme.PointColor2
import com.pardess.musicplayer.ui.theme.TextColor

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MainScreen(
    searchBoxState: SearchBoxState,
    onEvent: (MainUiEvent) -> Unit,
    uiState: MainUiState,
    onPlaybackEvent: (PlaybackEvent) -> Unit,
    onSearchEvent: (SearchBoxEvent) -> Unit,
) {

    val song1st = uiState.song1st
    val song2nd = uiState.song2nd
    val song3rd = uiState.song3rd
    val popularArtists = uiState.popularArtists
    val popularAlbums = uiState.popularAlbums
    val searchHistories = uiState.searchHistories
    val favoriteSongs = uiState.favoriteSongs

    val context = LocalContext.current
    var searchText by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }
    var isEditing by remember { mutableStateOf(false) }

    val speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)

    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR")
    }

    val expand = searchBoxState.expand

    var showGuideText by remember { mutableStateOf(true) }

    LaunchedEffect(expand) {
        if (expand) {
            showGuideText = false
        } else {
            isEditing = false
        }
    }

    var hasRecordPermission by remember { mutableStateOf(false) }

    val recordPermissionState = rememberPermissionState(Manifest.permission.RECORD_AUDIO)
    hasRecordPermission = recordPermissionState.status.isGranted

    var speechStatusMsg by remember { mutableStateOf("버튼을 누르고 말해 주세요.") }


    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp - 24.dp

    val bigIconSize = screenWidth * 2 / 3
    val smallIconSize = (screenWidth - 36.dp) / 3

    val searchBarHeight by animateDpAsState(
        targetValue = if (expand) screenHeight else 100.dp,
        animationSpec = tween(400), label = "Search Bar Height"
    )

    val paddingSize by animateDpAsState(
        targetValue = if (expand) 0.dp else 16.dp,
        animationSpec = tween(400), label = "padding size"
    )

    val innerPaddingSize by animateDpAsState(
        targetValue = if (expand) 16.dp else 0.dp,
        animationSpec = tween(400), label = "padding size"
    )

    val roundedCornerSize by animateDpAsState(
        targetValue = if (expand) 0.dp else 20.dp,
        animationSpec = tween(400), label = "rounded corner size"
    )

    val lazyListState = rememberLazyListState()

    val searchBarOffset by remember {
        derivedStateOf {
            // 스크롤 오프셋의 변화에 따라 이동 비율을 조정할 수 있습니다.
            // 예시에서는 최대 100.dp 만큼 위로 이동하도록 했습니다.
            val offset = (lazyListState.firstVisibleItemScrollOffset / 2f).dp
            // clamp해서 최대 offset을 100.dp로 제한
            -offset.coerceAtMost(150.dp)
        }
    }

    // 애니메이션 적용
    val animatedSearchBarOffset by animateDpAsState(
        targetValue = searchBarOffset,
        animationSpec = tween(durationMillis = 400)
    )

    LaunchedEffect(speechStatusMsg) {
//        Toast.makeText(context, speechStatusMsg, Toast.LENGTH_SHORT).show()
    }

    setSpeechRecognizer(
        speechRecognizer = speechRecognizer,
        setSpeechStatusMsg = { speechStatusMsg = it },
        setSpeechResult = { searchText = it },
        setSpeechStatus = {

        }
    )


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = BackgroundColor)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(color = BackgroundColor),
            state = lazyListState,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Spacer(modifier = Modifier.height(132.dp))
                Top3SongsSection(
                    onPlaybackEvent = onPlaybackEvent,
                    song1st = song1st,
                    song2nd = song2nd,
                    song3rd = song3rd,
                    favoriteSongs = favoriteSongs,
                    bigIconSize = bigIconSize,
                    smallIconSize = smallIconSize
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                ) {
                    SelectButtonSection(
                        onEvent = onEvent,
                        onPlaybackEvent = onPlaybackEvent
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    PopularSection(
                        onEvent = onEvent,
                        artists = popularArtists,
                        albums = popularAlbums
                    )
                }
            }

            item {
                Spacer(
                    modifier = Modifier.height(
                        NavigationBarHeight + WindowInsets.navigationBars.asPaddingValues()
                            .calculateBottomPadding()
                    )
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = animatedSearchBarOffset)
                .padding(paddingSize)
                .height(searchBarHeight)
                .background(
                    shape = RoundedCornerShape(roundedCornerSize),
                    color = BackgroundColor
                )
                .align(Alignment.TopCenter),
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(innerPaddingSize))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .padding(horizontal = innerPaddingSize)
                    .background(
                        shape = RoundedCornerShape(20.dp),
                        color = PointColor
                    )
                    .clip(shape = RoundedCornerShape(20.dp))
                    .clickable {
                        if (expand) {
                            onSearchEvent(SearchBoxEvent.Shrink)
                        } else {
                            onSearchEvent(SearchBoxEvent.Expand)
                        }
                    },
            ) {
                Row(
                    modifier = Modifier.align(Alignment.CenterStart),
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
                        enabled = isEditing,
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
//                                onEvent(MainUiEvent.Search(searchText))
                                onSearchEvent(SearchBoxEvent.Search(searchText))
//                                onSearchEvent(SearchBoxEvent.Shrink)
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
                                tint = TextColor,
                                modifier = Modifier.size(50.dp),
                                painter = painterResource(id = R.drawable.ic_close),
                                contentDescription = "ic_close",
                            )
                        }
                    } else {
                        IconButton(
                            modifier = Modifier.size(60.dp),
                            onClick = {
                                onSearchEvent(SearchBoxEvent.Expand)
                                isEditing = true
                            }
                        ) {
                            Icon(
                                tint = TextColor,
                                modifier = Modifier.size(40.dp),
                                painter = painterResource(id = R.drawable.ic_keyboard),
                                contentDescription = "edit",
                            )
                        }
                    }
                }
                if (showGuideText) {
                    Text(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(start = 16.dp),
                        text = "눌러서 검색하세요",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextColor
                    )
                }
            }
            if (expand) {
                SearchHistorySection(
                    modifier = Modifier.weight(1f),
                    onEvent = onEvent,
                    searchHistories = searchHistories,
                    onSearchEvent = onSearchEvent,
                )
            }
        }
        LaunchedEffect(isEditing) {
            if (isEditing) {
                focusRequester.requestFocus() // 포커스 부여
                keyboardController?.show() // 키보드 표시
            }
        }
    }
}

@Composable
fun Top3SongsSection(
    modifier: Modifier = Modifier,
    onPlaybackEvent: (PlaybackEvent) -> Unit,
    song1st: SongEntity?,
    song2nd: SongEntity?,
    song3rd: SongEntity?,
    favoriteSongs: List<FavoriteSong>,
    bigIconSize: Dp,
    smallIconSize: Dp,
) {
    Row(
        modifier = Modifier.wrapContentHeight(),
        horizontalArrangement = Arrangement.Center
    ) {
        Card(
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 6.dp)
        ) {
            MusicImage(
                filePath = song1st?.data ?: "",
                modifier = Modifier
                    .size(bigIconSize)
                    .aspectRatio(1f)
                    .clip(shape = RoundedCornerShape(12.dp))
                    .clickable {
                        if (song1st != null) {
                            onPlaybackEvent(
                                PlaybackEvent.PlaySong(
                                    0,
                                    favoriteSongs.map { it.song.toSong() })
                            )
                        }
                    },
                type = "album"
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(
            modifier = Modifier,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(6.dp))
            Card(
                elevation =
                CardDefaults.elevatedCardElevation(defaultElevation = 6.dp)
            ) {
                MusicImage(
                    filePath = song2nd?.data ?: "",
                    modifier = Modifier
                        .size(smallIconSize)
                        .aspectRatio(1f)
                        .clip(shape = RoundedCornerShape(12.dp))
                        .clickable {
                            if (song2nd != null) {
                                onPlaybackEvent(
                                    PlaybackEvent.PlaySong(
                                        1,
                                        favoriteSongs.map { it.song.toSong() })
                                )
                            }
                        },
                    type = "song"
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                elevation =
                CardDefaults.elevatedCardElevation(defaultElevation = 6.dp)
            ) {
                MusicImage(
                    filePath = song3rd?.data ?: "",
                    modifier = Modifier
                        .size(smallIconSize)
                        .aspectRatio(1f)
                        .clip(shape = RoundedCornerShape(12.dp))
                        .clickable {
                            if (song3rd != null)
                                onPlaybackEvent(
                                    PlaybackEvent.PlaySong(
                                        2,
                                        favoriteSongs.map { it.song.toSong() })
                                )
                        },
                    type = "artist"
                )
            }
        }
    }

}


@Composable
fun SearchHistorySection(
    modifier: Modifier = Modifier,
    onEvent: (MainUiEvent) -> Unit,
    searchHistories: List<SearchHistory>,
    onSearchEvent: (SearchBoxEvent) -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .background(
                color = BackgroundColor
            ),
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        LazyColumn(
            Modifier.fillMaxSize()
        ) {
            items(searchHistories) { history ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .clickable {
                            onSearchEvent(SearchBoxEvent.Search(history.text))
                        }
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    when (history.type) {
                        SearchType.TEXT -> {
                            Text(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(vertical = 8.dp, horizontal = 8.dp),
                                text = history.text,
                                fontSize = 28.sp,
                                lineHeight = 30.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                                maxLines = 2,
                            )
                        }

                        else -> {
                            MusicImage(
                                filePath = history.image ?: "",
                                modifier = Modifier
                                    .padding(end = 8.dp)
                                    .size(80.dp),
                                type = history.type.text
                            )
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                            ) {
                                Text(
                                    modifier = Modifier.align(Alignment.Center),
                                    text = history.text,
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold,
                                    lineHeight = 30.sp,
                                    color = Color.Black,
                                    maxLines = 2,
                                )
                            }
                        }
                    }

                    IconButton(
                        modifier = Modifier.size(45.dp),
                        onClick = {
                            onEvent(MainUiEvent.RemoveSearchHistory(history.id))
                        }
                    ) {
                        Icon(
                            modifier = Modifier.size(45.dp),
                            painter = painterResource(id = R.drawable.ic_close),
                            contentDescription = "ic_close",
                        )
                    }
                }
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