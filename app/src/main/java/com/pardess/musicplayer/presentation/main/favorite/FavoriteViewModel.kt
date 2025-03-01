package com.pardess.musicplayer.presentation.main.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pardess.musicplayer.data.entity.join.FavoriteSong
import com.pardess.musicplayer.domain.repository.ManageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val manageRepository: ManageRepository
) : ViewModel() {

    // 내부 상태를 불변 객체로 관리
    private val _uiState = MutableStateFlow(FavoriteUiState())
    val uiState = _uiState.asStateFlow()

    private val _effectChannel = Channel<FavoriteUiEffect>(Channel.BUFFERED)
    val effectFlow = _effectChannel.receiveAsFlow()

    private var currentFavorite: FavoriteSong? = null

    init {
        viewModelScope.launch {
            manageRepository.getFavoriteSongs().collect { favoriteSongs ->
                _uiState.update { currentState ->
                    currentState.copy(favoriteSongs = favoriteSongs)
                }
            }
        }
    }

    fun onEvent(event: FavoriteUiEvent) {
        when (event) {
            is FavoriteUiEvent.RemoveFavorite -> {
                removeFavorite()
            }

            FavoriteUiEvent.DismissRemoveDialog -> {
                currentFavorite = null
                viewModelScope.launch {
                    _effectChannel.send(FavoriteUiEffect.DismissRemoveDialog)
                }
            }

            is FavoriteUiEvent.ShowRemoveDialog -> {
                currentFavorite = event.favoriteSong
                viewModelScope.launch {
                    _effectChannel.send(FavoriteUiEffect.ShowRemoveDialog(event.favoriteSong))
                }
            }
        }
    }

    private fun removeFavorite() {
        val favoriteSong = currentFavorite ?: return
        viewModelScope.launch {
            manageRepository.removeFavorite(favoriteSong.song.id)
            currentFavorite = null
            _effectChannel.send(FavoriteUiEffect.FavoriteUiRemoved)
        }
    }


}