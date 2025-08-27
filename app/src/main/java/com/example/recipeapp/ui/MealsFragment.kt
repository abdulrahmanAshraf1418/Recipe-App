package com.example.recipeapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recipeapp.R
import com.example.recipeapp.network.MealRemoteDataSourceImpl
import com.example.recipeapp.network.RetrofitInstance
import com.example.recipeapp.repository.MealRepository
import com.example.recipeapp.viewmodel.MealViewModel
import com.example.recipeapp.viewmodel.MealViewModelFactory

class MealsFragment : Fragment() {

    private lateinit var viewModel: MealViewModel
    private lateinit var recycler: RecyclerView
    private lateinit var adapter: MealsFilterAdapter

    private var filterType: String? = null
    private var filterValue: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_meals, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val repo = MealRepository(MealRemoteDataSourceImpl(RetrofitInstance.api))
        viewModel = ViewModelProvider(this, MealViewModelFactory(repo))[MealViewModel::class.java]

        recycler = view.findViewById(R.id.recyclerViewMeals)
        adapter = MealsFilterAdapter()
        recycler.adapter = adapter
        recycler.layoutManager = GridLayoutManager(requireContext(), 2)

        arguments?.let {
            filterType = it.getString("filterType")
            filterValue = it.getString("filterValue")
        }

        when (filterType) {
            "Category" -> filterValue?.let { viewModel.fetchMealsByCategory(it) }
            "Ingredient" -> filterValue?.let { viewModel.fetchMealsByIngredient(it) }
            "Area" -> filterValue?.let { viewModel.fetchMealsByArea(it) }
        }

        viewModel.mealsLiveData.observe(viewLifecycleOwner) { items ->
            adapter.setMeals(items)
        }
    }
}