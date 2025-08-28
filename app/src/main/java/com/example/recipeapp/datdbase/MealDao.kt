package com.example.recipeapp.datdbase

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.recipeapp.models.Meal

@Dao
interface MealDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeal(meal: Meal)

    @Delete
    suspend fun deleteMeal(meal: Meal)

    @Query("SELECT * FROM meals")
    fun getAllLocalMeals(): LiveData<List<Meal>>

    @Query("SELECT * FROM meals WHERE idMeal = :id LIMIT 1")
    suspend fun getLocalMealById(id: String): Meal?

}
