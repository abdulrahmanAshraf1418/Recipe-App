package com.example.recipeapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recipeapp.R
import com.example.recipeapp.adapters.FavoriteAdapter
import com.example.recipeapp.datdbase.LocalDataSourceImpl
import com.example.recipeapp.network.MealRemoteDataSourceImpl
import com.example.recipeapp.network.RetrofitInstance
import com.example.recipeapp.repository.MealRepository
import com.example.recipeapp.utils.showConfirmDialog
import com.example.recipeapp.viewmodel.MealViewModel
import com.example.recipeapp.viewmodel.MealViewModelFactory
import com.google.android.material.snackbar.Snackbar

class FavoritesFragment : Fragment() {

    private lateinit var viewModel: MealViewModel
    private lateinit var favAdapter: FavoriteAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyTextView: TextView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_favorites, container, false)

        recyclerView = view.findViewById(R.id.favRecyclerView)
        emptyTextView = view.findViewById(R.id.emptyTextView)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        favAdapter = FavoriteAdapter(
            onMealClick = { mealId ->
                val action = FavoritesFragmentDirections.actionFavoritesFragmentToDetailsFragment(mealId)
                findNavController().navigate(action)
                          },

            onFavoriteClick = { meal ->
                requireContext().showConfirmDialog(
                    title = "Remove Favorite",
                    message = "Are you sure you want to remove ${meal.strMeal} from favorites?",
                    positiveText = "Yes",
                    negativeText = "Cancel",
                    onConfirm = {
                        viewModel.toggleMeal(meal)
                        meal.isFavorite = false
                        favAdapter.notifyDataSetChanged()

                        Snackbar.make(requireView(),
                            "${meal.strMeal} removed from favorites",
                            Snackbar.LENGTH_LONG
                        ).setAction("Undo") {
                            viewModel.toggleMeal(meal)
                            meal.isFavorite = true
                            favAdapter.notifyDataSetChanged()
                        }.show()
                    }
                )
            }
        )

        recyclerView.adapter = favAdapter

        val remoteDataSource = MealRemoteDataSourceImpl(RetrofitInstance.api)
        val localDataSource = LocalDataSourceImpl(requireContext())
        val repository = MealRepository(remoteDataSource, localDataSource)
        val factory = MealViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(MealViewModel::class.java)

        viewModel.allLocalMealsLiveData.observe(viewLifecycleOwner) { meals ->
            if (meals.isNullOrEmpty()) {
                recyclerView.visibility = View.GONE
                emptyTextView.visibility = View.VISIBLE
            } else {
                recyclerView.visibility = View.VISIBLE
                emptyTextView.visibility = View.GONE
                favAdapter.setMeals(meals)
            }

        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getAllLocalMeals()
    }
}