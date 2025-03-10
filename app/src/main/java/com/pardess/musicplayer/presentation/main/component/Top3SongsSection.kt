package com.pardess.musicplayer.presentation.main.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.pardess.musicplayer.data.entity.join.FavoriteSong
import com.pardess.musicplayer.presentation.common.component.MusicImage
import com.pardess.musicplayer.presentation.main.MainUiState
import com.pardess.musicplayer.presentation.playback.PlaybackEvent
import com.pardess.musicplayer.presentation.toSong


@Composable
fun Top3SongsSection(
    uiState: MainUiState,
    onPlaybackEvent: (PlaybackEvent) -> Unit,
    favoriteSongs: List<FavoriteSong>,
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp - 24.dp
    val bigIconSize = screenWidth * 2 / 3
    val smallIconSize = (screenWidth - 36.dp) / 3

    Row(
        modifier = Modifier.wrapContentHeight(),
        horizontalArrangement = Arrangement.Center
    ) {
        Card(
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 6.dp)
        ) {
            MusicImage(
                filePath = uiState.song1st?.data ?: "",
                modifier = Modifier
                    .size(bigIconSize)
                    .aspectRatio(1f)
                    .clip(shape = RoundedCornerShape(12.dp))
                    .clickable {
                        if (uiState.song1st != null) {
                            onPlaybackEvent(
                                PlaybackEvent.PlaySong(
                                    0,
                                    favoriteSongs.map { it.song.toSong() })
                            )
                            onPlaybackEvent(PlaybackEvent.RepeatMode(2))
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
                    filePath = uiState.song2nd?.data ?: "",
                    modifier = Modifier
                        .size(smallIconSize)
                        .aspectRatio(1f)
                        .clip(shape = RoundedCornerShape(12.dp))
                        .clickable {
                            if (uiState.song2nd != null) {
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
                    filePath = uiState.song3rd?.data ?: "",
                    modifier = Modifier
                        .size(smallIconSize)
                        .aspectRatio(1f)
                        .clip(shape = RoundedCornerShape(12.dp))
                        .clickable {
                            if (uiState.song3rd != null)
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
