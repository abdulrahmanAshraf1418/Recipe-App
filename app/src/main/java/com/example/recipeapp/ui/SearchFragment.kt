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
import com.airbnb.lottie.LottieAnimationView
import com.example.recipeapp.R
import com.example.recipeapp.datdbase.LocalDataSourceImpl
import com.example.recipeapp.network.MealRemoteDataSourceImpl
import com.example.recipeapp.network.RetrofitInstance
import com.example.recipeapp.repository.MealRepository
import com.example.recipeapp.utils.checkGuestAction
import com.example.recipeapp.utils.showConfirmDialog
import com.example.recipeapp.utils.showStyledSnackBar
import com.example.recipeapp.viewmodel.MealViewModel
import com.example.recipeapp.viewmodel.MealViewModelFactory
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

class SearchFragment : Fragment() {

    private lateinit var viewModel: MealViewModel
    private lateinit var recycler: RecyclerView
    private lateinit var radioGroup: RadioGroup
    private lateinit var simpleAdapter: SimpleListAdapter
    private lateinit var mealSearchAdapter: MealSearchAdapter
    private lateinit var editSearch: EditText
    private lateinit var progressBar: ProgressBar
    private lateinit var tvNoResults: TextView
    private lateinit var offlineAnimation: LottieAnimationView

    private val handler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null

    private val currentUid: String?
        get() = FirebaseAuth.getInstance().currentUser?.uid

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

        recycler = view.findViewById(R.id.recyclerView)
        radioGroup = view.findViewById(R.id.radioGroup)
        offlineAnimation = view.findViewById(R.id.offlineAnimationSearch)

        val repo = MealRepository(
            MealRemoteDataSourceImpl(RetrofitInstance.api),
            LocalDataSourceImpl(requireContext())
        )
        viewModel = ViewModelProvider(this, MealViewModelFactory(repo, currentUid.toString()))[MealViewModel::class.java]

        if (isNetworkAvailable()) {
            setViewsVisibility(true)
            offlineAnimation.cancelAnimation()
            offlineAnimation.visibility = View.GONE
            setupSearchLogic(btnSearch)
        } else {
            setViewsVisibility(false)
            showOfflineAnimation()
        }
    }

    private fun setupSearchLogic(btnSearch: ImageView) {
        simpleAdapter = SimpleListAdapter { value ->
            val type = viewModel.selectedType.value ?: "Category"
            val action = SearchFragmentDirections
                .actionSearchFragmentToMealsFragment(type, value)
            findNavController().navigate(action)
        }

        mealSearchAdapter = MealSearchAdapter(
            onMealClick = { meal ->
                val action = SearchFragmentDirections
                    .actionSearchFragmentToDetailsFragment(meal.idMeal)
                findNavController().navigate(action)
            },
            onFavoriteRequest = { meal, position ->
                checkGuestAction {
                    currentUid?.let { uid ->
                        if (meal.isFavorite) {
                            requireContext().showConfirmDialog(
                                title = "Remove Favorite",
                                message = "Are you sure you want to remove ${meal.strMeal} from favorites?",
                                onConfirm = {
                                    viewModel.toggleMeal(meal, uid)
                                    meal.isFavorite = false
                                    mealSearchAdapter.notifyItemChanged(position)

                                    requireView().showStyledSnackBar(
                                        message = "${meal.strMeal} removed from favorites",
                                        actionText = "Undo"
                                    ) {
                                        viewModel.toggleMeal(meal, uid)
                                        meal.isFavorite = true
                                        mealSearchAdapter.notifyItemChanged(position)
                                    }
                                }
                            )
                        } else {
                            viewModel.toggleMeal(meal, uid)
                            meal.isFavorite = true
                            mealSearchAdapter.notifyItemChanged(position)

                            requireView().showStyledSnackBar(
                                message = "${meal.strMeal} added to favorites",
                                actionText = "Undo"
                            ) {
                                viewModel.toggleMeal(meal, uid)
                                meal.isFavorite = false
                                mealSearchAdapter.notifyItemChanged(position)
                            }
                        }
                    }
                }
            }

        )

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

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = requireContext()
            .getSystemService(Context.CONNECTIVITY_SERVICE) as android.net.ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    private fun showOfflineAnimation() {
        offlineAnimation.visibility = View.VISIBLE
        offlineAnimation.playAnimation()
    }

    private fun setViewsVisibility(isVisible: Boolean) {
        val visibility = if (isVisible) View.VISIBLE else View.GONE
        listOf(recycler, radioGroup, editSearch, progressBar, tvNoResults).forEach {
            it.visibility = visibility
        }
    }

    private fun hideKeyboard() {
        val imm =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(editSearch.windowToken, 0)
    }
}
