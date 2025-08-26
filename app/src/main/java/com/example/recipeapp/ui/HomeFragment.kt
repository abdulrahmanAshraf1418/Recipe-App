package com.example.recipeapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.recipeapp.R
import com.example.recipeapp.network.MealRemoteDataSource
import com.example.recipeapp.network.MealRemoteDataSourceImpl
import com.example.recipeapp.network.RetrofitInstance
import com.example.recipeapp.repository.MealRepository
import com.example.recipeapp.viewmodel.MealViewModel
import com.example.recipeapp.viewmodel.MealViewModelFactory

class HomeFragment : Fragment() {

    private lateinit var randomMealImage: ImageView
    private lateinit var randomMealName: TextView
    private lateinit var randomMealCategory: TextView
    private lateinit var randomMealArea: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var mealsAdapter: MealsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        randomMealImage = view.findViewById(R.id.meal_image)
        randomMealName = view.findViewById(R.id.MealName)
        randomMealCategory = view.findViewById(R.id.MealCategory)
        randomMealArea = view.findViewById(R.id.MealArea)
        recyclerView = view.findViewById(R.id.recyclerViewMeals)

        mealsAdapter = MealsAdapter { mealId ->
            val action = HomeFragmentDirections.actionHomeFragmentToDetailsFragment(mealId)
            findNavController().navigate(action)
        }
        recyclerView.apply {
            adapter = mealsAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }

        val remoteDataSource: MealRemoteDataSource = MealRemoteDataSourceImpl(RetrofitInstance.api)
        val repository = MealRepository(remoteDataSource)
        val factory = MealViewModelFactory(repository)
        val viewModel = ViewModelProvider(this, factory).get(MealViewModel::class.java)

        viewModel.randomMealLiveData.observe(viewLifecycleOwner) { meal ->
            randomMealName.text = meal.strMeal
            randomMealCategory.text = "Category: ${meal.strCategory}"
            randomMealArea.text = "Area: ${meal.strArea}"
            Glide.with(this).load(meal.strMealThumb).into(randomMealImage)

            view.findViewById<View>(R.id.meal_image).setOnClickListener {
                val action = HomeFragmentDirections.actionHomeFragmentToDetailsFragment(meal.idMeal)
                findNavController().navigate(action)
            }
        }

        viewModel.mealsByLetterLiveData.observe(viewLifecycleOwner) { meals ->
            mealsAdapter.setMeals(meals)
        }

        viewModel.getRandomMeal()
        val randomLetter = getRandomLetter()
        viewModel.getMealsByLetter(randomLetter)
    }
    private fun getRandomLetter(): String {
        val letters = ('a'..'z').filterNot { it in listOf('q', 'x', 'u', 'z') }
        return letters.random().toString()
    }

}

