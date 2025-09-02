package com.example.recipeapp.scheduled

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scheduled_meals")
data class ScheduledMeal(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val mealId: String,
    val mealName: String,
    val mealThumb: String,
    val dateTime: Long
)