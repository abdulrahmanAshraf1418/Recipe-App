package com.example.recipeapp.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.recipeapp.models.Meal
import com.example.recipeapp.models.MealResponse
import com.example.recipeapp.repository.MealRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MealViewModel(private val repository: MealRepository) : ViewModel() {

    val randomMealLiveData = MutableLiveData<Meal>()
    val mealsByLetterLiveData = MutableLiveData<List<Meal>>()

    fun getRandomMeal() {
        repository.getRandomMeal().enqueue(object : Callback<MealResponse> {
            override fun onResponse(call: Call<MealResponse>, response: Response<MealResponse>) {
                response.body()?.meals?.get(0)?.let {
                    randomMealLiveData.postValue(it)
                }
            }

            override fun onFailure(call: Call<MealResponse>, t: Throwable) {}
        })
    }

    fun getMealsByLetter(letter: String) {
        repository.getMealsByFirstLetter(letter).enqueue(object : Callback<MealResponse> {
            override fun onResponse(call: Call<MealResponse>, response: Response<MealResponse>) {
                response.body()?.meals?.let {
                    mealsByLetterLiveData.postValue(it.filterNotNull())
                }
            }

            override fun onFailure(call: Call<MealResponse>, t: Throwable) {}
        })
    }
}
