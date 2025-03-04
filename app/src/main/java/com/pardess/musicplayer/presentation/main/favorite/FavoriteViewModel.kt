package com.pardess.musicplayer.presentation.main.favorite

import androidx.lifecycle.viewModelScope
import com.pardess.musicplayer.data.entity.join.FavoriteSong
import com.pardess.musicplayer.domain.repository.ManageRepository
import com.pardess.musicplayer.presentation.base.BaseUiEffect
import com.pardess.musicplayer.presentation.base.BaseUiEvent
import com.pardess.musicplayer.presentation.base.BaseUiState
import com.pardess.musicplayer.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FavoriteUiState(
    val favoriteSongs: List<FavoriteSong> = emptyList(),
    val selectedFavoriteSong: FavoriteSong? = null,
    val showRemoveDialog: Boolean = false,
) : BaseUiState

sealed class FavoriteUiEvent : BaseUiEvent {
    data class ShowRemoveDialog(val favoriteSong: FavoriteSong) : FavoriteUiEvent()
    object DismissRemoveDialog : FavoriteUiEvent()
    object RemoveFavorite : FavoriteUiEvent()
}

sealed class FavoriteUiEffect : BaseUiEffect {
    object FavoriteDelete : FavoriteUiEffect()
}

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val manageRepository: ManageRepository
) : BaseViewModel<FavoriteUiState, FavoriteUiEvent, FavoriteUiEffect>(FavoriteUiState()) {

    private val favoriteSongs = manageRepository.getFavoriteSongs().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        emptyList()
    )

    init {
        collectState(favoriteSongs) { favoriteSongs ->
            copy(favoriteSongs = favoriteSongs)
        }
    }

    override fun onEvent(event: FavoriteUiEvent) {
        when (event) {
            is FavoriteUiEvent.ShowRemoveDialog -> {
                updateState {
                    copy(
                        showRemoveDialog = true,
                        selectedFavoriteSong = event.favoriteSong
                    )
                }
            }

            FavoriteUiEvent.DismissRemoveDialog -> {
                updateState { copy(showRemoveDialog = false, selectedFavoriteSong = null) }
            }

            FavoriteUiEvent.RemoveFavorite -> {
                removeFavorite()
            }
        }
    }

    private fun removeFavorite() {
        val favoriteSong = uiState.value.selectedFavoriteSong ?: return
        viewModelScope.launch {
            manageRepository.removeFavorite(favoriteSong.song.id)
            updateState { copy(showRemoveDialog = false, selectedFavoriteSong = null) }
            sendEffect(FavoriteUiEffect.FavoriteDelete)
        }
    }
}
