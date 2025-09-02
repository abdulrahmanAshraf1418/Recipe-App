package com.example.recipeapp.scheduled

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recipeapp.R
import com.example.recipeapp.datdbase.LocalDataSourceImpl
import com.example.recipeapp.network.MealRemoteDataSource
import com.example.recipeapp.network.MealRemoteDataSourceImpl
import com.example.recipeapp.network.RetrofitInstance
import com.example.recipeapp.repository.MealRepository

class ScheduledMealsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ScheduledMealsAdapter
    private lateinit var viewModel: ScheduledMealsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_scheduled_meals, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerViewScheduledMeals)

        adapter = ScheduledMealsAdapter (
            onDeleteClick = {meal ->
                viewModel.deleteScheduledMeal(meal)
            },
            onMealClick = { mealId ->
                val action = ScheduledMealsFragmentDirections
                    .actionScheduledMealsFragmentToDetailsFragment(mealId)
                findNavController().navigate(action)
            }
        )

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())


        val localDataSource = LocalDataSourceImpl(requireContext())
        val remoteDataSource: MealRemoteDataSource = MealRemoteDataSourceImpl(RetrofitInstance.api)
        val repository = MealRepository(remoteDataSource, localDataSource)
        val factory = ScheduledMealsViewModelFactory(repository)

        viewModel = ViewModelProvider(this, factory)[ScheduledMealsViewModel::class.java]

        viewModel.scheduledMeals.observe(viewLifecycleOwner) { meals ->
            adapter.submitList(meals)
        }
    }
}