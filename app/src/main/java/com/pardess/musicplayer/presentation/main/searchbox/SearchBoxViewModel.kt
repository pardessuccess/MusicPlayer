package com.pardess.musicplayer.presentation.main.searchbox

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pardess.musicplayer.domain.model.SearchHistory
import com.pardess.musicplayer.domain.model.SearchType
import com.pardess.musicplayer.domain.repository.SearchRepository
import com.pardess.musicplayer.presentation.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchBoxViewModel @Inject constructor(
    private val searchRepository: SearchRepository
) : ViewModel() {

    private var _uiState = MutableStateFlow(SearchBoxState())
    val uiState = _uiState.asStateFlow()

    fun onEvent(
        event: SearchBoxEvent,
        onNavigateToRoute: (String) -> Unit = { _ -> }
    ) {
        when (event) {
            is SearchBoxEvent.Search -> {
                _uiState.value = _uiState.value.copy(searchQuery = event.searchQuery)
                viewModelScope.launch {
                    searchRepository.saveSearchHistory(
                        searchHistory = SearchHistory(
                            id = 0,
                            type = SearchType.TEXT,
                            image = null,
                            text = event.searchQuery,
                            timestamp = System.currentTimeMillis()
                        )
                    )
                }
                onEvent(SearchBoxEvent.Shrink)
                onNavigateToRoute(Screen.Search.route)
            }

            is SearchBoxEvent.Expand -> {
                _uiState.value = _uiState.value.copy(expand = true)
            }

            is SearchBoxEvent.Shrink -> {
                _uiState.value = _uiState.value.copy(expand = false)
            }
        }
    }
}