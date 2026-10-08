package com.pemmob.cicipdiary.data.repository

import com.pemmob.cicipdiary.data.model.Meal
import com.pemmob.cicipdiary.data.remote.MealApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Result wrapper for repository operations.
 */
sealed interface Result<out T> {
    data class Success<T>(val data: T) : Result<T>
    data class Error(val message: String) : Result<Nothing>
}

/**
 * Repository for fetching meal data. Handles network errors.
 */
class MealRepository(private val apiService: MealApiService) {

    suspend fun searchMeals(query: String): Result<List<Meal>?> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.searchMeals(query)
            Result.Success(response.meals)
        } catch (e: Exception) {
            Result.Error(e.localizedMessage ?: "Unknown error occurred")
        }
    }

    suspend fun getMealDetail(id: String): Result<Meal?> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getMealDetail(id)
            Result.Success(response.meals?.firstOrNull())
        } catch (e: Exception) {
            Result.Error(e.localizedMessage ?: "Unknown error occurred")
        }
    }
}
