package com.pardess.musicplayer.presentation.artist.album

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pardess.musicplayer.domain.model.Album
import com.pardess.musicplayer.domain.repository.MusicRepository
import com.pardess.musicplayer.presentation.UiState
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
class DetailAlbumViewModel @Inject constructor(
    private val repository: MusicRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val artistId = savedStateHandle.get<Long>("artistId") ?: 0L
    private val albumId = MutableStateFlow(savedStateHandle.get<Long>("albumId") ?: -1L)

    val uiState = MutableStateFlow(DetailAlbumUiState())

    private val albumsState: StateFlow<UiState<List<Album>>> =
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

    private fun loadAlbumData() {
        viewModelScope.launch {
            albumsState.collectLatest {
                if (it is UiState.Success<List<Album>>) {
                    if (albumId.value != -1L) {
                        val album = it.data.firstOrNull { it.id == albumId.value }
                        println("@@@@" + albumId.value)
                        if (album != null) {
                            uiState.value = uiState.value.copy(
                                albumState = UiState.Success(album),
                                albumsState = UiState.Success(it.data.filter { it.id != albumId.value })
                            )
                        } else {
                            uiState.value = uiState.value.copy(albumsState = UiState.Success(it.data))
                        }
                    } else {
                        uiState.value = uiState.value.copy(albumsState = UiState.Success(it.data))
                    }
                }
            }
        }
    }

    fun onEvent(event: DetailAlbumEvent) {
        when (event) {
            is DetailAlbumEvent.SelectAlbum -> {
                uiState.value = uiState.value.copy(albumState = UiState.Success(event.album))
            }

            is DetailAlbumEvent.SetAlbums -> {
                uiState.value = uiState.value.copy(albumsState = UiState.Success(event.albums))
            }
        }
    }

    init {
        loadAlbumData()
    }
}