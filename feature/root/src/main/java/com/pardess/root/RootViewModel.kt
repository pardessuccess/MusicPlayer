package com.pardess.root

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pardess.common.base.BaseUiEffect
import com.pardess.common.base.BaseUiEvent
import com.pardess.common.base.BaseUiState
import com.pardess.common.base.BaseViewModel
import com.pardess.navigation.HomeScreen
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

sealed class RootUiEffect : BaseUiEffect {
    data class NavigateBottom(val route: String) : RootUiEffect()
}

data class RootUiState(
    val bottomBarExpand: Boolean = true,
    val bottomBarHeight: Dp = 100.dp,
    val currentRoute: String = HomeScreen.Main.route,
    val searchBoxExpand: Boolean = false,
    val selectedBottomIndex: Int = 0
) : BaseUiState

sealed class RootUiEvent : BaseUiEvent {
    object BottomBarExpand : RootUiEvent()
    object BottomBarShrink : RootUiEvent()
    data class CurrentRoute(val route: String) : RootUiEvent()
    data class NavigateBottomBar(val route: String) : RootUiEvent()
    object SearchBoxExpand : RootUiEvent()
    object SearchBoxShrink : RootUiEvent()
    data class BottomBarSelect(val index: Int) : RootUiEvent()
}

@HiltViewModel
class HomeViewModel @Inject constructor() :
    BaseViewModel<RootUiState, RootUiEvent, RootUiEffect>(RootUiState()) {

    override fun onEvent(event: RootUiEvent) {
        when (event) {
            RootUiEvent.BottomBarExpand -> {
                updateState {
                    copy(bottomBarExpand = true, bottomBarHeight = 100.dp)
                }
            }

            RootUiEvent.BottomBarShrink -> {
                updateState {
                    copy(bottomBarExpand = false, bottomBarHeight = 0.dp)
                }
            }

            is RootUiEvent.NavigateBottomBar -> {
                sendEffect(RootUiEffect.NavigateBottom(event.route))
                updateState { copy(currentRoute = event.route) }
            }

            RootUiEvent.SearchBoxExpand -> {
                updateState { copy(searchBoxExpand = true) }
            }

            RootUiEvent.SearchBoxShrink -> {
                updateState { copy(searchBoxExpand = false) }
            }

            is RootUiEvent.CurrentRoute -> {
                updateState { copy(currentRoute = event.route) }
            }

            is RootUiEvent.BottomBarSelect -> {
                updateState { copy(selectedBottomIndex = event.index) }
            }
        }
    }
}