package com.pardess.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "user_prefs")

object UserPreferenceKeys {
    const val REPEAT_MODE_KEY = "repeat_mode"
    const val SHUFFLE_MODE_KEY = "shuffle_mode"
}

class UserPreferencesImpl(context: Context) : UserPreferences {
    private val dataStore = context.dataStore

    // ✅ setData: 제네릭으로 타입 안정성 보장
    override suspend fun <T> setData(key: String, value: T) {
        dataStore.edit { preferences ->
            when (value) {
                is Boolean -> preferences[booleanPreferencesKey(key)] = value
                is String -> preferences[stringPreferencesKey(key)] = value
                is Int -> preferences[intPreferencesKey(key)] = value
                is Float -> preferences[floatPreferencesKey(key)] = value
                is Long -> preferences[longPreferencesKey(key)] = value
                else -> throw IllegalArgumentException("Unsupported Type: ${value!!::class.java}")
            }
        }
    }

    // ✅ getData: 제네릭으로 타입 안정성 보장
    override fun <T> getData(key: String, defaultValue: T): Flow<T> {
        return dataStore.data.map { preferences ->
            val result: Any = when (defaultValue) {
                is Boolean -> preferences[booleanPreferencesKey(key)] ?: defaultValue
                is String -> preferences[stringPreferencesKey(key)] ?: defaultValue
                is Int -> preferences[intPreferencesKey(key)] ?: defaultValue
                is Float -> preferences[floatPreferencesKey(key)] ?: defaultValue
                is Long -> preferences[longPreferencesKey(key)] ?: defaultValue
                else -> throw IllegalArgumentException("Unsupported Type: ${defaultValue!!::class.java}")
            }
            @Suppress("UNCHECKED_CAST")
            result as T
        }
    }
}
