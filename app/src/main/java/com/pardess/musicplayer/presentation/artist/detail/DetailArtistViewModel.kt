package com.pardess.musicplayer.presentation.artist.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pardess.musicplayer.domain.model.Album
import com.pardess.musicplayer.domain.model.Song
import com.pardess.musicplayer.domain.repository.MusicRepository
import com.pardess.musicplayer.domain.usecase.artist.ArtistUseCase
import com.pardess.musicplayer.presentation.Status
import com.pardess.musicplayer.presentation.base.BaseUiEffect
import com.pardess.musicplayer.presentation.base.BaseUiEvent
import com.pardess.musicplayer.presentation.base.BaseUiState
import com.pardess.musicplayer.presentation.base.BaseViewModel
import com.pardess.musicplayer.presentation.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
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


sealed class DetailArtistUiEffect : BaseUiEffect {
    data class NavigateToAlbum(val route: String) : DetailArtistUiEffect()
}

data class DetailArtistUiState(
    val songsState: Status<List<Song>> = Status.Loading,
    val albumsState: Status<List<Album>> = Status.Loading
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

    private val artistSongsState: StateFlow<Status<List<Song>>> =
        useCase.getSongsByArtist(artistId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = Status.Loading
            )
    private val artistAlbumsState: StateFlow<Status<List<Album>>> =
        useCase.getAlbumsByArtist(artistId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = Status.Loading
            )

    override fun onEvent(event: DetailArtistUiEvent) {
        when (event) {
            is DetailArtistUiEvent.EnterDetailAlbum -> {
                sendEffect(DetailArtistUiEffect.NavigateToAlbum(Screen.DetailArtist.route + "/${artistId}/${event.albumId}"))
            }
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

