package com.example.recipeapp.datdbase

import androidx.lifecycle.LiveData
import com.example.recipeapp.models.Meal
import com.example.recipeapp.scheduled.ScheduledMeal

interface LocalDataSource {
    suspend fun insert(meal: Meal, userId: String)
    suspend fun delete(meal: Meal, userId: String)
    suspend fun isMealFavorite(mealId: String, userId: String): Boolean
    fun listAll(userId: String): LiveData<List<Meal>>
    suspend fun getLocalMealById(mealId: String, userId: String): Meal?
    suspend fun insertScheduledMeal(meal: ScheduledMeal)
    suspend fun deleteScheduledMeal(meal: ScheduledMeal)
    fun getAllScheduledMeals(): LiveData<List<ScheduledMeal>>
}


