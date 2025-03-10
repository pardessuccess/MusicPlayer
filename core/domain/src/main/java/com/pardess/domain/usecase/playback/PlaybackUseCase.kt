package com.pardess.domain.usecase.playback

import com.pardess.common.Result
import com.pardess.common.Utils.rearrangeList
import com.pardess.domain.repository.ManageRepository
import com.pardess.domain.repository.MediaPlayerRepository
import com.pardess.domain.repository.MusicRepository
import com.pardess.domain.repository.PrefRepository
import com.pardess.model.PlayerState
import com.pardess.model.Song
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.time.Duration
import javax.inject.Inject


data class PlaybackStateUpdateResult(
    val currentSongId: Long?,
    val historyUpdated: Boolean,
    val playCountUpdated: Boolean
)

interface PlaybackUseCase {

    fun playerStateFlow(): Flow<PlayerState>
    fun initMediaController()

    suspend fun handlePlaybackState(
        currentUiState: PlayerState,
        newPlayerState: PlayerState,
        currentSongId: Long?,
        historyUpdated: Boolean,
        playCountUpdated: Boolean
    ): PlaybackStateUpdateResult

    fun getAllSongs(): Flow<List<Song>>
    suspend fun onPlay(index: Int, songs: List<Song>)
    fun onPause()
    fun onResume()
    fun onSkipToNext()
    fun onSkipToPrevious()
    suspend fun onRepeatMode(repeatMode: Int)
    suspend fun onShuffleMode(shuffleMode: Boolean)
    fun onSeekPosition(duration: Duration)
    suspend fun saveFavoriteSong(song: Song)
    suspend fun onPlayRandom()

    suspend fun insertHistorySong(song: Song)
    suspend fun updatePlayCountSong(song: Song)

}

class PlaybackUseCaseImpl @Inject constructor(
    private val mediaPlayerRepository: MediaPlayerRepository,
    private val musicRepository: MusicRepository,
    private val manageRepository: ManageRepository,
    private val prefRepository: PrefRepository,
) : PlaybackUseCase {
    override fun playerStateFlow(): Flow<PlayerState> {
        return mediaPlayerRepository.getPlayerStateFlow()
    }

    override fun initMediaController() {
        mediaPlayerRepository.initMediaController()
    }

    override suspend fun handlePlaybackState(
        currentUiState: PlayerState,
        newPlayerState: PlayerState,
        currentSongId: Long?,
        historyUpdated: Boolean,
        playCountUpdated: Boolean
    ): PlaybackStateUpdateResult {
        var updatedSongId = currentSongId
        var updatedHistory = historyUpdated
        var updatedPlayCount = playCountUpdated

        println(currentUiState.repeatMode.toString() + " @@@ " + newPlayerState.repeatMode)

        val currentSong = newPlayerState.currentSong
        if (currentSong != null) {
            // 새 곡이 시작되면 update 플래그 리셋
            if (updatedSongId != currentSong.id) {
                updatedSongId = currentSong.id
                updatedHistory = false
                updatedPlayCount = false
            }
            val historyThresholdMillis = 30_000L // 30초
            val playCountThresholdMillis = currentSong.duration.toMillis() / 2

            if (!updatedHistory && newPlayerState.currentPosition.toMillis() >= historyThresholdMillis) {
                updatedHistory = true
                insertHistorySong(currentSong)
            }

            if (!updatedPlayCount && newPlayerState.currentPosition.toMillis() >= playCountThresholdMillis) {
                updatedPlayCount = true
                updatePlayCountSong(currentSong)
            }
        }

        return PlaybackStateUpdateResult(
            currentSongId = updatedSongId,
            historyUpdated = updatedHistory,
            playCountUpdated = updatedPlayCount
        )
    }

    override fun getAllSongs(): Flow<List<Song>> {
        return musicRepository.getSongs().map { result ->
            if (result is Result.Error) {
                return@map emptyList<Song>()
            }

            if (result is Result.Loading) {
                return@map emptyList<Song>()
            }

            (result as Result.Success).data
        }
    }

    override suspend fun onPlay(index: Int, songs: List<Song>) {
        val shuffleMode = prefRepository.getShuffleMode().first()
        val repeatMode = prefRepository.getRepeatMode().first()
        mediaPlayerRepository.shuffle(shuffleMode)
        mediaPlayerRepository.repeat(repeatMode)
        mediaPlayerRepository.addMediaItems(rearrangeList(songs, index))
        mediaPlayerRepository.play()
    }

    override fun onPause() {
        mediaPlayerRepository.pause()
    }

    override fun onResume() {
        mediaPlayerRepository.resume()
    }

    override fun onSkipToNext() {
        mediaPlayerRepository.next()
    }

    override fun onSkipToPrevious() {
        mediaPlayerRepository.previous()
    }

    override suspend fun onRepeatMode(repeatMode: Int) {
        mediaPlayerRepository.repeat(repeatMode)
        prefRepository.setRepeatMode(repeatMode)
    }

    override suspend fun onShuffleMode(shuffleMode: Boolean) {
        mediaPlayerRepository.shuffle(shuffleMode)
        prefRepository.setShuffleMode(shuffleMode)
    }

    override fun onSeekPosition(duration: Duration) {
        mediaPlayerRepository.onSeekingFinished(duration)
    }

    override suspend fun saveFavoriteSong(song: Song) {
        manageRepository.upsertFavorite(song = song)
    }

    override suspend fun onPlayRandom() {
        if (getAllSongs().first().isNotEmpty()) {
            onPlay(0, getAllSongs().first().shuffled())
        }
    }

    override suspend fun insertHistorySong(song: Song) {
        manageRepository.insertHistory(song, System.currentTimeMillis())
    }

    override suspend fun updatePlayCountSong(song: Song) {
        manageRepository.upsertPlayCount(song)
    }
}