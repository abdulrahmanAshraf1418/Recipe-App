package com.example.recipeapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipeapp.models.Meal
import com.example.recipeapp.models.MealItem
import com.example.recipeapp.repository.MealRepository
import kotlinx.coroutines.launch

class MealViewModel(
    private val repository: MealRepository,
    private val userId: String // 👈 استقبل userId من الـ Activity/Fragment
) : ViewModel() {

    val randomMealLiveData = MutableLiveData<Meal>()
    val mealsByLetterLiveData = MutableLiveData<List<Meal>>()
    val mealByIdLiveData = MutableLiveData<Meal>()
    val localMealByIdLiveData = MutableLiveData<Meal>()
    private var _allLocalMealsLiveData: LiveData<List<Meal>>? = null
    val allLocalMealsLiveData = MutableLiveData<List<Meal>>()
    private val _messageLiveData = MutableLiveData<String>()
    val messageLiveData: LiveData<String> = _messageLiveData

    val categoriesLiveData = MutableLiveData<List<String>>()
    val areasLiveData = MutableLiveData<List<String>>()
    val ingredientsLiveData = MutableLiveData<List<String>>()
    val mealsLiveData = MutableLiveData<List<MealItem>>()
    val mealsByNameLiveData = MutableLiveData<List<Meal>>()

    private val _selectedType = MutableLiveData<String>()
    val selectedType: LiveData<String> get() = _selectedType

    fun setSelectedType(type: String) {
        _selectedType.value = type
    }

    fun getRandomMeal() {
        viewModelScope.launch {
            val meal = repository.getRandomMeal(userId)
            meal?.let {
                randomMealLiveData.postValue(it)
            }
        }
    }

    fun getMealsByLetter(letter: String) {
        viewModelScope.launch {
            val meals = repository.getMealsByFirstLetter(letter, userId)
            mealsByLetterLiveData.postValue(meals)
        }
    }

    fun getMealById(id: String) {
        viewModelScope.launch {
            val meal = repository.getMealById(id, userId)
            meal?.let {
                val savedMeal = repository.getSavedMealById(id, userId)
                if (savedMeal != null) {
                    it.isFavorite = true
                }
                mealByIdLiveData.postValue(it)
            }
        }
    }

    fun searchMeals(name: String) {
        viewModelScope.launch {
            val meals = repository.searchMealsByName(name, userId)

            meals.forEach { meal ->
                val savedMeal = repository.getSavedMealById(meal.idMeal ?: "", userId)
                if (savedMeal != null) {
                    meal.isFavorite = true
                }
            }
            mealsByNameLiveData.postValue(meals)
        }
    }

    fun fetchCategories() = viewModelScope.launch {
        categoriesLiveData.postValue(repository.listCategories())
    }

    fun fetchAreas() = viewModelScope.launch {
        areasLiveData.postValue(repository.listAreas())
    }

    fun fetchIngredients() = viewModelScope.launch {
        ingredientsLiveData.postValue(repository.listIngredients())
    }

    fun fetchMealsByCategory(category: String) = viewModelScope.launch {
        mealsLiveData.postValue(repository.getMealsByCategory(category))
    }

    fun fetchMealsByArea(area: String) = viewModelScope.launch {
        mealsLiveData.postValue(repository.getMealsByArea(area))
    }

    fun fetchMealsByIngredient(ingredient: String) = viewModelScope.launch {
        mealsLiveData.postValue(repository.getMealsByIngredient(ingredient))
    }

    fun getAllLocalMeals() {
        viewModelScope.launch {
            if (_allLocalMealsLiveData == null) {
                _allLocalMealsLiveData = repository.getAllMeals(userId)
                _allLocalMealsLiveData!!.observeForever { meals ->
                    allLocalMealsLiveData.postValue(meals)
                }
            }
        }
    }

    private fun refreshLocalMeals() {
        viewModelScope.launch {
            val meals = repository.getAllMeals(userId)
            meals.observeForever { mealsList ->
                allLocalMealsLiveData.postValue(mealsList)
            }
        }
    }

    fun toggleMeal(meal: Meal, uid: String) {
        viewModelScope.launch {
            val savedMeal = repository.getSavedMealById(meal.idMeal ?: "", uid)
            if (savedMeal == null) {
                meal.isFavorite = true
                repository.insertMeal(meal, uid)
                _messageLiveData.postValue("${meal.strMeal} added to favorite")
            } else {
                repository.deleteMeal(savedMeal, uid)
                meal.isFavorite = false
                _messageLiveData.postValue("${meal.strMeal} removed from favorite")
            }
        }
    }


    fun getLocalMealById(mealId: String) {
        viewModelScope.launch {
            val meal = repository.getSavedMealById(mealId, userId)
            meal?.let {
                localMealByIdLiveData.postValue(it)
            }
        }
    }
}
