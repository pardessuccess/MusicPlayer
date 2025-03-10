package com.pardess.media_service

import android.app.PendingIntent
import android.content.Context

interface PendingIntentProvider {
    fun getMainActivityPendingIntent(context: Context): PendingIntent
}
