package com.example.recipeapp.ui

import IngredientAdapter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.recipeapp.R
import com.example.recipeapp.datdbase.LocalDataSourceImpl
import com.example.recipeapp.models.Ingredient
import com.example.recipeapp.models.Meal
import com.example.recipeapp.network.MealRemoteDataSourceImpl
import com.example.recipeapp.network.RetrofitInstance
import com.example.recipeapp.repository.MealRepository
import com.example.recipeapp.viewmodel.MealViewModel
import com.example.recipeapp.viewmodel.MealViewModelFactory
import com.google.android.material.snackbar.Snackbar

class DetailsFragment : Fragment() {

    private var mealImage: ImageView? = null
    private var Name: TextView? = null
    private var btnFavorite: ImageButton? = null
    private var Category: TextView? = null
    private var Area: TextView? = null
    private var details: TextView? = null
    private var btnMore: TextView? = null
    private var youTubePlayerView: YouTubePlayerView? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewModel : MealViewModel
    private lateinit var YoutubeLabel : TextView
    private var isExpanded = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val remoteDataSource = MealRemoteDataSourceImpl(RetrofitInstance.api)
        val localDataSource = LocalDataSourceImpl(requireContext())
        val repository = MealRepository(remoteDataSource, localDataSource)
        val factory = MealViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(MealViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        setupRecyclerView()
        observeViewModel()
        setupClickListeners()
        viewModel.messageLiveData.observe(viewLifecycleOwner) { message ->
            Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun initViews(view: View) {
        Name = view.findViewById(R.id.MealName)
        btnFavorite = view.findViewById(R.id.btnFavorite)
        btnMore = view.findViewById(R.id.btn_more)
        mealImage = view.findViewById(R.id.meal_image)
        Category = view.findViewById(R.id.MealCategory)
        Area = view.findViewById(R.id.MealArea)
        details = view.findViewById(R.id.details)
        YoutubeLabel = view.findViewById(R.id.youtube_label)
        youTubePlayerView = view.findViewById(R.id.youtube_player_view)
        recyclerView = view.findViewById(R.id.recyclerViewIngredients)

    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.HORIZONTAL,
            false
        )
        recyclerView.adapter = IngredientAdapter(emptyList())
    }
    private fun observeViewModel() {
        val mealId = arguments?.getString("mealId")
        if (mealId.isNullOrEmpty()) {
            return
        }

        if (isNetworkAvailable()) {
            observeOnlineMeal()
            viewModel.getMealById(mealId)

        } else {
            observeOfflineMeal()
            viewModel.getLocalMealById(mealId)
        }

    }

    private fun observeOnlineMeal() {
        youTubePlayerView?.let { playerView ->
            lifecycle.addObserver(playerView)
        }

        viewModel.mealByIdLiveData.observe(viewLifecycleOwner) { meal ->
            meal?.let {
                updateUI(it, isOnline = true)
                setupYouTubeVideo(it.strYoutube)
                setupIngredients(it)
            }
        }
    }

    private fun observeOfflineMeal() {
        viewModel.localMealByIdLiveData.observe(viewLifecycleOwner) { meal ->
            meal?.let {
                updateUI(it, isOnline = false)
                setupIngredients(it)
            }
        }
    }

    private fun updateUI(meal: Meal, isOnline: Boolean) {
        details?.text = meal.strInstructions
        details?.maxLines = 3
        Name?.text = meal.strMeal
        Area?.text = getString(R.string.area_label, meal.strArea)
        Category?.text = getString(R.string.category_label, meal.strCategory)
        mealImage?.let { imageView ->
            Glide.with(this).load(meal.strMealThumb).into(imageView)
        }

        if (isOnline) {
            youTubePlayerView?.visibility = View.VISIBLE
            YoutubeLabel.visibility = View.VISIBLE
        } else {
            youTubePlayerView?.visibility = View.GONE
            YoutubeLabel.visibility = View.GONE
        }

        if (meal.isFavorite) {
            btnFavorite?.setImageResource(R.drawable.heart_fill)
        } else {
            btnFavorite?.setImageResource(R.drawable.heart_outline)
        }

        btnFavorite?.setOnClickListener {
            viewModel.toggleMeal(meal)

            meal.isFavorite = !meal.isFavorite
            if (meal.isFavorite) {
                btnFavorite?.setImageResource(R.drawable.heart_fill)
            } else {
                btnFavorite?.setImageResource(R.drawable.heart_outline)
            }
        }
    }

    private fun setupIngredients(meal: Meal) {
        try {
            val ingredients = meal.getIngredients()
            if (ingredients.isNotEmpty()) {
                recyclerView.adapter = IngredientAdapter(ingredients)
            }
        } catch (e: Exception) {
            recyclerView.adapter = IngredientAdapter(emptyList())
        }
    }

    private fun setupClickListeners() {
        btnMore?.setOnClickListener {
            toggleDescription()
        }
    }

    private fun toggleDescription() {
        if (isExpanded) {
            details?.maxLines = 3
            btnMore?.text = getString(R.string.read_more)
        } else {
            details?.maxLines = Integer.MAX_VALUE
            btnMore?.text = getString(R.string.read_less)
        }
        isExpanded = !isExpanded
    }

    private fun Meal.getIngredients(): List<Ingredient> {
        val ingredients = mutableListOf<Ingredient>()

        val ingredientFields = listOf(
            strIngredient1 to strMeasure1,
            strIngredient2 to strMeasure2,
            strIngredient3 to strMeasure3,
            strIngredient4 to strMeasure4,
            strIngredient5 to strMeasure5,
            strIngredient6 to strMeasure6,
            strIngredient7 to strMeasure7,
            strIngredient8 to strMeasure8,
            strIngredient9 to strMeasure9,
            strIngredient10 to strMeasure10,
            strIngredient11 to strMeasure11,
            strIngredient12 to strMeasure12,
            strIngredient13 to strMeasure13,
            strIngredient14 to strMeasure14,
            strIngredient15 to strMeasure15,
            strIngredient16 to strMeasure16,
            strIngredient17 to strMeasure17,
            strIngredient18 to strMeasure18,
            strIngredient19 to strMeasure19,
            strIngredient20 to strMeasure20
        )

        ingredientFields.forEach { (ingredient, measure) ->
            if (!ingredient.isNullOrEmpty()) {
                val imageUrl = "https://www.themealdb.com/images/ingredients/${ingredient}.png"
                ingredients.add(
                    Ingredient(
                        name = ingredient,
                        measure = measure ?: "",
                        imageUrl = imageUrl
                    )
                )
            }
        }

        return ingredients
    }

    private fun setupYouTubeVideo(youtubeUrl: String?) {
        youtubeUrl?.let { url ->
            val videoId = extractVideoId(url)

            youTubePlayerView?.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                override fun onReady(youTubePlayer: YouTubePlayer) {
                    if (videoId.isNotEmpty()) {
                        youTubePlayer.loadVideo(videoId, 0f)
                    }
                }
            })
        }
    }

    private fun extractVideoId(url: String): String {
        return try {
            when {
                url.contains("watch?v=") -> url.substringAfter("watch?v=").substringBefore("&")
                url.contains("youtu.be/") -> url.substringAfter("youtu.be/").substringBefore("?")
                else -> ""
            }
        } catch (e: Exception) {
            ""
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            requireContext().getSystemService(android.content.Context.CONNECTIVITY_SERVICE) as android.net.ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    override fun onDestroyView() {
        super.onDestroyView()
        youTubePlayerView?.release()
    }}