package com.pardess.musicplayer.presentation.artist

import androidx.lifecycle.ViewModel
import com.pardess.musicplayer.domain.model.Artist
import com.pardess.musicplayer.domain.model.Song
import com.pardess.musicplayer.utils.Utils.getArtistsFromSongs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ArtistViewModel @Inject constructor() : ViewModel() {

    private val _artistState = MutableStateFlow<List<Artist>>(emptyList())
    val artistState = _artistState.asStateFlow()

    fun setArtists(songs: List<Song>) {
        songs.getArtistsFromSongs().let { artists ->
            _artistState.value = artists
        }
    }

}