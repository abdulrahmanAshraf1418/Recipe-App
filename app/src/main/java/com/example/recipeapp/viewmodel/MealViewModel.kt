package com.example.recipeapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipeapp.models.Meal
import com.example.recipeapp.models.MealItem
import com.example.recipeapp.repository.MealRepository
import kotlinx.coroutines.launch

class MealViewModel(private val repository: MealRepository) : ViewModel() {

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

    fun searchMeals(name: String) {
        viewModelScope.launch {
            val meals = repository.searchMealsByName(name)
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
}

    fun getMealById(id: String) {
        viewModelScope.launch {
            val meal = repository.getMealById(id)
            meal?.let {
                val savedMeal = repository.getSavedMealById(id)
                if (savedMeal != null) {
                    it.isFavorite = true
                }
                mealByIdLiveData.postValue(it)
            }
        }
    }


    fun searchMeals(name: String) {
        viewModelScope.launch {
            val meals = repository.searchMealsByName(name)
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

}
    fun getAllLocalMeals() {
        viewModelScope.launch {
            if (_allLocalMealsLiveData == null) {
                _allLocalMealsLiveData = repository.getAllMeals()
                _allLocalMealsLiveData!!.observeForever { meals ->
                    allLocalMealsLiveData.postValue(meals)
                }
            }
        }
    }

    private fun refreshLocalMeals() {
        viewModelScope.launch {
            val meals = repository.getAllMeals()
            meals.observeForever { mealsList ->
                allLocalMealsLiveData.postValue(mealsList)
            }
        }
    }

    fun toggleMeal(meal: Meal) {
        viewModelScope.launch {
            val savedMeal = repository.getSavedMealById(meal.idMeal ?: "")
            if (savedMeal == null) {
                meal.isFavorite = true
                repository.insertMeal(meal)
                _messageLiveData.postValue("${meal.strMeal} added to favorite")
            } else {
                repository.deleteMeal(savedMeal)
                meal.isFavorite = false
                _messageLiveData.postValue("${meal.strMeal} removed from favorite")

            }
        }
    }

    fun getLocalMealById(mealId: String) {
        viewModelScope.launch {
            val meal = repository.getSavedMealById(mealId)
            localMealByIdLiveData.postValue(meal!!)
        }
    }


}