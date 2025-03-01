package com.pardess.musicplayer.presentation.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.pardess.musicplayer.R


@Composable
fun EmptyImage(
    modifier: Modifier = Modifier,
    type: String,
) {

    Image(
        modifier = modifier.background(Color.Gray),
        contentScale = ContentScale.Crop,
        painter = painterResource(
            id = when (type) {
                "song" -> R.drawable.default_audio_art
                "album" -> R.drawable.default_album_art
                "artist" -> R.drawable.default_artist_art
                else -> R.drawable.default_audio_art
            }
        ),
        contentDescription = "Empty Song Image",
    )
}

