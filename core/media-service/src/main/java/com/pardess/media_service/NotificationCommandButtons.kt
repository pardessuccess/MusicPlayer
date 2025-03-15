package com.pardess.media_service

import android.os.Bundle
import androidx.media3.session.SessionCommand

enum class NotificationCommandButtons(
    val customAction: String,
    val displayName: String,
    val iconResId: Int,
    val sessionCommand: SessionCommand,
) {
    FAVORITE(
        customAction = "ACTION_FAVORITE",
        displayName = "Favorite",
        iconResId = R.drawable.ic_favorite ,
        sessionCommand = SessionCommand("ACTION_FAVORITE", Bundle.EMPTY)
    )
}