package com.pemmob.cicipdiary.ui.detail

import com.pemmob.cicipdiary.data.model.Meal

sealed interface DetailUiState {
    data object Loading : DetailUiState
    data class Success(val meal: Meal) : DetailUiState
    data class Error(val message: String) : DetailUiState
}
