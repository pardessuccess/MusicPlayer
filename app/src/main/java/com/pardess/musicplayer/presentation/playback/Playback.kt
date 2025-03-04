package com.pardess.musicplayer.presentation.playback

import androidx.activity.compose.BackHandler
import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pardess.musicplayer.R
import com.pardess.musicplayer.domain.model.Song
import com.pardess.musicplayer.presentation.component.CustomSlider
import com.pardess.musicplayer.presentation.component.CustomSliderDefaults
import com.pardess.musicplayer.presentation.component.FullWidthButton
import com.pardess.musicplayer.presentation.component.MusicImage
import com.pardess.musicplayer.presentation.component.progress
import com.pardess.musicplayer.presentation.component.track
import com.pardess.musicplayer.ui.theme.BackgroundColor
import com.pardess.musicplayer.ui.theme.PointBackgroundColor
import com.pardess.musicplayer.utils.Utils.toTime
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun Playback(
    playbackUiState: PlaybackUiState,
    onPlaybackUiEvent: (PlaybackEvent) -> Unit,
    onEvent: (PlaybackEvent) -> Unit,
) {
    if (playbackUiState.playerState.currentSong == null) return // 🎵 현재 재생 중인 노래가 없으면 UI 숨김

    var offsetX by remember { mutableFloatStateOf(0f) }

    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val statusBarHeight = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val navigationBarHeight = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    val barHeight by animateDpAsState(
        targetValue = if (playbackUiState.expand) screenHeight + statusBarHeight + navigationBarHeight else 116.dp,
        animationSpec = tween(400), label = "Playback Bar Height"
    )

    var iconResId by remember { mutableIntStateOf(R.drawable.ic_round_pause) }

    val currentSong = playbackUiState.playerState.currentSong
    val currentTime = playbackUiState.playerState.currentPosition.toMillis()
    val totalTime = currentSong.duration.toMillis()
    val shuffleMode = playbackUiState.playerState.shuffle
    val repeatMode = playbackUiState.playerState.repeatMode
    val isPlaying = playbackUiState.playerState.isPlaying

    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            iconResId = R.drawable.ic_round_pause
        } else {
            delay(100)
            iconResId = R.drawable.ic_round_play_arrow
        }
    }

    BackHandler(enabled = playbackUiState.expand) {
        if (playbackUiState.expand) {
            onPlaybackUiEvent(PlaybackEvent.ExpandPanel)
        }
    }

    val backgroundColor by animateColorAsState(
        targetValue = if (playbackUiState.expand) BackgroundColor else PointBackgroundColor,
        animationSpec = tween(durationMillis = 400)
    )

    AnimatedVisibility(
        visible = true,
        enter = expandIn(expandFrom = Alignment.BottomStart),
        exit = fadeOut(animationSpec = tween(200))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragEnd = {
                            if (barHeight == 116.dp) {
                                when {
                                    offsetX > 0 -> onPlaybackUiEvent(PlaybackEvent.SkipToPreviousSong)
                                    offsetX < 0 -> onPlaybackUiEvent(PlaybackEvent.SkipToNextSong)
                                }
                            }
                        },
                        onDrag = { change, dragAmount ->
                            if (barHeight == 116.dp) {
                                change.consume()
                                offsetX = dragAmount.x
                            }
                        }
                    )
                }
                .background(backgroundColor)
                .height(barHeight),
            contentAlignment = Alignment.BottomCenter
        ) {
            HomeBottomBarItem(
                onEvent = onEvent,
                barHeight = barHeight,
                expand = barHeight > screenHeight / 2,
                song = currentSong,
                currentTime = currentTime,
                totalTime = totalTime,
                iconResId = iconResId,
                shuffleMode = shuffleMode,
                repeatMode = repeatMode,
                isPlaying = isPlaying
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeBottomBarItem(
    onEvent: (PlaybackEvent) -> Unit,
    barHeight: Dp,
    expand: Boolean,
    song: Song,
    currentTime: Long,
    @DrawableRes iconResId: Int,
    totalTime: Long,
    shuffleMode: Boolean,
    repeatMode: Int,
    isPlaying: Boolean
) {

    val repeatModes = listOf("안함", "한곡", "반복")
    val shuffleModes = listOf("안함", "랜덤")

    val horizontalPadding by animateDpAsState(
        targetValue = if (expand) {
            15.dp
        } else {
            4.dp
        },
        animationSpec = tween(400), label = ""
    )

    val topPadding by animateDpAsState(
        targetValue = if (expand) {
            20.dp
        } else {
            4.dp
        },
        animationSpec = tween(400), label = ""
    )

    Column(
        modifier = Modifier
            .height(barHeight)
            .drawBehind {
                val strokeWidth = if (!expand) 4.dp.toPx() else 0.0.dp.toPx()
                drawLine(
                    color = PointBackgroundColor,
                    start = Offset(0f, size.height - strokeWidth / 2), // 약간 중앙 정렬
                    end = Offset(size.width, size.height - strokeWidth / 2),
                    strokeWidth = strokeWidth
                )
            }
            .clickable(onClick = { onEvent(PlaybackEvent.ExpandPanel) }),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = if (!expand) Arrangement.SpaceBetween else Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier.background(PointBackgroundColor)
            ) {
                MusicImage(
                    filePath = song.data,
                    modifier = Modifier
                        .background(if (expand) Color.White else PointBackgroundColor)
                        .padding(
                            start = horizontalPadding,
                            top = topPadding,
                            end = horizontalPadding,
                            bottom = 4.dp
                        )
                        .aspectRatio(1f),
                )
            }
            if (!expand) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(vertical = 8.dp, horizontal = 4.dp),
                ) {
                    Text(
                        modifier = Modifier.basicMarquee(iterations = 3),
                        text = song.title,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        maxLines = 1,
                    )

                    Text(
                        song.artistName,
                        maxLines = 1,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.basicMarquee(iterations = 3),
                    )
                }
                Icon(
                    tint = Color.Black,
                    painter = painterResource(iconResId),
                    contentDescription = "Play",
                    modifier = Modifier
                        .size(70.dp)
                        .padding(end = 8.dp)
                        .clip(CircleShape)
                        .clickable(onClick = {
                            if (isPlaying) {
                                onEvent(PlaybackEvent.PauseSong)
                            } else {
                                onEvent(PlaybackEvent.ResumeSong)
                            }
                        }),
                )
            }
        }
        AnimatedVisibility(
            visible = expand,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column {
                    Spacer(modifier = Modifier.height(20.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                modifier = Modifier
                                    .basicMarquee(1),
                                text = song.title,
                                maxLines = 1,
                                fontWeight = FontWeight.Bold,
                                fontSize = 40.sp
                            )

                            Text(
                                song.artistName,
                                maxLines = 1,
                                fontWeight = FontWeight.Bold,
                                fontSize = 30.sp,
                                modifier = Modifier
                                    .basicMarquee(1)
                                    .graphicsLayer {
                                        alpha = 0.60f
                                    })
                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp, horizontal = 12.dp)
                    ) {
                        CustomSlider(
                            modifier = Modifier.fillMaxWidth(),
                            value = currentTime.toFloat(),
                            onValueChange = {
                                onEvent(PlaybackEvent.SeekSongToPosition(it.toLong()))
                            },
                            valueRange = 0f..totalTime.toFloat(),
                            thumb = { thumbValue ->
                                CustomSliderDefaults.Thumb(
                                    thumbValue = "",
                                    color = Color.Transparent,
                                    size = 32.dp,
                                    modifier = Modifier.background(
                                        color = Color.Black,
                                        shape = CircleShape
                                    )
                                )
                            },
                            track = { sliderState ->
                                Box(
                                    modifier = Modifier
                                        .track()
                                        .border(
                                            width = 1.dp,
                                            color = Color.LightGray.copy(alpha = 0.4f),
                                            shape = CircleShape
                                        )
                                        .background(Color.LightGray.copy(alpha = 0.3f)),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .progress(sliderState = sliderState)
                                            .background(
                                                Color.Black
                                            )
                                    )
                                }
                            }
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 15.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                currentTime.toTime(),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                totalTime.toTime(),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_skip_previous),
                        contentDescription = "Skip Previous",
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable(onClick = {
                                onEvent(PlaybackEvent.SkipToPreviousSong)
                            })
                            .padding(start = 12.dp)
                            .weight(1f)
                            .aspectRatio(1f)
                    )
                    Icon(
                        painter = painterResource(iconResId),
                        contentDescription = "Play",
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable(onClick = {
                                if (isPlaying) {
                                    onEvent(PlaybackEvent.PauseSong)
                                } else {
                                    onEvent(PlaybackEvent.ResumeSong)
                                }
                            })
                            .size(140.dp),
                        tint = Color.Black
                    )
                    Icon(
                        painter = painterResource(R.drawable.ic_skip_next),
                        contentDescription = "Skip Next",
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable(onClick = {
                                onEvent(PlaybackEvent.SkipToNextSong)
                            })
                            .padding(end = 12.dp)
                            .weight(1f)
                            .aspectRatio(1f)
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Spacer(modifier = Modifier.width(10.dp))
                    FullWidthButton(
                        modifier = Modifier
                            .weight(1f)
                            .height(100.dp),
                        text = if (shuffleMode) shuffleModes[1] else shuffleModes[0],
                        onClick = { onEvent(PlaybackEvent.ShuffleMode(!shuffleMode)) }
                    )

                    IconButton(
                        modifier = Modifier
                            .size(90.dp),
                        onClick = {
                            onEvent(PlaybackEvent.Favorite)
                        },
                    ) {
                        Icon(
                            modifier = Modifier.size(90.dp),
                            painter = painterResource(R.drawable.ic_favorite),
                            contentDescription = "Close",
                            tint = Color.Red
                        )
                    }

                    FullWidthButton(
                        modifier = Modifier
                            .weight(1f)
                            .height(100.dp),
                        text = repeatModes[repeatMode],
                        onClick = {
                            onEvent(
                                PlaybackEvent.RepeatMode(
                                    when (repeatMode) {
                                        0 -> 1
                                        1 -> 2
                                        else -> 0
                                    }
                                )
                            )
                        }
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                }
            }
        }
    }
}