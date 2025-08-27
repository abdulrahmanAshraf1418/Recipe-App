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
        return remoteDataSource.getRandomMeal().meals?.firstOrNull()
    }

    suspend fun getMealsByFirstLetter(letter: String): List<Meal> {
        return (remoteDataSource.getMealsByFirstLetter(letter).meals ?: emptyList()) as List<Meal>
    }

    suspend fun searchMealsByName(name: String): List<Meal> {
        return (remoteDataSource.searchMealsByName(name).meals ?: emptyList()) as List<Meal>
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


    suspend fun getMealById (id: String): Meal?{
        return (remoteDataSource.getMealById(id).meals?.firstOrNull())
    }
    suspend fun searchMealsByName(name: String): List<Meal> {
        return (remoteDataSource.searchMealsByName(name).meals ?: emptyList()) as List<Meal>
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

        apiMeal?.let {
            val savedMeal = localDataSource.getLocalMealById(id)
            if (savedMeal != null) {
                it.isFavorite = true
            }
        }

        return apiMeal
    }
    suspend fun searchMealsByName(name: String): List<Meal> {
        return (remoteDataSource.searchMealsByName(name).meals ?: emptyList()) as List<Meal>
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

