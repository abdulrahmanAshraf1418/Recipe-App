package com.example.recipeapp.repository

import com.example.recipeapp.network.MealRemoteDataSource

class MealRepository(private val remoteDataSource: MealRemoteDataSource) {

    fun getRandomMeal() = remoteDataSource.getRandomMeal()

    fun getMealsByFirstLetter(letter: String) = remoteDataSource.getMealsByFirstLetter(letter)
}
