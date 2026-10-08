package com.pemmob.cicipdiary.ui.home

import com.pemmob.cicipdiary.data.model.Meal

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Success(val meals: List<Meal>) : HomeUiState
    data object Empty : HomeUiState
    data class Error(val message: String) : HomeUiState
}
