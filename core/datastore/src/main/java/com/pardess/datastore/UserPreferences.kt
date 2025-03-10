package com.pardess.datastore

import kotlinx.coroutines.flow.Flow

interface UserPreferences {
    suspend fun <T> setData(key: String, value: T)
    fun <T> getData(key: String, defaultValue: T): Flow<T>
}
