package com.pardess.musicplayer.presentation.playlist.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.pardess.musicplayer.data.entity.PlaylistEntity
import com.pardess.musicplayer.data.entity.PlaylistSong
import com.pardess.musicplayer.domain.repository.PlaylistRepository
import com.pardess.musicplayer.presentation.base.BaseUiEffect
import com.pardess.musicplayer.presentation.base.BaseUiEvent
import com.pardess.musicplayer.presentation.base.BaseUiState
import com.pardess.musicplayer.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DetailPlaylistUiState(
    val playlist: PlaylistEntity? = null,
    val playlistSongs: List<PlaylistSong> = emptyList(),
    val deleteMode: Boolean = false,
    val selectedSongs: List<PlaylistSong> = emptyList(),
    val isShowAddSongDialog: Boolean = false,
) : BaseUiState

sealed class DetailPlaylistUiEvent : BaseUiEvent {
    object DeleteSelectedSongs : DetailPlaylistUiEvent()
    object ToggleDeleteMode : DetailPlaylistUiEvent()
    object SaveSongToPlaylist : DetailPlaylistUiEvent()
    data class ToggleSongSelection(val playlistSong: PlaylistSong, val isSelected: Boolean) :
        DetailPlaylistUiEvent()

    data class SetShowAddSongDialog(val isShow: Boolean) : DetailPlaylistUiEvent()
}

sealed class DetailPlaylistUiEffect : BaseUiEffect {
    object SongSaved : DetailPlaylistUiEffect()
    object SongDeleted : DetailPlaylistUiEffect()
}

@HiltViewModel
class DetailPlaylistViewModel @Inject constructor(
    private val playlistRepository: PlaylistRepository,
    savedStateHandle: SavedStateHandle
) : BaseViewModel<DetailPlaylistUiState, DetailPlaylistUiEvent, DetailPlaylistUiEffect>(
    DetailPlaylistUiState()
) {

    private val playlistId = savedStateHandle.get<Long>("playlistId") ?: 0L

    private val playlistWithSongs = playlistRepository.getPlaylistWithSongs(playlistId).stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        initialValue = null,
    )

    init {
        collectState(playlistWithSongs) { playlistSongs ->
            copy(
                playlist = playlistSongs?.playlist,
                playlistSongs = playlistSongs?.songs ?: emptyList(),
                isShowAddSongDialog = playlistSongs?.songs?.isEmpty() == true
            )
        }
    }

    override fun onEvent(event: DetailPlaylistUiEvent) {
        when (event) {
            DetailPlaylistUiEvent.SaveSongToPlaylist -> {
                viewModelScope.launch {
                    playlistRepository.insertSongEntities(uiState.value.selectedSongs)
                }
                updateState {
                    copy(
                        selectedSongs = emptyList(),
                        isShowAddSongDialog = false,
                    )
                }
                sendEffect(DetailPlaylistUiEffect.SongSaved)
            }

            is DetailPlaylistUiEvent.SetShowAddSongDialog -> {
                updateState {
                    copy(
                        selectedSongs = emptyList(),
                        isShowAddSongDialog = event.isShow,
                    )
                }
            }

            is DetailPlaylistUiEvent.ToggleSongSelection -> {
                val updatedSongs = uiState.value.selectedSongs.toMutableList()
                if (event.isSelected) {
                    updatedSongs.add(event.playlistSong)
                } else {
                    updatedSongs.remove(event.playlistSong)
                }
                updateState { copy(selectedSongs = updatedSongs) }
            }

            DetailPlaylistUiEvent.DeleteSelectedSongs -> {
                viewModelScope.launch {
                    val selectedSongs = uiState.value.selectedSongs
                    val playlistSongs = uiState.value.playlistSongs
                    val songsToDelete =
                        playlistSongs.filter { it.songPrimaryKey in selectedSongs.map { it.songPrimaryKey } }
                    playlistRepository.deleteSongEntities(songsToDelete)
                }
                updateState {
                    copy(
                        selectedSongs = emptyList(),
                        deleteMode = false
                    )
                }
                sendEffect(DetailPlaylistUiEffect.SongDeleted)
            }

            DetailPlaylistUiEvent.ToggleDeleteMode -> {
                updateState { copy(selectedSongs = emptyList(), deleteMode = !deleteMode) }
            }
        }
    }
}