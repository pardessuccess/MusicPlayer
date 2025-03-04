package com.pardess.musicplayer.presentation.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

abstract class BaseViewModel<State : BaseUiState, Event : BaseUiEvent, Effect : BaseUiEffect>(
    initialState: State
) : ViewModel() {

    // 🔹 UI State 관리
    private val _uiState = MutableStateFlow(initialState)
    val uiState: StateFlow<State> = _uiState.asStateFlow()

    // 🔹 Effect 처리
    private val _effectChannel = Channel<Effect>(Channel.BUFFERED)
    val effectFlow = _effectChannel.receiveAsFlow()

    protected fun updateState(reducer: State.() -> State) {
        _uiState.value = _uiState.value.reducer()
    }

    abstract fun onEvent(event: Event)

    protected fun sendEffect(effect: Effect) {
        viewModelScope.launch {
            _effectChannel.send(effect)
        }
    }

    protected fun <T> collectFlow(flow: Flow<T>, collector: suspend (T) -> Unit) {
        viewModelScope.launch {
            flow.collectLatest { value ->
                collector(value)
            }
        }
    }

    protected fun <T> collectState(flow: StateFlow<T>, update: State.(T) -> State) {
        viewModelScope.launch {
            flow.collectLatest { value ->
                _uiState.update { it.update(value) }
            }
        }
    }

}