package com.pardess.musicplayer.presentation.main.search

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.pardess.musicplayer.domain.model.Album
import com.pardess.musicplayer.domain.model.Artist
import com.pardess.musicplayer.domain.model.SearchHistory
import com.pardess.musicplayer.domain.model.SearchType
import com.pardess.musicplayer.domain.model.Song
import com.pardess.musicplayer.domain.usecase.main.MainUseCase
import com.pardess.musicplayer.domain.usecase.main.SearchUseCase
import com.pardess.musicplayer.presentation.Status
import com.pardess.musicplayer.presentation.base.BaseUiEffect
import com.pardess.musicplayer.presentation.base.BaseUiEvent
import com.pardess.musicplayer.presentation.base.BaseUiState
import com.pardess.musicplayer.presentation.base.BaseViewModel
import com.pardess.musicplayer.presentation.home.HomeScreen
import com.pardess.musicplayer.presentation.navigation.Screen
import com.pardess.musicplayer.utils.Utils.extractChosung
import com.pardess.musicplayer.utils.Utils.getAlbumsFromSongs
import com.pardess.musicplayer.utils.Utils.getArtistsFromSongs
import com.pardess.musicplayer.utils.Utils.isChosungOnly
import com.pardess.musicplayer.utils.Utils.normalizeText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
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
    val searchResult: Status<SearchResult> = Status.Loading
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
    private val searchResults = MutableStateFlow<Status<SearchResult>>(Status.Loading)

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
                updateState { copy(searchQuery = event.query) }
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
                SearchHistory(
                    id = 0,
                    image = image,
                    text = text,
                    type = type,
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }

    private fun search(query: String) {
        val normalizedQuery = normalizeText(query)
        if (normalizedQuery.isBlank()) {
            searchResults.value = Status.Error("검색어를 입력해주세요.")
            return
        }
        viewModelScope.launch {
            combine(
                useCase.searchSongs(normalizedQuery, allSongs.value),
                useCase.searchArtists(normalizedQuery, allSongs.value),
                useCase.searchAlbums(normalizedQuery, allSongs.value)
            ) { songsResult, artistsResult, albumsResult ->
                if (songsResult is Status.Loading || artistsResult is Status.Loading || albumsResult is Status.Loading) {
                    Status.Loading
                } else {
                    val songs = (songsResult as Status.Success).data
                    val artists = (artistsResult as Status.Success).data
                    val albums = (albumsResult as Status.Success).data
                    Status.Success(SearchResult(songs, artists, albums))
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