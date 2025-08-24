package com.example.recipeapp.network

import com.example.recipeapp.models.MealResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface MealApi {
    @GET("random.php")
    suspend fun getRandomMeal(): MealResponse

    @GET("search.php")
    suspend fun getMealsByFirstLetter(
        @Query("f") letter: String
    ): MealResponse
}
