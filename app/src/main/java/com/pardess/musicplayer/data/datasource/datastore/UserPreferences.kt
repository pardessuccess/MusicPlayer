package com.pardess.musicplayer.data.datasource.datastore

import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.flow.Flow

interface UserPreferences {
    suspend fun <T> setData(key: String, value: T)
    fun <T> getData(key: String, defaultValue: T): Flow<T>
}
