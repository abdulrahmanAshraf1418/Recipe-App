package com.example.recipeapp.datdbase

import android.content.Context
import androidx.lifecycle.LiveData
import com.example.recipeapp.models.Meal

class LocalDataSourceImpl(context: Context) : LocalDataSource {

    private var dao : MealDao

    init {
        val db = MealDatabase.getInstance(context)
        dao = db.mealDao()
    }

    override suspend fun insert(meal: Meal) {
        dao.insertMeal(meal)
    }

    override suspend fun delete(meal: Meal) {
        dao.deleteMeal(meal)
    }

    override suspend fun listAll(): LiveData<List<Meal>> {
        return dao.getAllLocalMeals()
    }

    override suspend fun getLocalMealById(id: String): Meal?{
        return dao.getLocalMealById(id)
    }

    override suspend fun isMealFavorite(id: String): Boolean {
        return dao.getLocalMealById(id) != null
    }

}