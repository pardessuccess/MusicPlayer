package com.pardess.musicplayer.presentation.main.component

import android.content.Intent
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pardess.musicplayer.R
import com.pardess.musicplayer.domain.model.SearchHistory
import com.pardess.musicplayer.presentation.common.component.AutoResizeText
import com.pardess.musicplayer.presentation.common.component.AutoSizeText
import com.pardess.musicplayer.presentation.common.component.FontSizeRange
import com.pardess.musicplayer.presentation.home.HomeUiEvent
import com.pardess.musicplayer.presentation.main.MainUiEvent
import com.pardess.musicplayer.presentation.main.MainUiState
import com.pardess.musicplayer.presentation.playlist.dialog.SpeechStatus
import com.pardess.musicplayer.presentation.playlist.dialog.setSpeechRecognizer
import com.pardess.musicplayer.ui.theme.BackgroundColor
import com.pardess.musicplayer.ui.theme.PointColor
import com.pardess.musicplayer.ui.theme.TextColor


@Composable
fun SearchBoxSection(
    uiState: MainUiState,
    modifier: Modifier,
    lazyListState: LazyListState,
    searchHistories: List<SearchHistory>,
    expand: Boolean,
    onEvent: (MainUiEvent) -> Unit,
    onHomeUiEvent: (HomeUiEvent) -> Unit,
) {
    var searchText by remember { mutableStateOf("") }

    val context = LocalContext.current
    val speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR")
    }

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }
    var isEditing by remember { mutableStateOf(false) }

    LaunchedEffect(expand) {
        if (expand) {
            onEvent(MainUiEvent.DismissGuideText)
        } else {
            isEditing = false
        }
    }

    setSpeechRecognizer(
        speechRecognizer = speechRecognizer,
        setSpeechStatusMsg = { onEvent(MainUiEvent.RecordingMessage(it)) },
        setSpeechResult = {
            searchText = it
            onEvent(MainUiEvent.Search(it))
        },
        setSpeechStatus = {
            when (it) {
                SpeechStatus.READY, SpeechStatus.IN_PROGRESS -> {
                    onEvent(MainUiEvent.StartRecording)
                }

                SpeechStatus.COMPLETE, SpeechStatus.ERROR -> {
                    onEvent(MainUiEvent.StopRecording)
                }
            }
        }
    )

    val screenHeight = LocalConfiguration.current.screenHeightDp.dp

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

    val searchBarOffset by remember {
        derivedStateOf {
            val offset = (lazyListState.firstVisibleItemScrollOffset / 2f).dp
            -offset.coerceAtMost(150.dp)
        }
    }

    // 애니메이션 적용
    val animatedSearchBarOffset by animateDpAsState(
        targetValue = searchBarOffset,
        animationSpec = tween(durationMillis = 400)
    )
    val infiniteTransition = rememberInfiniteTransition()

    val radius by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 600f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000),
            repeatMode = RepeatMode.Restart
        ), label = ""
    )

    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000),
            repeatMode = RepeatMode.Restart
        ), label = ""
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .offset(y = animatedSearchBarOffset)
            .padding(paddingSize)
            .height(searchBarHeight)
            .background(
                shape = RoundedCornerShape(roundedCornerSize),
                color = BackgroundColor
            ),
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
                        onEvent(MainUiEvent.SearchBoxShrink)
                        onHomeUiEvent(HomeUiEvent.SearchBoxShrink)
                    } else {
                        speechRecognizer.startListening(intent)
                        onEvent(MainUiEvent.SearchBoxExpand)
                        onHomeUiEvent(HomeUiEvent.SearchBoxExpand)
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
                            onEvent(MainUiEvent.Search(searchText))
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
                            onEvent(MainUiEvent.SearchBoxExpand)
                            onHomeUiEvent(HomeUiEvent.SearchBoxExpand)
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
            if (uiState.showGuideText) {
                AutoSizeText(
                    text = "눌러서 검색하세요",
                    modifier = Modifier.align(Alignment.CenterStart).padding(start = 16.dp, end = 50.dp),
                )
            }
        }
        if (expand) {
            Box(
                modifier = Modifier.weight(1f)
            ) {
                SearchHistorySection(
                    modifier = Modifier,
                    onEvent = onEvent,
                    searchHistories = searchHistories,
                    onHomeUiEvent = onHomeUiEvent,
                )
                if (uiState.isRecording) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 50.dp)
                    ) {
                        Icon(
                            tint = TextColor,
                            painter = painterResource(id = R.drawable.ic_mic),
                            contentDescription = "mic",
                            modifier = Modifier
                                .size(200.dp)
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(100.dp))
                                .background(
                                    shape = RoundedCornerShape(150.dp),
                                    color = Color.Red
                                )
                        )
                        Canvas(
                            modifier = Modifier
                                .size(200.dp)
                                .aspectRatio(1f)
                        ) {
                            drawCircle(
                                color = Color.Black.copy(alpha = alpha), // 투명도 적용
                                radius = radius // 애니메이션된 반지름 적용
                            )
                        }
                    }
                }
            }
        }
    }
    LaunchedEffect(isEditing) {
        if (isEditing) {
            focusRequester.requestFocus() // 포커스 부여
            keyboardController?.show() // 키보드 표시
        }
    }
}