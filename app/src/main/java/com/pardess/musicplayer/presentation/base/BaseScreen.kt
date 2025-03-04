package com.pardess.musicplayer.presentation.base

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.collectLatest

@Composable
fun <State : BaseUiState, Event : BaseUiEvent, Effect : BaseUiEffect, VM : BaseViewModel<State, Event, Effect>> BaseScreen(
    viewModel: VM,
    onEffect: (Effect) -> Unit,
    content: @Composable (State, (Event) -> Unit) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel.effectFlow) {
        viewModel.effectFlow.collectLatest { effect ->
            onEffect(effect)
        }
    }

    content(uiState) { event -> viewModel.onEvent(event) }
}

/** 🔹 일반적인 Screen (MVI 미사용) */
@Composable
fun BaseScreen(
    content: @Composable () -> Unit
) {
    content()
}

/** 🔹 ViewModel을 사용하는 일반 Screen */
@Composable
fun <VM : ViewModel> BaseScreen(
    viewModel: VM,
    content: @Composable () -> Unit
) {
    content()
}