package com.pardess.musicplayer.presentation.main.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.pardess.musicplayer.presentation.component.TwoBottomButton

@Composable
fun RemoveHistoryDialog(
    modifier: Modifier = Modifier,
    onEvent: (HistoryUiEvent) -> Unit,
) {
    Dialog(
        onDismissRequest = { onEvent(HistoryUiEvent.DismissRemoveDialog) },
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(20.dp)
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(25.dp))
            Text(
                text = "기록을 지울까요?",
                fontSize = 35.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(15.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TwoBottomButton(
                    text1 = "취소",
                    text2 = "삭제",
                    onClick1 = { onEvent(HistoryUiEvent.DismissRemoveDialog) },
                    onClick2 = { onEvent(HistoryUiEvent.RemoveHistory) }
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}