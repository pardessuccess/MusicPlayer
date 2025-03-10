package com.pardess.musicplayer.presentation.common.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pardess.musicplayer.ui.theme.PointColor

@Composable
fun FullWidthButton(
    text: String,
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .height(100.dp)
        .padding(horizontal = 10.dp),
    color: Color = PointColor,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Button(
        modifier = modifier,
        colors = if (enabled) ButtonDefaults.buttonColors(
            color
        ) else {
            ButtonDefaults.buttonColors(
                Color(0xFFBDBDBD),
            )
        },
        enabled = enabled,
        shape = RoundedCornerShape(25),
        onClick = {
            onClick()
        },
    ) {
        Text(
            text = text,
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}