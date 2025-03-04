package com.pardess.musicplayer.presentation.main

import androidx.lifecycle.viewModelScope
import com.pardess.musicplayer.data.entity.SongEntity
import com.pardess.musicplayer.data.entity.join.FavoriteSong
import com.pardess.musicplayer.domain.model.Album
import com.pardess.musicplayer.domain.model.Artist
import com.pardess.musicplayer.domain.model.SearchHistory
import com.pardess.musicplayer.domain.repository.ManageRepository
import com.pardess.musicplayer.domain.repository.SearchRepository
import com.pardess.musicplayer.presentation.base.BaseViewModel
import com.pardess.musicplayer.presentation.base.BaseUiEffect
import com.pardess.musicplayer.presentation.base.BaseUiEvent
import com.pardess.musicplayer.presentation.base.BaseUiState
import com.pardess.musicplayer.presentation.toSong
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
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
    val showGuideText: Boolean = false
) : BaseUiState

sealed class MainUiEvent : BaseUiEvent {
    data class Navigate(val route: String) : MainUiEvent()
    data class RemoveSearchHistory(val id: Long) : MainUiEvent()
}

sealed class MainUiEffect : BaseUiEffect {
    data class Navigate(val route: String) : MainUiEffect()
}

@HiltViewModel
class MainViewModel @Inject constructor(
    manageRepository: ManageRepository,
    private val searchRepository: SearchRepository
) : BaseViewModel<MainUiState, MainUiEvent, MainUiEffect>(MainUiState()) {

    override fun onEvent(event: MainUiEvent) {
        when (event) {
            is MainUiEvent.Navigate -> {
                sendEffect(MainUiEffect.Navigate(event.route))
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

    private val searchHistories = searchRepository.getSearchHistory().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        emptyList()
    )

    private val popularArtists = favoriteSongs
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