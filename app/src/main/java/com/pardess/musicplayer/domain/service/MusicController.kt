package com.pardess.musicplayer.domain.service

import com.pardess.musicplayer.domain.model.Song
import com.pardess.musicplayer.domain.model.enums.PlayerState

interface MusicController {

    var mediaControllerCallback: (
        (
        playerState: PlayerState,
        currentSong: Song?,
        currentPosition: Long,
        totalDuration: Long,
        isShuffleEnabled: Boolean,
        repeatMode: Int
    ) -> Unit
    )?

    var serviceCallback: (
        (
        playerState: PlayerState,
        currentSong: Song?,
        currentPosition: Long,
        totalDuration: Long,
        isShuffleEnabled: Boolean,
        repeatMode: Int
    ) -> Unit
    )?

    fun getPlayerState(): PlayerState

    fun setShuffleModeEnabled(shuffleMode: Boolean)

    fun setRepeatMode(repeatMode: Int)

    fun callbackOnes()

    fun controllerListener()

    fun addMediaItems(songs: List<Song>)

    fun play(index: Int)

    fun resume()

    fun pause()

    fun getCurrentPosition(): Long

    fun destroy()

    fun skipToNextSong()

    fun skipToPreviousSong()

    fun getCurrentSong(): Song?

    fun seekTo(position: Long)

}