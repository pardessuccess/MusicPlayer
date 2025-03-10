package com.pardess.home.favorite

import androidx.lifecycle.viewModelScope
import com.pardess.common.base.BaseUiEffect
import com.pardess.common.base.BaseUiEvent
import com.pardess.common.base.BaseUiState
import com.pardess.common.base.BaseViewModel
import com.pardess.domain.usecase.main.MainDetailUseCase
import com.pardess.model.join.FavoriteSong
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
    private val useCase: MainDetailUseCase
) : BaseViewModel<FavoriteUiState, FavoriteUiEvent, FavoriteUiEffect>(FavoriteUiState()) {

    private val favoriteSongs = useCase.getFavoriteSongs().stateIn(
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
                val favoriteSong = uiState.value.selectedFavoriteSong ?: return
                viewModelScope.launch {
                    useCase.deleteFavoriteSong(favoriteSong.song.id)
                    updateState { copy(showRemoveDialog = false, selectedFavoriteSong = null) }
                    sendEffect(FavoriteUiEffect.FavoriteDelete)
                }
            }
        }
    }
}
