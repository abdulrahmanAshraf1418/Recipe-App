package com.example.recipeapp.network

import com.example.recipeapp.models.AreasResponse
import com.example.recipeapp.models.CategoriesResponse
import com.example.recipeapp.models.IngredientsResponse
import com.example.recipeapp.models.MealItemResponse
import com.example.recipeapp.models.MealResponse

interface MealRemoteDataSource {
    suspend fun getRandomMeal(): MealResponse
    suspend fun getMealsByFirstLetter(letter: String): MealResponse
    suspend fun getMealById (id: String): MealResponse
    suspend fun searchMealsByName(name: String): MealResponse
    suspend fun getCategories(): CategoriesResponse
    suspend fun getAreas(): AreasResponse
    suspend fun getIngredients(): IngredientsResponse
    suspend fun filterByCategory(category: String): MealItemResponse
    suspend fun filterByArea(area: String): MealItemResponse
    suspend fun filterByIngredient(ingredient: String): MealItemResponse
    suspend fun getMealById (id: String): MealResponse
}