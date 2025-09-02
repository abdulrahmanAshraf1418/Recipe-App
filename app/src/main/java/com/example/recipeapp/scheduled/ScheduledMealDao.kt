package com.example.recipeapp.scheduled

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ScheduledMealDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(meal: ScheduledMeal)

    @Delete
    suspend fun delete(meal: ScheduledMeal)

    @Query("SELECT * FROM scheduled_meals ORDER BY dateTime ASC")
    fun getAllScheduledMeals(): LiveData<List<ScheduledMeal>>
}
