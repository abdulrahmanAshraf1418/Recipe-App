package com.example.recipeapp.network

class MealRemoteDataSourceImpl(private val api: MealApi) : MealRemoteDataSource {
    override suspend fun getRandomMeal() = api.getRandomMeal()
    override suspend fun getMealsByFirstLetter(letter: String) = api.getMealsByFirstLetter(letter)
}