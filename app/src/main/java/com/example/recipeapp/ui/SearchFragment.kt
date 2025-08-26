package com.example.recipeapp.ui

import android.os.Bundle
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.recipeapp.R
import com.example.recipeapp.network.MealRemoteDataSourceImpl
import com.example.recipeapp.network.RetrofitInstance
import com.example.recipeapp.repository.MealRepository
import com.example.recipeapp.viewmodel.MealViewModel
import com.example.recipeapp.viewmodel.MealViewModelFactory

class SearchFragment : Fragment() {

    private lateinit var viewModel: MealViewModel
    private lateinit var recycler: RecyclerView
    private lateinit var radioGroup: RadioGroup
    private lateinit var simpleAdapter: SimpleListAdapter
    private lateinit var mealSearchAdapter: MealSearchAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val editSearch = view.findViewById<EditText>(R.id.editSearch)
        val btnSearch = view.findViewById<ImageView>(R.id.btnSearch)

        val repo = MealRepository(MealRemoteDataSourceImpl(RetrofitInstance.api))
        viewModel =
            ViewModelProvider(this, MealViewModelFactory(repo))[MealViewModel::class.java]

        recycler = view.findViewById(R.id.recyclerView)
        radioGroup = view.findViewById(R.id.radioGroup)

        simpleAdapter = SimpleListAdapter { value ->
            val type = viewModel.selectedType.value ?: "Category"
            val action = SearchFragmentDirections
                .actionSearchFragmentToMealsFragment(type, value)
            findNavController().navigate(action)
        }

        mealSearchAdapter = MealSearchAdapter { meal ->
//            val action = SearchFragmentDirections
//                .actionSearchFragmentToMealDetailFragment(meal.idMeal)
//            findNavController().navigate(action)
        }

        recycler.adapter = simpleAdapter
        recycler.layoutManager = GridLayoutManager(requireContext(), 2)

        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radioCategory -> {
                    recycler.adapter = simpleAdapter
                    viewModel.setSelectedType("Category")
                    viewModel.fetchCategories()
                }
                R.id.radioIngredient -> {
                    recycler.adapter = simpleAdapter
                    viewModel.setSelectedType("Ingredient")
                    viewModel.fetchIngredients()
                }
                R.id.radioArea -> {
                    recycler.adapter = simpleAdapter
                    viewModel.setSelectedType("Area")
                    viewModel.fetchAreas()
                }
            }
        }

        btnSearch.setOnClickListener {
            val query = editSearch.text.toString().trim()
            if (query.isNotEmpty()) {
                viewModel.searchMeals(query)
            }
        }

        editSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = editSearch.text.toString().trim()
                if (query.isNotEmpty()) {
                    viewModel.searchMeals(query)
                }
                true
            } else {
                false
            }
        }

        viewModel.mealsByNameLiveData.observe(viewLifecycleOwner) { meals ->
            if (meals.isNotEmpty()) {
                recycler.adapter = mealSearchAdapter
                recycler.layoutManager = GridLayoutManager(requireContext(), 2)
                mealSearchAdapter.submitList(meals)
            }
        }

        viewModel.categoriesLiveData.observe(viewLifecycleOwner) { list ->
            simpleAdapter.submitList(list)
        }
        viewModel.ingredientsLiveData.observe(viewLifecycleOwner) { list ->
            simpleAdapter.submitList(list)
        }
        viewModel.areasLiveData.observe(viewLifecycleOwner) { list ->
            simpleAdapter.submitList(list)
        }

        viewModel.selectedType.observe(viewLifecycleOwner) { type ->
            when (type) {
                "Category" -> radioGroup.check(R.id.radioCategory)
                "Ingredient" -> radioGroup.check(R.id.radioIngredient)
                "Area" -> radioGroup.check(R.id.radioArea)
            }
        }
    }
}
