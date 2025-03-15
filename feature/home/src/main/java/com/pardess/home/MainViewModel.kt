package com.pardess.home

import androidx.lifecycle.viewModelScope
import com.pardess.common.base.BaseUiEffect
import com.pardess.common.base.BaseUiEvent
import com.pardess.common.base.BaseUiState
import com.pardess.common.base.BaseViewModel
import com.pardess.domain.usecase.main.HomeUseCase
import com.pardess.model.Album
import com.pardess.model.Artist
import com.pardess.model.SearchHistory
import com.pardess.model.SearchType
import com.pardess.model.Song
import com.pardess.model.join.FavoriteSong
import com.pardess.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MainUiState(
    val song1st: Song? = null,
    val song2nd: Song? = null,
    val song3rd: Song? = null,
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
    private val useCase: HomeUseCase,
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
                    useCase.saveSearchHistory(
                        image = null,
                        text = event.query,
                        type = SearchType.TEXT
                    )
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