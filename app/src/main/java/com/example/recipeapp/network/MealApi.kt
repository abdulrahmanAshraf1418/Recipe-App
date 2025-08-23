package com.example.recipeapp.network

import com.example.recipeapp.models.MealResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface MealApi {

    @GET("random.php")
    fun getRandomMeal(): Call<MealResponse>

    @GET("search.php")
    fun getMealsByFirstLetter(
        @Query("f") letter: String
    ): Call<MealResponse>
}
