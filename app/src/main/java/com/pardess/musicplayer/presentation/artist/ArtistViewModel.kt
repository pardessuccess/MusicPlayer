package com.pardess.musicplayer.presentation.artist

import com.pardess.musicplayer.domain.model.Artist
import com.pardess.musicplayer.domain.model.Song
import com.pardess.musicplayer.presentation.base.BaseUiEffect
import com.pardess.musicplayer.presentation.base.BaseUiEvent
import com.pardess.musicplayer.presentation.base.BaseUiState
import com.pardess.musicplayer.presentation.base.BaseViewModel
import com.pardess.musicplayer.utils.Utils.getArtistsFromSongs
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

sealed class ArtistEffect : BaseUiEffect {
    data class SelectArtist(val artistId: Long) : ArtistEffect()
}

data class ArtistUiState(
    val artists: List<Artist> = emptyList()
) : BaseUiState

sealed class ArtistUiEvent : BaseUiEvent {
    data class LoadArtists(val songs: List<Song>) : ArtistUiEvent()
    data class SelectArtist(val artistId: Long) : ArtistUiEvent()
}

@HiltViewModel
class ArtistViewModel @Inject constructor() : BaseViewModel<ArtistUiState, ArtistUiEvent, ArtistEffect>(ArtistUiState()) {

    override fun onEvent(event: ArtistUiEvent) {
        when (event) {
            is ArtistUiEvent.LoadArtists -> {
                updateState { copy(artists = event.songs.getArtistsFromSongs()) }
            }

            is ArtistUiEvent.SelectArtist -> {
                sendEffect(ArtistEffect.SelectArtist(event.artistId))
            }
        }
    }
}
