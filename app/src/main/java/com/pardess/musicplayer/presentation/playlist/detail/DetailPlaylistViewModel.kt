package com.pardess.musicplayer.presentation.playlist.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pardess.musicplayer.data.entity.PlaylistSong
import com.pardess.musicplayer.domain.repository.PlaylistRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailPlaylistViewModel @Inject constructor(
    private val playlistRepository: PlaylistRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val playlistId = savedStateHandle.get<Long>("playlistId") ?: 0L

    private val _uiState = MutableStateFlow(DetailPlaylistUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadPlaylistDetail()
    }

    private fun loadPlaylistDetail() {
        viewModelScope.launch {
            playlistRepository.getPlaylistWithSongs(playlistId).collectLatest {
                _uiState.value = _uiState.value.copy(
                    playlist = it.playlist,
                    playlistSongs = it.songs,
                    dialogState = if (it.songs.isEmpty()) {
                        _uiState.value.dialogState.copy(isShowAddSongDialog = true)
                    } else {
                        _uiState.value.dialogState
                    }
                )
            }
        }
    }

    private fun deleteSongs(songs: List<PlaylistSong>) {
        viewModelScope.launch {
            playlistRepository.deleteSongEntities(songs)
        }
    }

    fun onEvent(event: DetailPlaylistUiEvent) {
        when (event) {
            is DetailPlaylistUiEvent.DeleteSongFromPlaylist -> {
                viewModelScope.launch {
                    println("@@@ Delete song ${_uiState.value.deleteSong}")
                    _uiState.value.deleteSong?.let { playlistRepository.deleteSongEntity(it) }
                }
            }

            DetailPlaylistUiEvent.SaveSongToPlaylist -> {
                viewModelScope.launch {
                    playlistRepository.insertSongEntities(_uiState.value.selectedSongs)
                }
                _uiState.value = _uiState.value.copy(
                    selectedSongs = emptyList(),
                    dialogState = _uiState.value.dialogState.copy(
                        isShowAddSongDialog = false
                    )
                )
            }

            is DetailPlaylistUiEvent.SetShowAddSongDialog -> {
                _uiState.value = _uiState.value.copy(
                    selectedSongs = emptyList(),
                    dialogState = _uiState.value.dialogState.copy(
                        isShowAddSongDialog = event.isShow,
                    ),
                )
            }

            is DetailPlaylistUiEvent.ToggleSongSelection -> {
                val currentSongs = _uiState.value.selectedSongs.toMutableList()
                if (event.isSelected) {
                    if (currentSongs.none { it.song.id == event.playlistSong.song.id }) {
                        currentSongs.add(event.playlistSong)
                    }
                } else {
                    currentSongs.removeAll { it.song.id == event.playlistSong.song.id }
                }
                _uiState.value = _uiState.value.copy(
                    selectedSongs = currentSongs
                )
            }

            is DetailPlaylistUiEvent.SetShowDeleteSongDialog -> {
                _uiState.value = _uiState.value.copy(
                    deleteSong = event.deleteSong,
                    selectedSongs = emptyList(),
                    dialogState = _uiState.value.dialogState.copy(
                        isShowDeleteSongDialog = event.isShow,
                    )
                )
            }

            DetailPlaylistUiEvent.DeleteSelectedSongs -> {
                _uiState.value = _uiState.value.copy(
                    dialogState = _uiState.value.dialogState.copy(
                        isShowDeleteSongDialog = false
                    )
                )
                deleteSongs(_uiState.value.selectedSongs)
            }

            DetailPlaylistUiEvent.ToggleDeleteMode -> {
                _uiState.value = _uiState.value.copy(
                    selectedSongs = emptyList(),
                    deleteMode = !_uiState.value.deleteMode
                )
            }
        }
    }
}