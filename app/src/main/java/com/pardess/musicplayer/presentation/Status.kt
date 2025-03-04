package com.pardess.musicplayer.presentation


sealed class Status<out T> {
    object Loading : Status<Nothing>()
    data class Success<out T>(val data: T) : Status<T>()
    data class Error(val message: String) : Status<Nothing>()
}