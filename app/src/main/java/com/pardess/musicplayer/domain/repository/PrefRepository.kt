package com.pardess.musicplayer.domain.repository

import kotlinx.coroutines.flow.Flow

interface PrefRepository {

    suspend fun setRepeatMode(value: Int)
    fun getRepeatMode(): Flow<Int>

    suspend fun setShuffleMode(value: Boolean)
    fun getShuffleMode(): Flow<Boolean>


}