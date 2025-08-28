package com.example.recipeapp.ui

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RadioGroup
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
    private lateinit var editSearch: EditText
    private lateinit var progressBar: ProgressBar
    private lateinit var tvNoResults: TextView

    private val handler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        editSearch = view.findViewById(R.id.editSearch)
        val btnSearch = view.findViewById<ImageView>(R.id.btnSearch)
        progressBar = view.findViewById(R.id.progressBar)
        tvNoResults = view.findViewById(R.id.tvNoResults)

        val repo = MealRepository(MealRemoteDataSourceImpl(RetrofitInstance.api))
        viewModel = ViewModelProvider(this, MealViewModelFactory(repo))[MealViewModel::class.java]

        recycler = view.findViewById(R.id.recyclerView)
        radioGroup = view.findViewById(R.id.radioGroup)

        simpleAdapter = SimpleListAdapter { value ->
            val type = viewModel.selectedType.value ?: "Category"
            val action = SearchFragmentDirections
                .actionSearchFragmentToMealsFragment(type, value)
            findNavController().navigate(action)
        }

        mealSearchAdapter = MealSearchAdapter { meal ->
            val action = SearchFragmentDirections
                .actionSearchFragmentToDetailsFragment(meal.idMeal)
            findNavController().navigate(action)
        }

        recycler.adapter = simpleAdapter
        recycler.layoutManager = GridLayoutManager(requireContext(), 2)

        if (viewModel.selectedType.value == null) {
            radioGroup.check(R.id.radioCategory)
            viewModel.setSelectedType("Category")
            progressBar.visibility = View.VISIBLE
            viewModel.fetchCategories()
        }

        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            progressBar.visibility = View.VISIBLE
            editSearch.setText("")
            mealSearchAdapter.submitList(emptyList())
            recycler.adapter = simpleAdapter
            recycler.layoutManager = GridLayoutManager(requireContext(), 2)
            tvNoResults.visibility = View.GONE

            when (checkedId) {
                R.id.radioCategory -> {
                    viewModel.setSelectedType("Category")
                    viewModel.fetchCategories()
                }
                R.id.radioIngredient -> {
                    viewModel.setSelectedType("Ingredient")
                    viewModel.fetchIngredients()
                }
                R.id.radioArea -> {
                    viewModel.setSelectedType("Area")
                    viewModel.fetchAreas()
                }
            }
        }

        btnSearch.setOnClickListener {
            val query = editSearch.text.toString().trim()
            if (query.isNotEmpty()) {
                progressBar.visibility = View.VISIBLE
                viewModel.searchMeals(query)
                hideKeyboard()
            }
        }

        editSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = editSearch.text.toString().trim()
                if (query.isNotEmpty()) {
                    progressBar.visibility = View.VISIBLE
                    viewModel.searchMeals(query)
                    hideKeyboard()
                }
                true
            } else {
                false
            }
        }

        editSearch.addTextChangedListener { text ->
            val query = text.toString().trim()
            searchRunnable?.let { handler.removeCallbacks(it) }

            searchRunnable = Runnable {
                if (query.isNotEmpty()) {
                    progressBar.visibility = View.VISIBLE
                    viewModel.searchMeals(query)
                } else {
                    recycler.adapter = simpleAdapter
                    recycler.layoutManager = GridLayoutManager(requireContext(), 2)
                    tvNoResults.visibility = View.GONE
                    progressBar.visibility = View.VISIBLE

                    when (viewModel.selectedType.value) {
                        "Category" -> viewModel.fetchCategories()
                        "Ingredient" -> viewModel.fetchIngredients()
                        "Area" -> viewModel.fetchAreas()
                    }
                }
            }

            handler.postDelayed(searchRunnable!!, 500)
        }

        viewModel.mealsByNameLiveData.observe(viewLifecycleOwner) { meals ->
            progressBar.visibility = View.GONE
            if (meals.isNotEmpty()) {
                tvNoResults.visibility = View.GONE
                recycler.adapter = mealSearchAdapter
                recycler.layoutManager = GridLayoutManager(requireContext(), 2)
                mealSearchAdapter.submitList(meals)
            } else {
                tvNoResults.visibility = View.VISIBLE
                recycler.adapter = mealSearchAdapter
                mealSearchAdapter.submitList(emptyList())
            }
        }

        viewModel.categoriesLiveData.observe(viewLifecycleOwner) { list ->
            progressBar.visibility = View.GONE
            tvNoResults.visibility = View.GONE
            simpleAdapter.submitList(list)
        }
        viewModel.ingredientsLiveData.observe(viewLifecycleOwner) { list ->
            progressBar.visibility = View.GONE
            tvNoResults.visibility = View.GONE
            simpleAdapter.submitList(list)
        }
        viewModel.areasLiveData.observe(viewLifecycleOwner) { list ->
            progressBar.visibility = View.GONE
            tvNoResults.visibility = View.GONE
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

    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(editSearch.windowToken, 0)
    }
}
