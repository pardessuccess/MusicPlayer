package com.pardess.musicplayer.presentation.main.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pardess.musicplayer.presentation.component.AutoResizeText
import com.pardess.musicplayer.presentation.component.FontSizeRange
import com.pardess.musicplayer.presentation.main.MainUiEvent
import com.pardess.musicplayer.presentation.navigation.Screen
import com.pardess.musicplayer.presentation.playback.PlaybackEvent
import com.pardess.musicplayer.ui.theme.BackgroundColor
import com.pardess.musicplayer.ui.theme.PointColor
import com.pardess.musicplayer.ui.theme.TextColor


@Composable
fun SelectButtonSection(
    onEvent: (MainUiEvent) -> Unit,
    onPlaybackEvent: (PlaybackEvent) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(BackgroundColor)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Card(
                modifier = Modifier
                    .weight(1f)
                    .height(100.dp),
                colors = CardDefaults.cardColors(
                    containerColor = PointColor
                ),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 3.dp),
                onClick = {
                    onEvent(MainUiEvent.Navigate(Screen.Favorite.route))
                }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    AutoResizeText(
                        text = "좋아요",
                        fontSizeRange = FontSizeRange(30.sp, 160.sp),
                        color = TextColor,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))
            Card(
                modifier = Modifier
                    .weight(1f)
                    .height(100.dp),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = PointColor
                ),
                onClick = {
                    onPlaybackEvent(PlaybackEvent.PlayRandom)
                }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    AutoResizeText(
                        text = "무작위",
                        fontSizeRange = FontSizeRange(30.sp, 160.sp),
                        color = TextColor,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Card(
                modifier = Modifier
                    .weight(1f)
                    .height(100.dp),
                colors = CardDefaults.cardColors(
                    containerColor = PointColor
                ),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 3.dp),
                onClick = {
                    onEvent(MainUiEvent.Navigate(Screen.History.route))
                }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    AutoResizeText(
                        text = "최근 기록",
                        fontSizeRange = FontSizeRange(30.sp, 160.sp),
                        color = TextColor,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Card(
                modifier = Modifier
                    .weight(1f)
                    .height(100.dp),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = PointColor
                ),
                onClick = {
                    onEvent(MainUiEvent.Navigate(Screen.PlayCount.route))
                }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    AutoResizeText(
                        text = "자주 재생",
                        fontSizeRange = FontSizeRange(30.sp, 160.sp),
                        color = TextColor,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}
