package com.pardess.musicplayer.presentation.playlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pardess.musicplayer.data.entity.PlaylistEntity
import com.pardess.musicplayer.data.entity.PlaylistSong
import com.pardess.musicplayer.domain.repository.PlaylistRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class PlaylistViewModel @Inject constructor(
    private val playlistRepository: PlaylistRepository,
) : ViewModel() {

    private val playlists = playlistRepository.getAllPlaylist().map { list ->
        list.sortedWith(compareBy<PlaylistEntity> { it.pinnedAt != null }.thenBy { it.pinnedAt })
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = emptyList()
    )

    private val _uiState = MutableStateFlow(PlaylistUiState())
    val uiState = combine(_uiState, playlists) { baseState, playlists ->
        baseState.copy(playlists = playlists)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = PlaylistUiState()
    )

    fun onEvent(event: PlaylistUiEvent) {
        when (event) {
            is PlaylistUiEvent.SetShowPlaylistDialog -> {
                _uiState.value = _uiState.value.copy(
                    dialogState = _uiState.value.dialogState.copy(isShowCreatePlaylistDialog = event.isShow)
                )
            }

            is PlaylistUiEvent.SetShowAddSongDialog -> {
                _uiState.value = _uiState.value.copy(
                    dialogState = _uiState.value.dialogState.copy(isShowAddSongDialog = event.isShow)
                )
            }

            is PlaylistUiEvent.ToggleSongSelection -> {
                // 예시: selectedSongs 리스트에 추가하거나 제거
                val currentSongs = _uiState.value.dialogState.selectedSongs.toMutableList()
                if (event.isSelected) {
                    currentSongs.add(event.song)
                } else {
                    currentSongs.remove(event.song)
                }
                _uiState.value = _uiState.value.copy(
                    dialogState = _uiState.value.dialogState.copy(selectedSongs = currentSongs)
                )
            }

            is PlaylistUiEvent.ChangePlaylistOrder -> {
                changePlaylistsOrder(event.changedPlaylists)
            }

            is PlaylistUiEvent.CreatePlaylist -> {
                createPlaylist(event.playlistName)
            }

            PlaylistUiEvent.DeletePlaylist -> {
                _uiState.value.selectedPlaylist?.let { deletePlaylist(it) }
            }

            PlaylistUiEvent.SavePlaylist -> {
                viewModelScope.launch {
                    playlistRepository.insertSongEntities(_uiState.value.dialogState.selectedSongs)
                }
                _uiState.value = _uiState.value.copy(
                    dialogState = _uiState.value.dialogState.copy(selectedSongs = emptyList())
                )
            }

            is PlaylistUiEvent.SetShowDeletePlaylistDialog -> {
                _uiState.value = _uiState.value.copy(
                    selectedPlaylist = event.playlistEntity,
                    dialogState = _uiState.value.dialogState.copy(isShowDeletePlaylistDialog = event.isShow)
                )
            }
        }
    }

    private fun changePlaylistsOrder(changedPlaylists: List<PlaylistEntity>) {
        viewModelScope.launch(Dispatchers.IO) {
            changedPlaylists.forEachIndexed { index, playlist ->
                playlist.displayOrder = index
                playlistRepository.updatePlaylist(playlist)
            }
        }
    }

    private fun deletePlaylist(playlist: PlaylistEntity) {
        viewModelScope.launch {
            playlistRepository.deletePlaylist(playlist)
        }
    }

    private fun createPlaylist(playlistName: String) =
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val currentPlaylists =
                    playlistRepository.getAllPlaylist().firstOrNull() ?: emptyList()
                val newDisplayOrder = currentPlaylists.size
                val newPlaylist = PlaylistEntity(
                    playlistName = playlistName,
                    displayOrder = newDisplayOrder,
                )
                _uiState.value = _uiState.value.copy(
                    selectedPlaylist = playlistRepository.createPlaylist(newPlaylist)
                )
            }
        }

    fun updatePlaylistsOrder(updatedPlaylists: List<PlaylistEntity>) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            val modifiedPlaylists = updatedPlaylists.mapIndexedNotNull { index, playlist ->
                if (playlist.displayOrder != index) {
                    playlist.copy(displayOrder = index) // 새로운 순서로 변경
                } else null
            }

            if (modifiedPlaylists.isNotEmpty()) {
                playlistRepository.updatePlaylists(modifiedPlaylists) // ✅ 변경된 항목만 일괄 업데이트
            }
        }
    }

    fun addSongEntityToPlaylist(playlistSong: PlaylistSong) {
        viewModelScope.launch {
            playlistRepository.insertSongEntity(playlistSong)
        }
    }

}