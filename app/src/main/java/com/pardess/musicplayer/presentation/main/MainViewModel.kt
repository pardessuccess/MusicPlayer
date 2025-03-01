package com.pardess.musicplayer.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pardess.musicplayer.domain.model.Album
import com.pardess.musicplayer.domain.model.Artist
import com.pardess.musicplayer.domain.model.SearchHistory
import com.pardess.musicplayer.domain.model.SearchType
import com.pardess.musicplayer.domain.repository.ManageRepository
import com.pardess.musicplayer.domain.repository.SearchRepository
import com.pardess.musicplayer.presentation.navigation.Screen
import com.pardess.musicplayer.presentation.toSong
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    manageRepository: ManageRepository,
    private val searchRepository: SearchRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState = _uiState.asStateFlow()

    private val _effectChannel = Channel<MainUiEffect>(Channel.BUFFERED)
    val effectFlow = _effectChannel.receiveAsFlow()

    fun onEvent(event: MainUiEvent) {
        when (event) {
            is MainUiEvent.Navigate -> {
                viewModelScope.launch {
                    _effectChannel.send(MainUiEffect.Navigate(event.route))
                }
            }
            is MainUiEvent.RemoveSearchHistory -> {
                viewModelScope.launch {
                    searchRepository.deleteSearchHistory(event.id)
                }
            }
        }
    }

    private val favoriteSongs = manageRepository.getFavoriteSongs().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        emptyList()
    )

    val searchHistories = searchRepository.getSearchHistory().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        emptyList()
    )

    val popularArtists = favoriteSongs
        .map { favorites ->
            favorites.groupBy { it.song.artistId }
                .map { (artistId, artistFavorites) ->
                    // 해당 아티스트의 총 즐겨찾기 수 계산 (null이면 0으로 처리)
                    val totalFavorites = artistFavorites.sumOf { it.favoriteCount ?: 0 }
                    // 아티스트 이름 (모든 항목은 동일하다고 가정)
                    val artistName = artistFavorites.first().song.artistName

                    // 해당 아티스트의 앨범 목록 생성 (앨범 아이디별 그룹화)
                    val albums = artistFavorites.groupBy { it.song.albumId }
                        .map { (albumId, albumFavorites) ->
                            Album(
                                id = albumId,
                                title = albumFavorites.first().song.albumName,
                                artistId = artistId,
                                artistName = artistName,
                                year = albumFavorites.first().song.year,
                                songCount = albumFavorites.size,
                                songs = albumFavorites.map { it.song.toSong() }
                            )
                        }
                    // 도메인 모델 Artist 생성
                    Artist(
                        id = artistId,
                        name = artistName,
                        albums = albums,
                        songs = artistFavorites.map { it.song.toSong() }
                    ) to totalFavorites
                }
                .sortedByDescending { it.second }
                .map { it.first }
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val popularAlbums = favoriteSongs
        .map { favorites ->
            favorites.groupBy { it.song.albumId }
                .map { (albumId, albumFavorites) ->
                    val totalFavorites = albumFavorites.sumOf { it.favoriteCount ?: 0 }
                    Album(
                        id = albumId,
                        title = albumFavorites.first().song.albumName,
                        artistId = albumFavorites.first().song.artistId,
                        artistName = albumFavorites.first().song.artistName,
                        year = albumFavorites.first().song.year,
                        songCount = albumFavorites.size,
                        songs = albumFavorites.map { it.song.toSong() }
                    ) to totalFavorites
                }
                .sortedByDescending { it.second }
                .map { it.first }
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())


    private fun loadInfo() {
        viewModelScope.launch {
            favoriteSongs.collectLatest { songs ->
                _uiState.update { currentState ->
                    currentState.copy(
                        favoriteSongs = songs,
                        song1st = if (songs.isNotEmpty()) songs[0].song else null,
                        song2nd = if (songs.size > 1) songs[1].song else null,
                        song3rd = if (songs.size > 2) songs[2].song else null,
                    )
                }
            }
        }

        viewModelScope.launch {
            popularArtists.collectLatest { artistsList ->
                _uiState.update { currentState ->
                    if (artistsList.isNotEmpty()) {
                        val min = minOf(artistsList.size - 1, 9)
                        currentState.copy(popularArtists = artistsList.slice(0..min))
                    } else {
                        currentState.copy(popularArtists = artistsList)
                    }
                }
            }
        }

        viewModelScope.launch {
            popularAlbums.collectLatest { albumsList ->
                _uiState.update { currentState ->
                    if (currentState.popularAlbums.isNotEmpty()) {
                        val min = minOf(albumsList.size - 1, 9)
                        currentState.copy(popularAlbums = albumsList.slice(0..min))
                    } else {
                        currentState.copy(popularAlbums = albumsList)
                    }
                }
            }
        }

        viewModelScope.launch {
            searchHistories.collectLatest { searchHistories ->
                _uiState.update { currentState ->
                    currentState.copy(searchHistories = searchHistories)
                }
            }
        }
    }


    init {
        loadInfo()
    }


}