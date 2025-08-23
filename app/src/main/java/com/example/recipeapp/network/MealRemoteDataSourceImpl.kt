package com.example.recipeapp.network

class MealRemoteDataSourceImpl(private val api: MealApi) : MealRemoteDataSource {

    override fun getRandomMeal() = api.getRandomMeal()

    override fun getMealsByFirstLetter(letter: String) = api.getMealsByFirstLetter(letter)
}
