package com.pardess.domain.repository

import com.pardess.model.PlayerState
import kotlinx.coroutines.flow.Flow

interface MediaPlayerListenerRepository {
    fun getPlayerStateFlow(): Flow<PlayerState>
    val isSessionReady: Flow<Boolean>
}