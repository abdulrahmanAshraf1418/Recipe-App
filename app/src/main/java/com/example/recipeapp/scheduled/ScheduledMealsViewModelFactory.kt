package com.example.recipeapp.scheduled

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.recipeapp.repository.MealRepository

class ScheduledMealsViewModelFactory(
    private val repository: MealRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ScheduledMealsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ScheduledMealsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
