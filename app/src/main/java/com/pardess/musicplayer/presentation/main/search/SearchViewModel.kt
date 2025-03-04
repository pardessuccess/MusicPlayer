package com.pardess.musicplayer.presentation.main.search

import androidx.lifecycle.viewModelScope
import com.pardess.musicplayer.domain.model.Album
import com.pardess.musicplayer.domain.model.Artist
import com.pardess.musicplayer.domain.model.SearchHistory
import com.pardess.musicplayer.domain.model.SearchType
import com.pardess.musicplayer.domain.model.Song
import com.pardess.musicplayer.domain.repository.SearchRepository
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch
import javax.inject.Inject


data class SearchUiState(
    val allSongs: List<Song> = emptyList(),
    val searchQuery: String = "",
    val searchResult: SearchResult = SearchResult()
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
    private val searchRepository: SearchRepository
) : BaseViewModel<SearchUiState, SearchUiEvent, SearchUiEffect>(SearchUiState()) {

    private val allSongs = MutableStateFlow<List<Song>>(emptyList())
    private val searchResults = MutableStateFlow(SearchResult())

    init {
        viewModelScope.launch {
            combine(uiState, allSongs, searchResults) { baseState, allSongs, results ->
                baseState.copy(
                    allSongs = allSongs,
                    searchResult = results
                )
            }.launchIn(viewModelScope)
        }
    }

    override fun onEvent(event: SearchUiEvent) {
        when (event) {
            is SearchUiEvent.Search -> {
                search(event.query)
                saveSearchHistory(null, event.query, SearchType.TEXT)
            }

            is SearchUiEvent.SelectSong -> saveSearchHistory(
                image = event.song.data,
                event.song.title,
                SearchType.SONG
            )

            is SearchUiEvent.SelectArtist -> {
                saveSearchHistory(
                    image = event.artist.songs.firstOrNull()?.data,
                    event.artist.name,
                    SearchType.ARTIST
                )
                sendEffect(SearchUiEffect.Navigate(HomeScreen.Artist.route + "/${event.artist.id}"))
            }

            is SearchUiEvent.SelectAlbum -> {
                saveSearchHistory(
                    image = event.album.songs.firstOrNull()?.data,
                    event.album.title, SearchType.ALBUM
                )
                sendEffect(SearchUiEffect.Navigate(Screen.DetailArtist.route + "/${event.album.artistId}/${event.album.id}"))
            }
        }
    }

    private fun saveSearchHistory(image: String?, text: String, type: SearchType) {
        viewModelScope.launch {
            searchRepository.saveSearchHistory(
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

    fun search(query: String) {
        val normalizedQuery = normalizeText(query)
        val useChosungSearch = isChosungOnly(query)

        if (normalizedQuery.isBlank()) {
            searchResults.value = SearchResult()
            return
        }

        val filteredSongs = allSongs.value.filter {
            val title = normalizeText(it.title)
            if (useChosungSearch) extractChosung(title).contains(query)
            else title.contains(normalizedQuery)
        }

        val filteredArtists = allSongs.value
            .filter { normalizeText(it.artistName).contains(normalizedQuery) }
            .getArtistsFromSongs()

        val filteredAlbums = allSongs.value
            .filter { normalizeText(it.albumName).contains(normalizedQuery) }
            .getAlbumsFromSongs()

        searchResults.value = SearchResult(
            songs = filteredSongs,
            artists = filteredArtists,
            albums = filteredAlbums
        )
    }

    private fun setSearchQuery(query: String) {
        updateState {
            uiState.value.copy(searchQuery = query)
        }
    }

    fun initSearch(query: String, songs: List<Song>) {
        setSearchQuery(query)
        allSongs.value = songs
        println(allSongs.value)
        search(uiState.value.searchQuery)
    }
}