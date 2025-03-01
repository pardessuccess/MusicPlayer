package com.pardess.musicplayer.presentation.home

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {

    val uiState = MutableStateFlow(HomeUiState())

    fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.BottomBarExpand -> {
                uiState.value = uiState.value.copy(bottomBarExpand = true)
            }

            is HomeEvent.BottomBarShrink -> {
                uiState.value = uiState.value.copy(bottomBarExpand = false)

            }

            is HomeEvent.OnNavigateToRoute -> {
                uiState.value = uiState.value.copy(currentRoute = event.route)
            }

            HomeEvent.SearchSectionExpand -> {
                uiState.value = uiState.value.copy(searchBoxExpand = true)
            }
            HomeEvent.SearchSectionShrink -> {
                uiState.value = uiState.value.copy(searchBoxExpand = false)
            }
        }
    }
}