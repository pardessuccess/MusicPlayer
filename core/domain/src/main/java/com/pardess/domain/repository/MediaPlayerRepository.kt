package com.pardess.domain.repository

import com.pardess.model.PlayerState
import com.pardess.model.Song
import kotlinx.coroutines.flow.Flow
import java.time.Duration

interface MediaPlayerRepository {
    fun getPlayerStateFlow(): Flow<PlayerState>
    fun initMediaController()
    fun addMediaItems(songs: List<Song>)
    fun play()
    fun resume()
    fun pause()
    fun stop()
    fun next()
    fun previous()
    fun repeat(repeatMode: Int)
    fun shuffle(shuffle: Boolean)
    fun onSeekingStarted()
    fun onSeekingFinished(duration: Duration)
}
