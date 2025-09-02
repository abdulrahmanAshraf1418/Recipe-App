package com.example.recipeapp.datdbase

import android.content.Context
import androidx.lifecycle.LiveData
import com.example.recipeapp.models.Meal
import com.example.recipeapp.scheduled.ScheduledMeal
import com.example.recipeapp.scheduled.ScheduledMealDao

class LocalDataSourceImpl(context: Context) : LocalDataSource {

    private var dao: MealDao
    private var scheduledMealDao : ScheduledMealDao

    init {
        val db = MealDatabase.getInstance(context)
        dao = db.mealDao()
        scheduledMealDao = db.scheduledMealDao()
    }


    override suspend fun insert(meal: Meal, userId: String) {
        val mealWithUser = meal.copy(userId = userId, isFavorite = true)
        dao.insertMeal(mealWithUser)
    }

    override suspend fun delete(meal: Meal, userId: String) {
        val mealWithUser = meal.copy(userId = userId)
        dao.deleteMeal(mealWithUser)
    }

    override fun listAll(userId: String): LiveData<List<Meal>> {
        return dao.getAllLocalMeals(userId)
    }

    override suspend fun getLocalMealById(mealId: String, userId: String): Meal? {
        return dao.getLocalMealById(mealId, userId)
    }

    override suspend fun isMealFavorite(mealId: String, userId: String): Boolean {
        return dao.getLocalMealById(mealId, userId) != null
    }


    override suspend fun insertScheduledMeal(meal: ScheduledMeal) = scheduledMealDao.insert(meal)

    override suspend fun deleteScheduledMeal(meal: ScheduledMeal) = scheduledMealDao.delete(meal)

    override fun getAllScheduledMeals(): LiveData<List<ScheduledMeal>> = scheduledMealDao.getAllScheduledMeals()

}
