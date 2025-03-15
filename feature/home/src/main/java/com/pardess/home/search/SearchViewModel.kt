package com.pardess.home.search

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.pardess.common.Result
import com.pardess.common.Utils.normalizeText
import com.pardess.common.base.BaseUiEffect
import com.pardess.common.base.BaseUiEvent
import com.pardess.common.base.BaseUiState
import com.pardess.common.base.BaseViewModel
import com.pardess.domain.usecase.main.SearchUseCase
import com.pardess.model.Album
import com.pardess.model.Artist
import com.pardess.model.SearchHistory
import com.pardess.model.SearchType
import com.pardess.model.Song
import com.pardess.navigation.HomeScreen
import com.pardess.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

data class SearchUiState(
    val allSongs: List<Song> = emptyList(),
    val searchQuery: String = "",
    val searchResult: Result<SearchResult> = Result.Loading
) : BaseUiState

sealed class SearchUiEffect : BaseUiEffect {
    data class Navigate(val route: String) : SearchUiEffect()
}

sealed class SearchUiEvent : BaseUiEvent {
    data class Search(val query: String) : SearchUiEvent()
    data class SelectSong(val song: Song) : SearchUiEvent()
    data class SelectArtist(val artist: Artist) : SearchUiEvent()
    data class SelectAlbum(val album: Album) : SearchUiEvent()
}

data class SearchResult(
    val songs: List<Song> = emptyList(),
    val artists: List<Artist> = emptyList(),
    val albums: List<Album> = emptyList()
)

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val useCase: SearchUseCase,
    savedStateHandle: SavedStateHandle
) : BaseViewModel<SearchUiState, SearchUiEvent, SearchUiEffect>(SearchUiState()) {

    private val allSongs = MutableStateFlow<List<Song>>(emptyList())
    private val searchResults = MutableStateFlow<Result<SearchResult>>(Result.Loading)

    private var initialSearchQuery =
        savedStateHandle.get<String>("searchQuery") ?: ""

    init {
        viewModelScope.launch {
            combine(allSongs, searchResults) { songs, results ->
                updateState {
                    copy(
                        allSongs = songs,
                        searchQuery = uiState.value.searchQuery.ifBlank { initialSearchQuery },
                        searchResult = results
                    )
                }
            }.launchIn(viewModelScope)
        }
    }

    override fun onEvent(event: SearchUiEvent) {
        when (event) {
            is SearchUiEvent.Search -> {
//                updateState { copy(searchQuery = event.query) }
                search(event.query)
                if (event.query.isNotEmpty()) {
                    saveSearchHistory(null, event.query, SearchType.TEXT)
                }
            }

            is SearchUiEvent.SelectSong -> saveSearchHistory(
                image = event.song.data,
                text = event.song.title,
                type = SearchType.SONG
            )

            is SearchUiEvent.SelectArtist -> {
                saveSearchHistory(
                    image = event.artist.songs.firstOrNull()?.data,
                    text = event.artist.name,
                    type = SearchType.ARTIST
                )
                sendEffect(SearchUiEffect.Navigate(HomeScreen.Artist.route + "/${event.artist.id}"))
            }

            is SearchUiEvent.SelectAlbum -> {
                saveSearchHistory(
                    image = event.album.songs.firstOrNull()?.data,
                    text = event.album.title,
                    type = SearchType.ALBUM
                )
                sendEffect(SearchUiEffect.Navigate(Screen.DetailArtist.route + "/${event.album.artistId}/${event.album.id}"))
            }
        }
    }

    private fun saveSearchHistory(image: String?, text: String, type: SearchType) {
        viewModelScope.launch {
            useCase.saveSearchHistory(
                image = image,
                searchType = type,
                text = text,
            )
        }
    }

    private fun search(query: String) {
        val normalizedQuery = normalizeText(query)
        if (normalizedQuery.isBlank()) {
            searchResults.value = Result.Error("검색어를 입력해주세요.")
            return
        }
        viewModelScope.launch {
            combine(
                useCase.searchSongs(normalizedQuery, allSongs.value),
                useCase.searchArtists(normalizedQuery, allSongs.value),
                useCase.searchAlbums(normalizedQuery, allSongs.value)
            ) { songsResult, artistsResult, albumsResult ->
                if (songsResult is Result.Loading || artistsResult is Result.Loading || albumsResult is Result.Loading) {
                    Result.Loading
                } else {
                    val songs = (songsResult as Result.Success).data
                    val artists = (artistsResult as Result.Success).data
                    val albums = (albumsResult as Result.Success).data
                    Result.Success(SearchResult(songs, artists, albums))
                }
            }.collectLatest { result ->
                searchResults.value = result
            }
        }
    }

    fun setAllSongs(songs: List<Song>) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            allSongs.value = songs
            if (songs.isNotEmpty() && initialSearchQuery.isNotBlank()) {
                search(initialSearchQuery)
            }
        }
    }
}