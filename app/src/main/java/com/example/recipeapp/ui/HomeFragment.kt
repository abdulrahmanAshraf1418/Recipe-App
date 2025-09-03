package com.example.recipeapp.ui

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.target.Target
import com.example.recipeapp.R
import com.example.recipeapp.datdbase.LocalDataSourceImpl
import com.example.recipeapp.network.MealRemoteDataSource
import com.example.recipeapp.network.MealRemoteDataSourceImpl
import com.example.recipeapp.network.RetrofitInstance
import com.example.recipeapp.repository.MealRepository
import com.example.recipeapp.utils.checkGuestAction
import com.example.recipeapp.utils.showConfirmDialog
import com.example.recipeapp.utils.showStyledSnackBar
import com.example.recipeapp.viewmodel.MealViewModel
import com.example.recipeapp.viewmodel.MealViewModelFactory
import com.google.firebase.auth.FirebaseAuth

class HomeFragment : Fragment() {

    private lateinit var randomMealImage: ImageView
    private lateinit var randomMealName: TextView
    private lateinit var randomMealCategory: TextView
    private lateinit var randomMealArea: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var mealsAdapter: MealsAdapter
    private lateinit var offlineAnimation: LottieAnimationView
    private lateinit var titleMealOfDay : TextView
    private lateinit var titleSpecialMeals : TextView
    private lateinit var swipeRefreshLayout : SwipeRefreshLayout
    private lateinit var mealCard : CardView
    private lateinit var viewModel: MealViewModel
    private lateinit var mealFavoriteIcon: ImageButton
    private lateinit var lottieAnim : LottieAnimationView
    private lateinit var progressBar: ProgressBar

    private val userId: String? by lazy {
        FirebaseAuth.getInstance().currentUser?.uid
    }

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
        titleSpecialMeals = view.findViewById(R.id.titleSpecialMeals)
        titleMealOfDay = view.findViewById(R.id.titleMealOfDay)
        mealCard = view.findViewById(R.id.mealCard)
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)
        offlineAnimation = view.findViewById(R.id.offlineAnimation)
        mealFavoriteIcon = view.findViewById(R.id.MealFavoriteIcon)
        progressBar = view.findViewById(R.id.progressBarHome)
        lottieAnim = view.findViewById(R.id.imageLoadingAnimation)

        mealsAdapter = MealsAdapter(
            onMealClick = { mealId ->
                val action = HomeFragmentDirections.actionHomeFragmentToDetailsFragment(mealId)
                findNavController().navigate(action)
            },
            onFavoriteRequest = { meal, position ->
                checkGuestAction {
                    userId?.let { uid ->
                        if (meal.isFavorite) {
                            requireContext().showConfirmDialog(
                                title = "Remove Favorite",
                                message = "Are you sure you want to remove ${meal.strMeal} from favorites?",
                                onConfirm = {
                                    viewModel.toggleMeal(meal, uid)
                                    meal.isFavorite = false
                                    mealsAdapter.notifyItemChanged(position)

                                    requireView().showStyledSnackBar(
                                        message = "${meal.strMeal} removed from favorites",
                                        actionText = "Undo"
                                    ) {
                                        viewModel.toggleMeal(meal, uid)
                                        meal.isFavorite = true
                                        mealsAdapter.notifyItemChanged(position)
                                    }
                                }
                            )
                        } else {
                            viewModel.toggleMeal(meal, uid)
                            meal.isFavorite = true
                            mealsAdapter.notifyItemChanged(position)

                            requireView().showStyledSnackBar(
                                message = "${meal.strMeal} added to favorites",
                                actionText = "Undo"
                            ) {
                                viewModel.toggleMeal(meal, uid)
                                meal.isFavorite = false
                                mealsAdapter.notifyItemChanged(position)
                            }
                        }
                    }
                }
            }
        )

        val remoteDataSource: MealRemoteDataSource = MealRemoteDataSourceImpl(RetrofitInstance.api)
        val repository = MealRepository(remoteDataSource, LocalDataSourceImpl(requireContext()))
        val factory = MealViewModelFactory(repository, userId.toString())
        viewModel = ViewModelProvider(this, factory).get(MealViewModel::class.java)

        setupObservers(view)
        loadData()

        swipeRefreshLayout.setOnRefreshListener {
            loadData()
        }
    }

    private fun setupObservers(view: View) {
        viewModel.randomMealLiveData.observe(viewLifecycleOwner) { meal ->
            swipeRefreshLayout.isRefreshing = false
            randomMealName.text = meal.strMeal
            randomMealCategory.text = "Category: ${meal.strCategory}"
            randomMealArea.text = "Area: ${meal.strArea}"

            Glide.with(this)
                .load(meal.strMealThumb)
                .listener(object : com.bumptech.glide.request.RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable?>,
                        isFirstResource: Boolean
                    ): Boolean {
                        lottieAnim.visibility = View.GONE
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable,
                        model: Any,
                        target: Target<Drawable?>?,
                        dataSource: DataSource,
                        isFirstResource: Boolean
                    ): Boolean {
                        lottieAnim.visibility = View.GONE
                        return false
                    }
                })
                .into(randomMealImage)

            if (meal.isFavorite) {
                mealFavoriteIcon.setImageResource(R.drawable.heart_fill)
            } else {
                mealFavoriteIcon.setImageResource(R.drawable.heart_outline)
            }

            mealFavoriteIcon.setOnClickListener {
                checkGuestAction {
                    userId?.let { uid ->
                        if (meal.isFavorite) {
                            requireContext().showConfirmDialog(
                                title = "Remove Favorite",
                                message = "Are you sure you want to remove ${meal.strMeal} from favorites?",
                                onConfirm = {
                                    viewModel.toggleMeal(meal, uid)
                                    meal.isFavorite = false
                                    mealFavoriteIcon.setImageResource(R.drawable.heart_outline)

                                    requireView().showStyledSnackBar(
                                        message = "${meal.strMeal} removed from favorites",
                                        actionText = "Undo"
                                    ) {
                                        viewModel.toggleMeal(meal, uid)
                                        meal.isFavorite = true
                                        mealFavoriteIcon.setImageResource(R.drawable.heart_fill)
                                    }
                                }
                            )
                        } else {
                            viewModel.toggleMeal(meal, uid)
                            meal.isFavorite = true
                            mealFavoriteIcon.setImageResource(R.drawable.heart_fill)

                            requireView().showStyledSnackBar(
                                message = "${meal.strMeal} added to favorites",
                                actionText = "Undo"
                            ) {
                                viewModel.toggleMeal(meal, uid)
                                meal.isFavorite = false
                                mealFavoriteIcon.setImageResource(R.drawable.heart_outline)
                            }
                        }
                    }
                }
            }

            view.findViewById<View>(R.id.mealCard).setOnClickListener {
                val action = HomeFragmentDirections.actionHomeFragmentToDetailsFragment(meal.idMeal)
                findNavController().navigate(action)
            }
        }

        viewModel.mealsByLetterLiveData.observe(viewLifecycleOwner) { meals ->
            swipeRefreshLayout.isRefreshing = false
            mealsAdapter.setMeals(meals)
        }

        recyclerView.apply {
            adapter = mealsAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun loadData() {
        if (isNetworkAvailable()) {
            offlineAnimation.cancelAnimation()
            offlineAnimation.visibility = View.GONE

            swipeRefreshLayout.isRefreshing = true
            viewModel.getRandomMeal()
            viewModel.getMealsByLetter(getRandomLetter())
        } else {
            swipeRefreshLayout.isRefreshing = false
            setViewsVisibility(false)
            showOfflineAnimation()
        }
    }

    private fun showOfflineAnimation() {
        offlineAnimation.visibility = View.VISIBLE
        offlineAnimation.playAnimation()
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = requireContext()
            .getSystemService(android.content.Context.CONNECTIVITY_SERVICE) as android.net.ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    private fun setViewsVisibility(isVisible: Boolean) {
        val visibility = if (isVisible) View.VISIBLE else View.GONE
        listOf(
            recyclerView, randomMealImage, randomMealName,
            randomMealCategory, randomMealArea, titleMealOfDay,
            titleSpecialMeals, mealCard
        ).forEach { it.visibility = visibility }
    }

    private fun getRandomLetter(): String {
        val letters = ('a'..'z')
        return letters.random().toString()
    }
}
