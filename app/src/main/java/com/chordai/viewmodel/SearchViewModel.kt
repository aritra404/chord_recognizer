package com.chordai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chordai.data.api.YouTubeResult
import com.chordai.data.repository.ChordRepository
import com.chordai.ui.util.UiState
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SearchViewModel : ViewModel() {
    private val repository = ChordRepository()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _uiState = MutableStateFlow<UiState<List<YouTubeResult>>>(UiState.Idle)
    val uiState: StateFlow<UiState<List<YouTubeResult>>> = _uiState

    private var searchJob: Job? = null

    @OptIn(FlowPreview::class)
    fun onQueryChanged(query: String) {
        _searchQuery.value = query
        searchJob?.cancel()
        
        if (query.isBlank()) {
            _uiState.value = UiState.Idle
            return
        }

        searchJob = viewModelScope.launch {
            delay(500) // Debounce
            performSearch(query)
        }
    }

    fun performSearch(query: String? = null) {
        val q = query ?: _searchQuery.value
        if (q.isBlank()) return

        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val results = repository.search(q)
                if (results.isEmpty()) {
                    _uiState.value = UiState.Error("No results found for '$q'")
                } else {
                    _uiState.value = UiState.Success(results)
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Network error. Make sure the server is online.")
            }
        }
    }
}
