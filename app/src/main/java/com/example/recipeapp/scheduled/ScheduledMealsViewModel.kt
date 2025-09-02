package com.example.recipeapp.scheduled

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipeapp.repository.MealRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

class ScheduledMealsViewModel @Inject constructor(
    private val repository: MealRepository
) : ViewModel() {

    val scheduledMeals: LiveData<List<ScheduledMeal>> = repository.getAllScheduledMeals()

    fun addScheduledMeal(meal: ScheduledMeal) {
        viewModelScope.launch {
            repository.insertScheduledMeal(meal)
        }
    }

    fun deleteScheduledMeal(meal: ScheduledMeal) {
        viewModelScope.launch {
            repository.deleteScheduledMeal(meal)
        }
    }
}

