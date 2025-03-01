package com.pardess.musicplayer.presentation.playlist.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.pardess.musicplayer.presentation.component.TwoBottomButton
import com.pardess.musicplayer.presentation.playlist.detail.DetailPlaylistUiEvent

@Composable
fun DeleteSongDialog(
    modifier: Modifier = Modifier,
    onEvent: (DetailPlaylistUiEvent) -> Unit,
) {
    Dialog(
        onDismissRequest = { onEvent(DetailPlaylistUiEvent.SetShowDeleteSongDialog(false)) }
    ) {
        Surface(
            modifier = modifier,
            shape = RoundedCornerShape(20.dp),
            color = Color.White
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Spacer(modifier = Modifier.height(10.dp))
                Text("음악 삭제", fontSize = 45.sp)
                Spacer(modifier = Modifier.height(10.dp))
                Text("정말 삭제하시겠습니까?", fontSize = 20.sp)
                Spacer(modifier = Modifier.height(10.dp))
                TwoBottomButton(
                    text1 = "확인",
                    text2 = "취소",
                    onClick1 = {
                        onEvent(DetailPlaylistUiEvent.DeleteSongFromPlaylist)
                        onEvent(DetailPlaylistUiEvent.SetShowDeleteSongDialog(false))
                    },
                    onClick2 = {
                        onEvent(DetailPlaylistUiEvent.SetShowDeleteSongDialog(false))
                    }
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}