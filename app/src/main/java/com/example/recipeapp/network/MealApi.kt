package com.example.recipeapp.network

import com.example.recipeapp.models.AreasResponse
import com.example.recipeapp.models.CategoriesResponse
import com.example.recipeapp.models.IngredientsResponse
import com.example.recipeapp.models.MealItemResponse
import com.example.recipeapp.models.MealResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface MealApi {
    @GET("random.php")
    suspend fun getRandomMeal(): MealResponse

    @GET("search.php")
    suspend fun getMealsByFirstLetter(
        @Query("f") letter: String
    ): MealResponse

    @GET ("lookup.php")
    suspend fun getMealById (
        @Query("i") id: String
    ): MealResponse

    @GET("search.php")
    suspend fun searchMealsByName(
        @Query("s") name: String
    ): MealResponse


    @GET("list.php")
    suspend fun getCategories(@Query("c") list: String = "list"): CategoriesResponse

    @GET("list.php")
    suspend fun getAreas(@Query("a") list: String = "list"): AreasResponse

    @GET("list.php")
    suspend fun getIngredients(@Query("i") list: String = "list"): IngredientsResponse

    @GET("filter.php")
    suspend fun filterByCategory(@Query("c") category: String): MealItemResponse

    @GET("filter.php")
    suspend fun filterByArea(@Query("a") area: String): MealItemResponse

    @GET("filter.php")
    suspend fun filterByIngredient(@Query("i") ingredient: String): MealItemResponse
}
