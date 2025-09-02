package com.example.recipeapp.datdbase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.recipeapp.models.Meal
import com.example.recipeapp.scheduled.ScheduledMeal
import com.example.recipeapp.scheduled.ScheduledMealDao

@Database(
    entities = [Meal::class, ScheduledMeal::class],
    version = 4,
    exportSchema = false
)
abstract class MealDatabase : RoomDatabase() {

    abstract fun mealDao(): MealDao
    abstract fun scheduledMealDao(): ScheduledMealDao

    companion object {
        @Volatile
        private var instance: MealDatabase? = null

        fun getInstance(context: Context): MealDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): MealDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                MealDatabase::class.java,
                "meal_database"
            )
                .fallbackToDestructiveMigration(false)
                .build()
        }
    }
}
