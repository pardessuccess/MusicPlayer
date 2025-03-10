package com.pardess.home.favorite

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import com.pardess.ui.TwoBottomButton

@Composable
fun RemoveFavoriteDialog(
    modifier: Modifier = Modifier,
    onEvent: (FavoriteUiEvent) -> Unit,
) {
    Dialog(
        onDismissRequest = { onEvent(FavoriteUiEvent.DismissRemoveDialog) },
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
                modifier = Modifier.padding(horizontal = 10.dp),
                text = "좋아요를 초기화 할까요?",
                fontSize = 35.sp,
                lineHeight = 35.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(15.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TwoBottomButton(
                    text1 = "삭제",
                    text2 = "취소",
                    onClick1 = { onEvent(FavoriteUiEvent.RemoveFavorite) },
                    onClick2 = { onEvent(FavoriteUiEvent.DismissRemoveDialog) }
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}