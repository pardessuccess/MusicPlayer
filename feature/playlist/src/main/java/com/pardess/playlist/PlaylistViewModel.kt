package com.pardess.playlist

import androidx.lifecycle.viewModelScope
import com.pardess.common.base.BaseUiEffect
import com.pardess.common.base.BaseUiEvent
import com.pardess.common.base.BaseUiState
import com.pardess.common.base.BaseViewModel
import com.pardess.domain.usecase.playlist.PlaylistUseCase
import com.pardess.model.Playlist
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


data class PlaylistUiState(
    val selectedPlaylist: Playlist? = null,
    val playlists: List<Playlist> = emptyList(),
    val selectedPlaylistIds: List<Long> = emptyList(),
    val deleteMode: Boolean = false,
    val isShowCreateDialog: Boolean = false,
) : BaseUiState

sealed class PlaylistUiEvent : BaseUiEvent {
    data class ChangePlaylistOrder(val changedPlaylists: List<Playlist>) : PlaylistUiEvent()
    data class SetShowPlaylistDialog(val isShow: Boolean) : PlaylistUiEvent()
    data class CreatePlaylist(val playlistName: String) : PlaylistUiEvent()
    data class TogglePlaylistSelection(val playlist: Playlist, val isSelected: Boolean) :
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
    private val useCase: PlaylistUseCase,
) : BaseViewModel<PlaylistUiState, PlaylistUiEvent, PlaylistUiEffect>(PlaylistUiState()) {

    private val playlists = useCase.getPlaylists()
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
                    useCase.deletePlaylist(uiState.value.selectedPlaylistIds)
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

    private fun changePlaylistsOrder(changedPlaylists: List<Playlist>) {
        viewModelScope.launch(Dispatchers.IO) {
            useCase.changePlaylistOrder(changedPlaylists)
        }
    }

    private fun createPlaylist(playlistName: String) {
        viewModelScope.launch {
            val newPlaylist = useCase.createPlaylist(playlistName)
            updateState {
                copy(selectedPlaylist = newPlaylist)
            }
        }
    }
}
