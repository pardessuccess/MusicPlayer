package com.pardess.musicplayer.presentation.playlist

import androidx.lifecycle.viewModelScope
import com.pardess.musicplayer.data.entity.PlaylistEntity
import com.pardess.musicplayer.domain.repository.PlaylistRepository
import com.pardess.musicplayer.presentation.base.BaseUiEffect
import com.pardess.musicplayer.presentation.base.BaseUiEvent
import com.pardess.musicplayer.presentation.base.BaseUiState
import com.pardess.musicplayer.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


data class PlaylistUiState(
    val selectedPlaylist: PlaylistEntity? = null,
    val playlists: List<PlaylistEntity> = emptyList(),
    val selectedPlaylistIds: List<Long> = emptyList(),
    val deleteMode: Boolean = false,
    val isShowCreateDialog: Boolean = false,
) : BaseUiState

sealed class PlaylistUiEvent : BaseUiEvent {
    data class ChangePlaylistOrder(val changedPlaylists: List<PlaylistEntity>) : PlaylistUiEvent()
    data class SetShowPlaylistDialog(val isShow: Boolean) : PlaylistUiEvent()
    data class CreatePlaylist(val playlistName: String) : PlaylistUiEvent()
    data class TogglePlaylistSelection(val playlist: PlaylistEntity, val isSelected: Boolean) :
        PlaylistUiEvent()
    object DeletePlaylists : PlaylistUiEvent()
    object ToggleDeleteMode : PlaylistUiEvent()
    data class Navigate(val route: String) : PlaylistUiEvent()
    data class ShowToast(val message: String) : PlaylistUiEvent()
}

sealed class PlaylistUiEffect : BaseUiEffect {
    data class Navigate(val route: String) : PlaylistUiEffect()
    data class ShowToast(val message: String) : PlaylistUiEffect()
}

@HiltViewModel
class PlaylistViewModel @Inject constructor(
    private val playlistRepository: PlaylistRepository,
) : BaseViewModel<PlaylistUiState, PlaylistUiEvent, PlaylistUiEffect>(PlaylistUiState()) {

    private val playlists = playlistRepository.getAllPlaylist()
        .map { list -> list.sortedWith(compareBy<PlaylistEntity> { it.pinnedAt != null }.thenBy { it.pinnedAt }) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = emptyList()
        )

    init {
        collectState(playlists) { playlists ->
            copy(playlists = playlists)
        }
    }

    override fun onEvent(event: PlaylistUiEvent) {
        when (event) {
            is PlaylistUiEvent.TogglePlaylistSelection -> {
                val selectedPlaylists = uiState.value.selectedPlaylistIds.toMutableList()
                if (event.isSelected) {
                    selectedPlaylists.add(event.playlist.playlistId)
                } else {
                    selectedPlaylists.remove(event.playlist.playlistId)
                }
                updateState {
                    copy(selectedPlaylistIds = selectedPlaylists)
                }
            }

            is PlaylistUiEvent.ChangePlaylistOrder -> {
                changePlaylistsOrder(event.changedPlaylists)
            }

            is PlaylistUiEvent.CreatePlaylist -> {
                createPlaylist(event.playlistName)
            }

            PlaylistUiEvent.DeletePlaylists -> {
                viewModelScope.launch {
                    uiState.value.selectedPlaylistIds.let {
                        playlistRepository.deletePlaylistsByIds(it)
                    }
                }
                updateState {
                    copy(
                        selectedPlaylistIds = emptyList(),
                        deleteMode = false
                    )
                }
            }

            PlaylistUiEvent.ToggleDeleteMode -> {
                updateState {
                    copy(deleteMode = !deleteMode)
                }
            }

            is PlaylistUiEvent.SetShowPlaylistDialog -> {
                updateState {
                    copy(isShowCreateDialog = event.isShow)
                }
            }

            is PlaylistUiEvent.Navigate -> {
                sendEffect(PlaylistUiEffect.Navigate(event.route))
            }

            is PlaylistUiEvent.ShowToast -> {
                sendEffect(PlaylistUiEffect.ShowToast(event.message))
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

    private fun createPlaylist(playlistName: String) {
        viewModelScope.launch {
            val newPlaylist = withContext(Dispatchers.IO) {
                val currentPlaylists =
                    playlistRepository.getAllPlaylist().firstOrNull() ?: emptyList()
                val newDisplayOrder = currentPlaylists.size
                val newPlaylist = PlaylistEntity(
                    playlistName = playlistName,
                    displayOrder = newDisplayOrder,
                )
                playlistRepository.createPlaylist(newPlaylist)
            }
            updateState {
                copy(selectedPlaylist = newPlaylist)
            }
        }
    }
}
