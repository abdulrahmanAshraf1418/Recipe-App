package com.example.recipeapp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.example.recipeapp.datdbase.LocalDataSource
import com.example.recipeapp.models.Meal
import com.example.recipeapp.models.MealItem
import com.example.recipeapp.network.MealRemoteDataSource

class MealRepository(
    private val remoteDataSource: MealRemoteDataSource,
    private val localDataSource: LocalDataSource
) {

    suspend fun getRandomMeal(): Meal? {
        val meal = remoteDataSource.getRandomMeal().meals?.firstOrNull()
        meal?.let {
            it.isFavorite = localDataSource.isMealFavorite(it.idMeal)
        }
        return meal
    }

    suspend fun getMealsByFirstLetter(letter: String): List<Meal> {
        val meals = remoteDataSource.getMealsByFirstLetter(letter).meals ?: emptyList()
        return meals.map { meal ->
            meal?.isFavorite = localDataSource.isMealFavorite(meal.idMeal)
            meal as Meal
        }
    }

    suspend fun searchMealsByName(name: String): List<Meal> {
        val meals = remoteDataSource.searchMealsByName(name).meals ?: emptyList()
        return meals.map { meal ->
            meal?.isFavorite = localDataSource.isMealFavorite(meal.idMeal)
            meal as Meal
        }
    }

    suspend fun listCategories(): List<String> =
        remoteDataSource.getCategories().meals?.map { it.strCategory } ?: emptyList()

    suspend fun listAreas(): List<String> =
        remoteDataSource.getAreas().meals?.map { it.strArea } ?: emptyList()

    suspend fun listIngredients(): List<String> =
        remoteDataSource.getIngredients().meals?.map { it.strIngredient } ?: emptyList()

    suspend fun getMealsByCategory(category: String): List<MealItem> =
        remoteDataSource.filterByCategory(category).meals ?: emptyList()

    suspend fun getMealsByArea(area: String): List<MealItem> =
        remoteDataSource.filterByArea(area).meals ?: emptyList()

    suspend fun getMealsByIngredient(ingredient: String): List<MealItem> =
        remoteDataSource.filterByIngredient(ingredient).meals ?: emptyList()

    suspend fun getMealById(id: String): Meal? {
        val apiMeal = remoteDataSource.getMealById(id).meals?.firstOrNull()
        apiMeal?.isFavorite = localDataSource.isMealFavorite(id)
        return apiMeal
    }

    suspend fun insertMeal(meal: Meal) = localDataSource.insert(meal)

    suspend fun deleteMeal(meal: Meal) = localDataSource.delete(meal)

    suspend fun getAllMeals(): LiveData<List<Meal>> {
        return localDataSource.listAll().map { meals ->
            meals.map { meal ->
                meal.copy(isFavorite = true)
            }
        }
    }
    suspend fun getSavedMealById(id: String): Meal? {
        return localDataSource.getLocalMealById(id)
    }
}
