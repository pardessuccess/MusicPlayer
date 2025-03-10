package com.pardess.musicplayer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    companion object {
        const val TAG = "MainActivity"
        const val EXPAND_PANEL = "expand_panel"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge()
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
//            window.decorView.systemUiVisibility = (
//                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
//                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
//                            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                    )
//        }
        // Android 10 이상에서도 전체 화면을 원한다면 아래 코드도 사용 가능 (모든 API 레벨에 대해 작동)
        setContent {
            MusicApp()
        }
    }
}