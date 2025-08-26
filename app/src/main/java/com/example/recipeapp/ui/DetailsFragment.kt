package com.example.recipeapp.ui

import IngredientAdapter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
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
import com.example.recipeapp.models.Ingredient
import com.example.recipeapp.models.Meal
import com.example.recipeapp.network.MealRemoteDataSourceImpl
import com.example.recipeapp.network.RetrofitInstance
import com.example.recipeapp.repository.MealRepository
import com.example.recipeapp.viewmodel.MealViewModel
import com.example.recipeapp.viewmodel.MealViewModelFactory

class DetailsFragment : Fragment() {

    private var mealImage: ImageView? = null
    private var Name: TextView? = null
    private var btnFavorite: Button? = null
    private var Category: TextView? = null
    private var Area: TextView? = null
    private var details: TextView? = null
    private var btnMore: TextView? = null
    private var youTubePlayerView: YouTubePlayerView? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewModel : MealViewModel
    private var isExpanded = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val remoteDataSource = MealRemoteDataSourceImpl(RetrofitInstance.api)
        val repository = MealRepository(remoteDataSource)
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

        Name = view.findViewById(R.id.MealName)
        btnFavorite = view.findViewById(R.id.btnFavorites)
        btnMore = view.findViewById(R.id.btn_more)
        mealImage = view.findViewById(R.id.meal_image)
        Category = view.findViewById(R.id.MealCategory)
        Area = view.findViewById(R.id.MealArea)
        details = view.findViewById(R.id.details)
        youTubePlayerView = view.findViewById(R.id.youtube_player_view)
        recyclerView = view.findViewById(R.id.recyclerViewIngredients)
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        // إعداد YouTube Player
        youTubePlayerView?.let { playerView ->
            lifecycle.addObserver(playerView)
        }

        // إعداد الـ Observer
        viewModel.mealByIdLiveData.observe(viewLifecycleOwner) { meal ->
            meal?.let {
                details?.text = it.strInstructions
                details?.maxLines = 3
                mealImage?.let { imageView ->
                    Glide.with(this).load(it.strMealThumb).into(imageView)
                }
                Name?.text = it.strMeal
                Area?.text = "Area: ${it.strArea}"
                Category?.text = "Category: ${it.strCategory}"

                // تشغيل YouTube Video
                setupYouTubeVideo(it.strYoutube)

                val ingredients = meal.getIngredients()

                recyclerView.apply {
                    adapter = IngredientAdapter(ingredients)
                    layoutManager =
                        LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                }
            }

        }

        btnMore?.setOnClickListener {

            if (isExpanded) {
                details?.maxLines = 3
                btnMore?.text = "Read More"
            } else {
                details?.maxLines = Integer.MAX_VALUE
                btnMore?.text = "Read Less"
            }
            isExpanded = !isExpanded
        }

        val mealId = arguments?.getString("mealId")
        view.post {
            viewModel.getMealById(mealId!!)
        }
    }

    fun Meal.getIngredients(): List<Ingredient> {
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
}