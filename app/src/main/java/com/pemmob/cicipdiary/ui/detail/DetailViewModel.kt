package com.pemmob.cicipdiary.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.pemmob.cicipdiary.data.repository.MealRepository
import com.pemmob.cicipdiary.data.repository.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for Detail Screen.
 * Fetches meal detail by ID passed from navigation arguments.
 */
class DetailViewModel(
    private val repository: MealRepository,
    private val mealId: String
) : ViewModel() {

    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    init {
        fetchDetail()
    }

    fun retry() {
        fetchDetail()
    }

    private fun fetchDetail() {
        viewModelScope.launch {
            _uiState.value = DetailUiState.Loading
            when (val result = repository.getMealDetail(mealId)) {
                is Result.Success -> {
                    if (result.data != null) {
                        _uiState.value = DetailUiState.Success(result.data)
                    } else {
                        _uiState.value = DetailUiState.Error("Meal not found")
                    }
                }
                is Result.Error -> {
                    _uiState.value = DetailUiState.Error(result.message)
                }
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(
        private val repository: MealRepository,
        private val mealId: String
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return DetailViewModel(repository, mealId) as T
        }
    }
}
