package com.pardess.musicplayer.presentation.main.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pardess.musicplayer.presentation.common.component.AutoResizeText
import com.pardess.musicplayer.presentation.common.component.AutoSizeText
import com.pardess.musicplayer.presentation.common.component.FontSizeRange
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
//
//@Composable
//fun TextAutoSizeLayoutScope.AutoText(
//    text: AnnotatedString,
//    constraints: Constraints,
//    fontSizes: Array<TextUnit>,
//) {
//    var lastTextSize: TextUnit = TextUnit.Unspecified
//    for (presetSize in fontSizes) {
//        val layoutResult = performLayout(constraints, text, presetSize)
//        if (
//            layoutResult.size.width <= constraints.maxWidth &&
//            layoutResult.size.height <= constraints.maxHeight
//        ) {
//            lastTextSize = presetSize
//        } else {
//            break
//        }
//    }
//    Text(
//        text = text.toString(),
//        fontSize = lastTextSize,
//    )
//}
//
//fun getFontSize(
//    constraints: Constraints,
//    fontSizes: Array<TextUnit>,
//) {
//    val density = LocalDensity.current.density
//    var lastTextSize: TextUnit = TextUnit.Unspecified
//    for (presetSize in fontSizes) {
//        val layoutResult = TextAutoSizeLayoutScope.performLayout(constraints, text, presetSize)
//        if (
//            layoutResult.size.width <= constraints.maxWidth &&
//            layoutResult.size.height <= constraints.maxHeight
//        ) {
//            lastTextSize = presetSize
//        } else {
//            break
//        }
//    }
//    return lastTextSize
//}
//
//data class PresetsTextAutoSize(private val presets: Array<TextUnit>) : TextAutoSize {
//    override fun TextAutoSizeLayoutScope.getFontSize(
//        constraints: Constraints,
//        text: AnnotatedString
//    ): TextUnit {
//        var lastTextSize: TextUnit = TextUnit.Unspecified
//        for (presetSize in presets) {
//            val layoutResult = performLayout(constraints, text, presetSize)
//            if (
//                layoutResult.size.width <= constraints.maxWidth &&
//                layoutResult.size.height <= constraints.maxHeight
//            ) {
//                lastTextSize = presetSize
//            } else {
//                break
//            }
//        }
//        return lastTextSize
//    }
//}
//

//BasicText(
//modifier = Modifier.fillMaxSize(),
//text = "자주 재생",
//autoSize = TextAutoSize.StepBased(
//minFontSize = 20.sp,
//maxFontSize = 50.sp,
//stepSize = 5.sp
//),
//maxLines = 1
//)