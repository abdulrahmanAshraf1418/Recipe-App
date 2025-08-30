package com.example.recipeapp.datdbase

import androidx.lifecycle.LiveData
import com.example.recipeapp.models.Meal

interface LocalDataSource {
    suspend fun insert(meal: Meal, userId: String)
    suspend fun delete(meal: Meal, userId: String)
    suspend fun isMealFavorite(mealId: String, userId: String): Boolean
    fun listAll(userId: String): LiveData<List<Meal>>
    suspend fun getLocalMealById(mealId: String, userId: String): Meal?
}


