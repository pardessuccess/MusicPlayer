package com.pardess.musicplayer.presentation.home

import androidx.compose.ui.unit.dp
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

import androidx.compose.ui.unit.Dp
import com.pardess.musicplayer.presentation.base.BaseUiEffect
import com.pardess.musicplayer.presentation.base.BaseUiEvent
import com.pardess.musicplayer.presentation.base.BaseUiState
import com.pardess.musicplayer.presentation.base.BaseViewModel

sealed class HomeUiEffect : BaseUiEffect {
    data class NavigateBottom(val route: String) : HomeUiEffect()
}

data class HomeUiState(
    val bottomBarExpand: Boolean = true,
    val bottomBarHeight: Dp = 100.dp,
    val currentRoute: String = HomeScreen.Main.route,
    val searchBoxExpand: Boolean = false,
    val selectedBottomIndex: Int = 0
) : BaseUiState

sealed class HomeUiEvent : BaseUiEvent {
    object BottomBarExpand : HomeUiEvent()
    object BottomBarShrink : HomeUiEvent()
    data class CurrentRoute(val route: String) : HomeUiEvent()
    data class NavigateBottomBar(val route: String) : HomeUiEvent()
    object SearchBoxExpand : HomeUiEvent()
    object SearchBoxShrink : HomeUiEvent()
    data class BottomBarSelect(val index: Int) : HomeUiEvent()
}

@HiltViewModel
class HomeViewModel @Inject constructor() :
    BaseViewModel<HomeUiState, HomeUiEvent, HomeUiEffect>(HomeUiState()) {

    override fun onEvent(event: HomeUiEvent) {
        when (event) {
            HomeUiEvent.BottomBarExpand -> {
                updateState {
                    copy(bottomBarExpand = true, bottomBarHeight = 100.dp)
                }
            }

            HomeUiEvent.BottomBarShrink -> {
                updateState {
                    copy(bottomBarExpand = false, bottomBarHeight = 0.dp)
                }
            }

            is HomeUiEvent.NavigateBottomBar -> {
                sendEffect(HomeUiEffect.NavigateBottom(event.route))
                updateState { copy(currentRoute = event.route) }
            }

            HomeUiEvent.SearchBoxExpand -> {
                updateState { copy(searchBoxExpand = true) }
            }

            HomeUiEvent.SearchBoxShrink -> {
                updateState { copy(searchBoxExpand = false) }
            }

            is HomeUiEvent.CurrentRoute -> {
                updateState { copy(currentRoute = event.route) }
            }

            is HomeUiEvent.BottomBarSelect -> {
                updateState { copy(selectedBottomIndex = event.index) }
            }
        }
    }
}