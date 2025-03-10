package com.pardess.artist.album

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import com.pardess.common.Result
import com.pardess.common.base.BaseUiEffect
import com.pardess.common.base.BaseUiEvent
import com.pardess.common.base.BaseUiState
import com.pardess.common.base.BaseViewModel
import com.pardess.domain.usecase.artist.ArtistUseCase
import com.pardess.model.Album
import com.pardess.navigation.Screen

// UI State
data class DetailAlbumUiState(
    val albumState: Result<Album> = Result.Loading,
    val albumsState: Result<List<Album>> = Result.Loading,
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
    private val albumsFlow: StateFlow<Result<List<Album>>> = useCase.getAlbumsByArtist(artistId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Result.Loading)

    private val albumFlow: StateFlow<Result<Album>> =
        useCase.getAlbum(artistId = artistId, albumId = albumId.value)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Result.Loading)

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
                updateState { copy(albumState = Result.Success(event.album)) }
                sendEffect(DetailAlbumUiEffect.NavigateToAlbum(Screen.DetailArtist.route + "/${event.album.artistId}/${event.album.id}"))
            }

            else -> {}
        }
    }
}