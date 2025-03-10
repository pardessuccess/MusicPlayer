package com.pardess.musicplayer.presentation.common.component

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import com.pardess.musicplayer.utils.Utils.getImage

@Composable
fun MusicImage(
    filePath: String?, modifier: Modifier = Modifier,
    type: String = "song"
) {
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(filePath) {
        bitmap = filePath?.getImage()
    }

    bitmap?.let { image ->
        Box(
            modifier = modifier.wrapContentSize().background(Color.White)
        ) {
            Image(
                bitmap = image.asImageBitmap(),
//            contentScale = ContentScale.Crop,
                contentDescription = "음악 커버",
                modifier = modifier,
            )
        }
    } ?: EmptyImage(
        modifier = modifier,
        type = type
    )
}
