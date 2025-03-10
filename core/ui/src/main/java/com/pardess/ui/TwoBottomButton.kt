package com.pardess.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.pardess.designsystem.Gray300
import com.pardess.designsystem.PointColor

@Composable
fun TwoBottomButton(
    modifier: Modifier = Modifier,
    text1: String,
    text2: String,
    onClick1: () -> Unit,
    onClick2: () -> Unit,
    color1: Color = PointColor,
    color2: Color = Gray300,
) {
    Row(
        modifier = modifier.fillMaxWidth()
    ) {
        Spacer(Modifier.width(10.dp))
        FullWidthButton(
            modifier = Modifier
                .weight(1f)
                .height(100.dp),
            text = text1,
            color = color1,
            onClick = {
                onClick1()
            }
        )
        Spacer(Modifier.width(10.dp))
        FullWidthButton(
            modifier = Modifier
                .weight(1f)
                .height(100.dp),
            text = text2,
            color = color2,
            onClick = {
                onClick2()
            }
        )
        Spacer(Modifier.width(10.dp))
    }
}