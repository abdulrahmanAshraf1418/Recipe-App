package com.example.recipeapp.repository

import com.example.recipeapp.models.Meal
import com.example.recipeapp.network.MealRemoteDataSource

class MealRepository(private val remoteDataSource: MealRemoteDataSource) {

    suspend fun getRandomMeal(): Meal? {
        return remoteDataSource.getRandomMeal().meals?.firstOrNull()
    }

    suspend fun getMealsByFirstLetter(letter: String): List<Meal> {
        return (remoteDataSource.getMealsByFirstLetter(letter).meals ?: emptyList()) as List<Meal>
    }

}

