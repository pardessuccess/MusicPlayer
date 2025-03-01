package com.pardess.musicplayer.presentation.playlist.dialog

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.pardess.musicplayer.R
import com.pardess.musicplayer.presentation.component.FullWidthButton
import com.pardess.musicplayer.presentation.playlist.PlaylistUiEvent

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CreatePlaylistDialog(
    onEvent: (PlaylistUiEvent) -> Unit,
) {
    val context = LocalContext.current
    var name by remember { mutableStateOf("") }
    var isEditing by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }

    val speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)

    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR")
    }

    var hasRecordPermission by remember { mutableStateOf(false) }

    val recordPermissionState = rememberPermissionState(Manifest.permission.RECORD_AUDIO)
    hasRecordPermission = recordPermissionState.status.isGranted

    var speechStatusMsg by remember { mutableStateOf("버튼을 누르고 말해 주세요.") }

    LaunchedEffect(speechStatusMsg) {
        Toast.makeText(context, speechStatusMsg, Toast.LENGTH_SHORT).show()
    }

    setSpeechRecognizer(
        speechRecognizer = speechRecognizer,
        setSpeechStatusMsg = {
            speechStatusMsg = it
        },
        setSpeechResult = {
            name = it
        }
    )

    Dialog(
        onDismissRequest = { onEvent(PlaylistUiEvent.SetShowPlaylistDialog(false)) }
    ) {
        Surface(
            modifier = Modifier,
            shape = RoundedCornerShape(20.dp),
            color = Color.White
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Create Playlist",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .height(100.dp)
                            .background(
                                shape = RoundedCornerShape(20.dp),
                                color = Color(0xFFE1E1EB)
                            )
                            .clip(shape = RoundedCornerShape(20.dp))
                            .clickable {
                                if (hasRecordPermission) {
                                    speechRecognizer.startListening(intent)
                                } else {
                                    recordPermissionState.launchPermissionRequest()
                                }
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        BasicTextField(
                            value = name,
                            onValueChange = {
                                name = it
                            },
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 16.dp)
                                .focusRequester(focusRequester),
                            enabled = isEditing,
                            textStyle = TextStyle(
                                fontSize = 30.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            ),
                            singleLine = true,
                        )
                        IconButton(
                            modifier = Modifier.size(60.dp),
                            onClick = {
                                isEditing = true
                            }
                        ) {
                            Icon(
                                modifier = Modifier.size(40.dp),
                                painter = painterResource(id = R.drawable.ic_keyboard),
                                contentDescription = "edit",
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                FullWidthButton(
                    text = "Create",
                    onClick = {
                        onEvent(PlaylistUiEvent.SetShowPlaylistDialog(false))
                        onEvent(PlaylistUiEvent.CreatePlaylist(name))
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
        LaunchedEffect(isEditing) {
            if (isEditing) {
                focusRequester.requestFocus() // 포커스 부여
                keyboardController?.show() // 키보드 표시
            }
        }
    }
}

fun setSpeechRecognizer(
    speechRecognizer: SpeechRecognizer,
    setSpeechStatusMsg: (String) -> Unit,
    setSpeechResult: (String) -> Unit
) {
    speechRecognizer.setRecognitionListener(object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle) {
            setSpeechStatusMsg("음성 인식 준비 중...")
        }

        override fun onBeginningOfSpeech() {
            setSpeechStatusMsg("음성 인식 중...")
        }

        override fun onRmsChanged(rmsdB: Float) {}
        override fun onBufferReceived(buffer: ByteArray?) {

        }

        override fun onEndOfSpeech() {
            setSpeechStatusMsg("음성 인식 완료")
        }

        override fun onError(error: Int) {
            setSpeechStatusMsg("오류 발생: $error")
        }

        override fun onResults(results: Bundle?) {
            val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            setSpeechResult(matches?.get(0) ?: "오류")
            setSpeechStatusMsg(matches?.get(0) ?: "결과를 찾을 수 없습니다.")
            println(matches?.get(0) ?: "결과를 찾을 수 없습니다.")
        }

        override fun onPartialResults(partialResults: Bundle?) {}
        override fun onEvent(eventType: Int, params: Bundle?) {}
    })
}