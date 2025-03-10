package com.pardess.musicplayer.presentation.main

import androidx.lifecycle.viewModelScope
import com.pardess.musicplayer.data.entity.SongEntity
import com.pardess.musicplayer.data.entity.join.FavoriteSong
import com.pardess.musicplayer.domain.model.Album
import com.pardess.musicplayer.domain.model.Artist
import com.pardess.musicplayer.domain.model.SearchHistory
import com.pardess.musicplayer.domain.usecase.main.MainUseCase
import com.pardess.musicplayer.presentation.base.BaseViewModel
import com.pardess.musicplayer.presentation.base.BaseUiEffect
import com.pardess.musicplayer.presentation.base.BaseUiEvent
import com.pardess.musicplayer.presentation.base.BaseUiState
import com.pardess.musicplayer.presentation.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MainUiState(
    val song1st: SongEntity? = null,
    val song2nd: SongEntity? = null,
    val song3rd: SongEntity? = null,
    val favoriteSongs: List<FavoriteSong> = emptyList(),
    val popularArtists: List<Artist> = emptyList(),
    val popularAlbums: List<Album> = emptyList(),
    val searchHistories: List<SearchHistory> = emptyList(),
    val searchBoxExpand: Boolean = false,
    val editMode: Boolean = false,
    val isRecording: Boolean = false,
    val showGuideText: Boolean = true,
) : BaseUiState

sealed class MainUiEvent : BaseUiEvent {
    data class Navigate(val route: String) : MainUiEvent()
    data class RemoveSearchHistory(val id: Long) : MainUiEvent()
    object SearchBoxShrink : MainUiEvent()
    object SearchBoxExpand : MainUiEvent()
    data class Search(val query: String) : MainUiEvent()
    object StartRecording : MainUiEvent()
    object StopRecording : MainUiEvent()
    data class SetEditMode(val editMode: Boolean) : MainUiEvent()
    object DismissGuideText : MainUiEvent()
    data class RecordingMessage(val message: String) : MainUiEvent()
}

sealed class MainUiEffect : BaseUiEffect {
    data class Navigate(val route: String) : MainUiEffect()
    data class Search(val query: String) : MainUiEffect()
    data class RecordingMessage(val message: String) : MainUiEffect()
}

@HiltViewModel
class MainViewModel @Inject constructor(
    private val useCase: MainUseCase,
) : BaseViewModel<MainUiState, MainUiEvent, MainUiEffect>(MainUiState()) {

    override fun onEvent(event: MainUiEvent) {
        when (event) {
            is MainUiEvent.Navigate -> {
                sendEffect(MainUiEffect.Navigate(event.route))
            }

            is MainUiEvent.RemoveSearchHistory -> {
                viewModelScope.launch {
                    useCase.deleteSearchHistory(event.id)
                }
            }

            MainUiEvent.SearchBoxExpand -> {
                updateState {
                    copy(searchBoxExpand = true)
                }
            }

            MainUiEvent.SearchBoxShrink -> {
                updateState {
                    copy(searchBoxExpand = false)
                }
            }

            is MainUiEvent.Search -> {
                viewModelScope.launch {
                    sendEffect(MainUiEffect.Navigate(Screen.Search.route))
                    sendEffect(MainUiEffect.Search(event.query))
                }
            }

            is MainUiEvent.SetEditMode -> {
                updateState {
                    copy(editMode = event.editMode)
                }
            }

            MainUiEvent.StartRecording -> {
                updateState {
                    copy(isRecording = true)
                }
            }

            MainUiEvent.StopRecording -> {
                updateState {
                    copy(isRecording = false)
                }
            }

            MainUiEvent.DismissGuideText -> {
                updateState {
                    copy(showGuideText = false)
                }
            }

            is MainUiEvent.RecordingMessage -> {
                sendEffect(MainUiEffect.RecordingMessage(event.message))
            }
        }
    }

    private val favoriteSongs = useCase.getFavoriteSongs().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        emptyList()
    )

    private val searchHistories = useCase.getSearchHistory().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        emptyList()
    )

    private val popularArtists = useCase.getPopularArtists()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val popularAlbums = useCase.getPopularAlbums()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    init {
        collectState(favoriteSongs) { songs ->
            copy(
                favoriteSongs = songs,
                song1st = songs.getOrNull(0)?.song,
                song2nd = songs.getOrNull(1)?.song,
                song3rd = songs.getOrNull(2)?.song
            )
        }
        collectState(popularArtists) { artistsList ->
            copy(popularArtists = artistsList.take(10))
        }
        collectState(popularAlbums) { albumsList ->
            copy(popularAlbums = albumsList.take(10))
        }
        collectState(searchHistories) { searchHistories ->
            copy(searchHistories = searchHistories)
        }
    }
}