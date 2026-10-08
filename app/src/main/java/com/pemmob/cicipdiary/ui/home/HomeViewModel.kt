package com.pemmob.cicipdiary.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.pemmob.cicipdiary.data.repository.MealRepository
import com.pemmob.cicipdiary.data.repository.Result
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

/**
 * ViewModel for Home Screen.
 * Uses StateFlow to manage UI state and handles debounced search query.
 */
@OptIn(FlowPreview::class)
class HomeViewModel(private val repository: MealRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("cake") // Default query
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    init {
        viewModelScope.launch {
            _searchQuery
                .debounce(500L)
                .distinctUntilChanged()
                .collectLatest { query ->
                    performSearch(query.ifBlank { "cake" })
                }
        }
    }

    fun onQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun retry() {
        performSearch(_searchQuery.value.ifBlank { "cake" })
    }

    private fun performSearch(query: String) {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            when (val result = repository.searchMeals(query)) {
                is Result.Success -> {
                    if (result.data.isNullOrEmpty()) {
                        _uiState.value = HomeUiState.Empty
                    } else {
                        _uiState.value = HomeUiState.Success(result.data)
                    }
                }
                is Result.Error -> {
                    _uiState.value = HomeUiState.Error(result.message)
                }
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(private val repository: MealRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return HomeViewModel(repository) as T
        }
    }
}
