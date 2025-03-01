package com.pardess.musicplayer.presentation.artist.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pardess.musicplayer.domain.model.Album
import com.pardess.musicplayer.domain.model.Song
import com.pardess.musicplayer.domain.repository.MusicRepository
import com.pardess.musicplayer.presentation.UiState
import com.pardess.musicplayer.presentation.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class DetailArtistViewModel @Inject constructor(
    private val repository: MusicRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val artistId = savedStateHandle.get<Long>("artistId") ?: 0L

    private val artistSongsState: StateFlow<UiState<List<Song>>> =
        repository.getSongsByArtist(artistId)
            .map { UiState.Success(it) as UiState<List<Song>> }
            .onStart { emit(UiState.Loading) }
            .catch { emit(UiState.Error(it.message ?: "Unknown Error")) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = UiState.Loading
            )
    private val artistAlbumsState: StateFlow<UiState<List<Album>>> =
        repository.getAlbumsByArtist(artistId).map {
            UiState.Success(it) as UiState<List<Album>>
        }.onStart { emit(UiState.Loading) }
            .catch {
                emit(UiState.Error(it.message ?: "Unknown Error"))
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = UiState.Loading
            )

    val uiState = MutableStateFlow(DetailArtistUiState())

    fun onEvent(event: DetailArtistUiEvent, onNavigateToRoute: (String) -> Unit = {}) {
        when (event) {
            is DetailArtistUiEvent.EnterDetailAlbum -> {
                println("@@@@ EnterDetailAlbum Screen.DetailArtist.route + /${artistId}/${event.albumId}")
                onNavigateToRoute(Screen.DetailArtist.route + "/${artistId}/${event.albumId}")
            }
        }
    }

    init {
        loadArtistData()
    }


    private fun loadArtistData() {
        viewModelScope.launch {
            artistAlbumsState.collectLatest {
                if (it is UiState.Success) {
                    uiState.value = uiState.value.copy(
                        albumsState = it
                    )
                }
            }
        }
        viewModelScope.launch() {
            artistSongsState.collectLatest {
                if (it is UiState.Success) {
                    uiState.value = uiState.value.copy(
                        songsState = it
                    )
                }
            }
        }
    }
}

