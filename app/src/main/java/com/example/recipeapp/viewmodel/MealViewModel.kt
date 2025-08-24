package com.example.recipeapp.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipeapp.models.Meal
import com.example.recipeapp.repository.MealRepository
import kotlinx.coroutines.launch

class MealViewModel(private val repository: MealRepository) : ViewModel() {

    val randomMealLiveData = MutableLiveData<Meal>()
    val mealsByLetterLiveData = MutableLiveData<List<Meal>>()

    fun getRandomMeal() {
        viewModelScope.launch {
            val meal = repository.getRandomMeal()
            meal?.let {
                randomMealLiveData.postValue(it)
            }
        }
    }

    fun getMealsByLetter(letter: String) {
        viewModelScope.launch {
            val meals = repository.getMealsByFirstLetter(letter)
            mealsByLetterLiveData.postValue(meals)
        }
    }
}

