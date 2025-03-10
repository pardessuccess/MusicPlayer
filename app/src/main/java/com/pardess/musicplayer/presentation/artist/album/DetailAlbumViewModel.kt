package com.pardess.musicplayer.presentation.artist.album

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pardess.musicplayer.domain.model.Album
import com.pardess.musicplayer.domain.model.Artist
import com.pardess.musicplayer.domain.repository.MusicRepository
import com.pardess.musicplayer.domain.usecase.artist.ArtistUseCase
import com.pardess.musicplayer.presentation.Status
import com.pardess.musicplayer.presentation.base.BaseUiEffect
import com.pardess.musicplayer.presentation.base.BaseUiEvent
import com.pardess.musicplayer.presentation.base.BaseUiState
import com.pardess.musicplayer.presentation.base.BaseViewModel
import com.pardess.musicplayer.presentation.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


// UI State
data class DetailAlbumUiState(
    val albumState: Status<Album> = Status.Loading,
    val albumsState: Status<List<Album>> = Status.Loading,
) : BaseUiState

// UI Effect (Navigation)
sealed class DetailAlbumUiEffect : BaseUiEffect {
    data class NavigateToAlbum(val route: String) : DetailAlbumUiEffect()
}

// UI Event
sealed class DetailAlbumUiEvent : BaseUiEvent {
    data class SelectAlbum(val album: Album) : DetailAlbumUiEvent()
}

@HiltViewModel
class DetailAlbumViewModel @Inject constructor(
    useCase: ArtistUseCase,
    savedStateHandle: SavedStateHandle
) : BaseViewModel<DetailAlbumUiState, DetailAlbumUiEvent, DetailAlbumUiEffect>(DetailAlbumUiState()) {

    private val artistId = savedStateHandle.get<Long>("artistId") ?: 0L
    private val albumId = MutableStateFlow(savedStateHandle.get<Long>("albumId") ?: -1L)
    private val albumsFlow: StateFlow<Status<List<Album>>> = useCase.getAlbumsByArtist(artistId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Status.Loading)

    private val albumFlow: StateFlow<Status<Album>> =
        useCase.getAlbum(artistId = artistId, albumId = albumId.value)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Status.Loading)

    init {
        viewModelScope.launch {
            combine(albumFlow, albumsFlow) { album, albums ->
                updateState {
                    copy(albumState = album, albumsState = albums)
                }
                println("@@@@ Album: $album $albums")
            }.launchIn(viewModelScope)
        }
    }

    override fun onEvent(event: DetailAlbumUiEvent) {
        when (event) {
            is DetailAlbumUiEvent.SelectAlbum -> {
                updateState { copy(albumState = Status.Success(event.album)) }
                sendEffect(DetailAlbumUiEffect.NavigateToAlbum(Screen.DetailArtist.route + "/${event.album.artistId}/${event.album.id}"))
            }
        }
    }
}