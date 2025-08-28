package com.example.recipeapp.datdbase

import androidx.lifecycle.LiveData
import com.example.recipeapp.models.Meal

interface LocalDataSource {
    suspend fun insert (meal: Meal)
    suspend fun delete (meal: Meal)
    suspend fun listAll (): LiveData<List<Meal>>
    suspend fun getLocalMealById(id: String): Meal?

}