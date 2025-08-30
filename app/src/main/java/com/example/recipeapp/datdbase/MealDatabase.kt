package com.example.recipeapp.datdbase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.recipeapp.models.Meal

@Database(entities = [Meal::class], version = 3, exportSchema = false)
abstract class MealDatabase : RoomDatabase() {

    abstract fun mealDao(): MealDao

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
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}
