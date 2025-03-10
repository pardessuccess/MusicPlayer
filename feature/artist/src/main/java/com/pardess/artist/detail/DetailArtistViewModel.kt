package com.pardess.artist.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.pardess.common.Result
import com.pardess.common.base.BaseUiEffect
import com.pardess.common.base.BaseUiEvent
import com.pardess.common.base.BaseUiState
import com.pardess.common.base.BaseViewModel
import com.pardess.domain.usecase.artist.ArtistUseCase
import com.pardess.model.Album
import com.pardess.model.Song
import com.pardess.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject


sealed class DetailArtistUiEffect : BaseUiEffect {
    data class NavigateToAlbum(val route: String) : DetailArtistUiEffect()
}

data class DetailArtistUiState(
    val songsState: Result<List<Song>> = Result.Loading,
    val albumsState: Result<List<Album>> = Result.Loading
) : BaseUiState

sealed class DetailArtistUiEvent : BaseUiEvent {
    data class EnterDetailAlbum(val albumId: Long) : DetailArtistUiEvent()
}

@HiltViewModel
class DetailArtistViewModel @Inject constructor(
    useCase: ArtistUseCase,
    savedStateHandle: SavedStateHandle
) : BaseViewModel<DetailArtistUiState, DetailArtistUiEvent, DetailArtistUiEffect>(
    DetailArtistUiState()
) {

    private val artistId = savedStateHandle.get<Long>("artistId") ?: 0L

    private val artistSongsState: StateFlow<Result<List<Song>>> =
        useCase.getSongsByArtist(artistId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = Result.Loading
            )
    private val artistAlbumsState: StateFlow<Result<List<Album>>> =
        useCase.getAlbumsByArtist(artistId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = Result.Loading
            )

    override fun onEvent(event: DetailArtistUiEvent) {
        when (event) {
            is DetailArtistUiEvent.EnterDetailAlbum -> {
                sendEffect(DetailArtistUiEffect.NavigateToAlbum(Screen.DetailArtist.route + "/${artistId}/${event.albumId}"))
            }

            else -> {}
        }
    }

    init {
        collectState(artistSongsState) { songs ->
            copy(songsState = songs)
        }
        collectState(artistAlbumsState) { albums ->
            copy(albumsState = albums)
        }
    }
}

