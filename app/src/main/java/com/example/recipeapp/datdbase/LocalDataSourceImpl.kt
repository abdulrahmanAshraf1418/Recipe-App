package com.example.recipeapp.datdbase

import android.content.Context
import androidx.lifecycle.LiveData
import com.example.recipeapp.models.Meal

class LocalDataSourceImpl(context: Context) : LocalDataSource {

    private var dao: MealDao

    init {
        val db = MealDatabase.getInstance(context)
        dao = db.mealDao()
    }

    override suspend fun insert(meal: Meal, userId: String) {
        // نضيف userId للوجبة قبل التخزين
        val mealWithUser = meal.copy(userId = userId, isFavorite = true)
        dao.insertMeal(mealWithUser)
    }

    override suspend fun delete(meal: Meal, userId: String) {
        // لازم نمسح الوجبة لليوزر المحدد
        val mealWithUser = meal.copy(userId = userId)
        dao.deleteMeal(mealWithUser)
    }

    override fun listAll(userId: String): LiveData<List<Meal>> {
        return dao.getAllLocalMeals(userId)
    }

    override suspend fun getLocalMealById(id: String, userId: String): Meal? {
        return dao.getLocalMealById(id, userId)
    }

    override suspend fun isMealFavorite(id: String, userId: String): Boolean {
        return dao.getLocalMealById(id, userId) != null
    }
}
