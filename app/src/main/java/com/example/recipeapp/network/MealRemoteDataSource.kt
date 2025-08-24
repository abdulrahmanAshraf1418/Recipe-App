package com.example.recipeapp.network

import com.example.recipeapp.models.MealResponse
import retrofit2.Call

interface MealRemoteDataSource {
    suspend fun getRandomMeal(): MealResponse
    suspend fun getMealsByFirstLetter(letter: String): MealResponse
}