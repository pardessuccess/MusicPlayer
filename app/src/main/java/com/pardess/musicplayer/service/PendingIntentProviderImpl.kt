package com.pardess.musicplayer.service

import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import com.pardess.media_service.PendingIntentProvider
import com.pardess.musicplayer.MainActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class PendingIntentProviderImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : PendingIntentProvider {
    override fun getMainActivityPendingIntent(context: Context): PendingIntent {
        return PendingIntent.getActivity(
            context,
            0,
            Intent().apply {
                action = Intent.ACTION_VIEW
                component = ComponentName(context, MainActivity::class.java)
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}