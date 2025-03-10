package com.pardess.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pardess.designsystem.BackgroundColor
import com.pardess.designsystem.PointColor
import com.pardess.designsystem.TextColor
import com.pardess.home.MainUiEvent
import com.pardess.navigation.Screen
import com.pardess.playback.PlaybackEvent
import com.pardess.ui.AutoSizeText


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
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 5.dp),
                onClick = {
                    onEvent(MainUiEvent.Navigate(Screen.Favorite.route))
                }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    AutoSizeText(
                        text = "좋아요",
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
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 5.dp),
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
                    AutoSizeText(text = "무작위")
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
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 5.dp),
                onClick = {
                    onEvent(MainUiEvent.Navigate(Screen.History.route))
                }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    AutoSizeText(
                        text = "최근 기록",
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
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 5.dp),
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
                    AutoSizeText(
                        text = "자주 재생",
                        color = TextColor,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}