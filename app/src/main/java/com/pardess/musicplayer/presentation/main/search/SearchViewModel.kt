package com.pardess.musicplayer.presentation.main.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pardess.musicplayer.domain.model.Album
import com.pardess.musicplayer.domain.model.Artist
import com.pardess.musicplayer.domain.model.SearchHistory
import com.pardess.musicplayer.domain.model.SearchType
import com.pardess.musicplayer.domain.model.Song
import com.pardess.musicplayer.domain.repository.SearchRepository
import com.pardess.musicplayer.presentation.home.HomeScreen
import com.pardess.musicplayer.presentation.navigation.Screen
import com.pardess.musicplayer.utils.Utils.getAlbumsFromSongs
import com.pardess.musicplayer.utils.Utils.getArtistsFromSongs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import java.text.Normalizer

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchRepository: SearchRepository
) : ViewModel() {

    fun onEvent(
        event: SearchEvent,
        onPlaybackEvent: (Song, List<Song>) -> Unit = { _, _ -> },
        onNavigateToRoute: (String) -> Unit = { _ -> }
    ) {
        when (event) {
            is SearchEvent.SelectSong -> {
                onPlaybackEvent(event.song, searchUiState.value.searchResult.songs)
                saveSearchHistory(
                    image = event.song.data,
                    text = event.song.title,
                    type = SearchType.SONG
                )
            }

            is SearchEvent.Search -> {
                search(event.searchQuery)
                saveSearchHistory(
                    image = null,
                    text = event.searchQuery,
                    type = SearchType.TEXT
                )
            }

            is SearchEvent.SelectAlbum -> {
                onNavigateToRoute(Screen.DetailArtist.route + "/${event.album.artistId}/${event.album.id}")
                saveSearchHistory(
                    image = event.album.songs.firstOrNull()?.data,
                    text = event.album.title,
                    type = SearchType.ALBUM
                )
            }

            is SearchEvent.SelectArtist -> {
                onNavigateToRoute(HomeScreen.Artist.route + "/${event.artist.id}")
                saveSearchHistory(
                    image = event.artist.songs.firstOrNull()?.data,
                    text = event.artist.name,
                    type = SearchType.ARTIST
                )
            }
        }
    }

    private fun saveSearchHistory(
        image: String?,
        text: String,
        type: SearchType
    ) {
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



    private val allSongs = MutableStateFlow<List<Song>>(emptyList())
    private val songsResult = MutableStateFlow<List<Song>>(emptyList())
    private val artistsResult = MutableStateFlow<List<Artist>>(emptyList())
    private val albumsResult = MutableStateFlow<List<Album>>(emptyList())

    private val _searchUiState = MutableStateFlow(SearchUiState())
    val searchUiState = combine(
        _searchUiState,
        allSongs,
        songsResult,
        artistsResult,
        albumsResult
    ) { baseState, allSongs, songs, artists, albums ->
        if (allSongs.isEmpty()) {
            println("@@@@ allSongs.isEmpty()")
            baseState.copy(
                searchResult = SearchResult(
                    songs = emptyList(),
                    artists = emptyList(),
                    albums = emptyList()
                )
            )
        } else {
            println("@@@@ allSongs: $allSongs")
            baseState.copy(
                allSongs = allSongs,
                searchResult = SearchResult(
                    songs = songs,
                    artists = artists,
                    albums = albums
                )
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = SearchUiState()
    )

    fun setAllSongs(allSongs: List<Song>) {
        _searchUiState.value = _searchUiState.value.copy(
            allSongs = allSongs,
        )
        this.allSongs.value = allSongs // ✅ Flow 값을 직접 변경
    }

    fun setSearchQuery(searchQuery: String) {
        _searchUiState.value = _searchUiState.value.copy(searchQuery = searchQuery)
    }

    fun search(searchQuery: String) {

        _searchUiState.value = _searchUiState.value.copy(searchQuery = searchQuery)

        // 먼저 입력 검색어가 초성만으로 구성되어 있는지 체크
        val useChosungSearch = isChosungOnly(searchQuery)

        val normalizedQuery = normalizeText(searchQuery)
        println("@@@@ normalizedQuery: $normalizedQuery, searchQuery: $searchQuery")

        if (normalizedQuery.isBlank()) {
            songsResult.value = emptyList()
            artistsResult.value = emptyList()
            albumsResult.value = emptyList()
            return
        }

        if (useChosungSearch) {
            // 초성 검색: 대상 문자열을 먼저 정규화한 후 초성 추출하여 검색
            songsResult.value = allSongs.value.asSequence()
                .filter { song ->
                    extractChosung(normalizeText(song.title)).contains(searchQuery)
                }
                .toList()

            artistsResult.value = allSongs.value.asSequence()
                .filter { song ->
                    extractChosung(normalizeText(song.artistName)).contains(searchQuery)
                }
                .toList().getArtistsFromSongs()

            albumsResult.value = allSongs.value.asSequence()
                .filter { song ->
                    extractChosung(normalizeText(song.albumName)).contains(searchQuery)
                }
                .toList().getAlbumsFromSongs()
        } else {
            // 일반 검색: 정규화된 문자열로 검색
            val normalizedQuery = normalizeText(searchQuery)

            songsResult.value = allSongs.value.asSequence()
                .filter { song ->
                    normalizeText(song.title).contains(normalizedQuery)
                }
                .toList()

            artistsResult.value = allSongs.value.asSequence()
                .filter { song ->
                    normalizeText(song.artistName).contains(normalizedQuery)
                }
                .toList().getArtistsFromSongs()

            albumsResult.value = allSongs.value.asSequence()
                .filter { song ->
                    normalizeText(song.albumName).contains(normalizedQuery)
                }
                .toList().getAlbumsFromSongs()
        }

        println("@@@@ songsResult: ${songsResult.value}")
        println("@@@@ artistsResult: ${artistsResult.value}")
        println("@@@@ albumsResult: ${albumsResult.value}")
    }

    // 초성 배열 (한글 호환 자모)
    private val CHOSUNG = arrayOf(
        'ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄸ', 'ㄹ',
        'ㅁ', 'ㅂ', 'ㅃ', 'ㅅ', 'ㅆ', 'ㅇ',
        'ㅈ', 'ㅉ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'
    )

    // 입력 문자열이 초성만으로 이루어졌는지 확인하는 함수
    private fun isChosungOnly(query: String): Boolean {
        val filtered = query.filter { !it.isWhitespace() }
        // 빈 문자열이 아니고, 모든 문자가 CHOSUNG 배열에 포함되어 있으면 초성만 입력된 것으로 판단
        return filtered.isNotEmpty() && filtered.all { it in CHOSUNG }
    }

    // 한글 음절(가~힣)을 초성 문자열로 변환하는 함수
    private fun extractChosung(text: String): String {
        val result = StringBuilder()
        for (c in text) {
            if (c in '\uAC00'..'\uD7A3') { // 가 ~ 힣
                val syllableIndex = c - '\uAC00'
                val choseongIndex = syllableIndex / (21 * 28)
                result.append(CHOSUNG[choseongIndex])
            } else {
                result.append(c)
            }
        }
        return result.toString()
    }

    // 기존 normalizeText 함수 (한글, 영문, 숫자, 공백 정리)
    private fun normalizeText(text: String): String {
        // 1. NFD로 분해 (라틴 악센트 제거 용)
        val nfd = Normalizer.normalize(text, Normalizer.Form.NFD)
        // 2. 결합 다이아크리틱 문자 제거
        val withoutDiacritics = nfd.replace(Regex("\\p{InCombiningDiacriticalMarks}+"), "")
        // 3. NFC로 재구성하여 한글은 완성형으로 복원
        val recomposed = Normalizer.normalize(withoutDiacritics, Normalizer.Form.NFC)
        // 4. 줄바꿈을 띄어쓰기로 변환하고 연속 공백을 하나로 축소
        val withSpaces = recomposed
            .replace(Regex("[\\r\\n]+"), " ")
            .replace(Regex("\\s+"), " ")
        // 5. 유사 문자 변환
        val replaced = withSpaces
            .replace("ä", "a")
            .replace("ö", "o")
            .replace("ü", "u")
            .replace("ß", "ss")
        // 6. 특수문자 제거 (한글, 영문, 숫자, 공백 이외 제거)
        val cleaned = replaced.replace(Regex("[^ㄱ-ㅎ가-힣a-zA-Z0-9\\s]"), "")
        // 7. 앞뒤 공백 제거 및 소문자 변환
        return cleaned.trim().lowercase()
    }
}