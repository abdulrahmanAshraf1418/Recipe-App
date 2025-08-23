package com.example.recipeapp.network

import com.example.recipeapp.models.MealResponse
import retrofit2.Call

interface MealRemoteDataSource {
    fun getRandomMeal(): Call<MealResponse>
    fun getMealsByFirstLetter(letter: String): Call<MealResponse>
}
