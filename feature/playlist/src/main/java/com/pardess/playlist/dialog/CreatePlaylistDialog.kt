package com.pardess.playlist.dialog

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.Toast
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import com.pardess.designsystem.Gray300
import com.pardess.designsystem.PointColor
import com.pardess.designsystem.TextColor
import com.pardess.playlist.PlaylistUiEvent
import com.pardess.playlist.R
import com.pardess.ui.FullWidthButton

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

    var showGuideText by remember { mutableStateOf(true) }

    var isRecording by remember { mutableStateOf(false) }

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
        },
        setSpeechStatus = {
            isRecording = when (it) {
                SpeechStatus.READY, SpeechStatus.IN_PROGRESS -> true
                SpeechStatus.COMPLETE, SpeechStatus.ERROR -> false
            }
        }
    )

    val infiniteTransition = rememberInfiniteTransition()

    val radius by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 300f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000),
            repeatMode = RepeatMode.Restart
        ), label = ""
    )

    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000),
            repeatMode = RepeatMode.Restart
        ), label = ""
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
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .background(
                                shape = RoundedCornerShape(20.dp),
                                color = PointColor
                            )
                            .clip(shape = RoundedCornerShape(20.dp))
                            .clickable {
                                if (hasRecordPermission) {
                                    showGuideText = false
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
                                color = TextColor
                            ),
                            singleLine = true,
                        )
                        IconButton(
                            modifier = Modifier.size(60.dp),
                            onClick = {
                                showGuideText = false
                                isEditing = true
                            }
                        ) {
                            Icon(
                                modifier = Modifier.size(40.dp),
                                painter = painterResource(id = R.drawable.ic_keyboard),
                                contentDescription = "edit",
                                tint = TextColor
                            )
                        }
                    }
                    if (showGuideText) {
                        Text(
                            text = "눌러서 입력하세요",
                            modifier = Modifier
                                .padding(16.dp)
                                .align(Alignment.CenterStart),
                            color = Color.Black,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                if (isRecording) {
                    Box {
                        Icon(
                            tint = TextColor,
                            painter = painterResource(id = R.drawable.ic_mic),
                            contentDescription = "mic",
                            modifier = Modifier
                                .size(100.dp)
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(50.dp))
                                .background(
                                    shape = RoundedCornerShape(75.dp),
                                    color = Color.Red
                                )
                        )
                        Canvas(
                            modifier = Modifier
                                .size(100.dp)
                                .aspectRatio(1f)
                        ) {
                            drawCircle(
                                color = Color.Black.copy(alpha = alpha), // 투명도 적용
                                radius = radius // 애니메이션된 반지름 적용
                            )
                        }
                    }
                } else {
                    FullWidthButton(
                        text = "만들기",
                        color = if (name.isEmpty()) Gray300 else PointColor,
                        onClick = {
                            if (name.isEmpty()) return@FullWidthButton
                            onEvent(PlaylistUiEvent.SetShowPlaylistDialog(false))
                            onEvent(PlaylistUiEvent.CreatePlaylist(name))
                        }
                    )
                }
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
    setSpeechResult: (String) -> Unit,
    setSpeechStatus: (SpeechStatus) -> Unit,
) {
    speechRecognizer.setRecognitionListener(object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle) {
            setSpeechStatusMsg("음성 인식 준비 중...")
            setSpeechStatus(SpeechStatus.READY)
        }

        override fun onBeginningOfSpeech() {
            setSpeechStatusMsg("음성 인식 중...")
            setSpeechStatus(SpeechStatus.IN_PROGRESS)
        }

        override fun onRmsChanged(rmsdB: Float) {}
        override fun onBufferReceived(buffer: ByteArray?) {

        }

        override fun onEndOfSpeech() {
            setSpeechStatusMsg("음성 인식 완료")
            setSpeechStatus(SpeechStatus.COMPLETE)
        }

        override fun onError(error: Int) {
            setSpeechStatusMsg("오류 발생: $error")
            setSpeechStatus(SpeechStatus.ERROR)
        }

        override fun onResults(results: Bundle?) {
            val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            setSpeechResult(matches?.get(0) ?: "오류")
            setSpeechStatusMsg(matches?.get(0) ?: "결과를 찾을 수 없습니다.")
            println(matches?.get(0) ?: "결과를 찾을 수 없습니다.")
            if (matches?.get(0) != null) {
                setSpeechStatus(SpeechStatus.COMPLETE)
            } else {
                setSpeechStatus(SpeechStatus.ERROR)
            }
        }

        override fun onPartialResults(partialResults: Bundle?) {}
        override fun onEvent(eventType: Int, params: Bundle?) {}
    })
}

enum class SpeechStatus {
    READY,
    COMPLETE,
    ERROR,
    IN_PROGRESS
}