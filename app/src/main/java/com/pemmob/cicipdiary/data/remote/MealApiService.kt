package com.pemmob.cicipdiary.data.remote

import com.pemmob.cicipdiary.data.model.MealResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface MealApiService {
    @GET("search.php")
    suspend fun searchMeals(@Query("s") query: String): MealResponse

    @GET("lookup.php")
    suspend fun getMealDetail(@Query("i") id: String): MealResponse
}
