package com.example.recipeapp.ui

import IngredientAdapter
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.example.recipeapp.R
import com.example.recipeapp.datdbase.LocalDataSourceImpl
import com.example.recipeapp.models.Ingredient
import com.example.recipeapp.models.Meal
import com.example.recipeapp.network.MealRemoteDataSourceImpl
import com.example.recipeapp.network.RetrofitInstance
import com.example.recipeapp.repository.MealRepository
import com.example.recipeapp.scheduled.ScheduledMeal
import com.example.recipeapp.scheduled.ScheduledMealsViewModel
import com.example.recipeapp.scheduled.ScheduledMealsViewModelFactory
import com.example.recipeapp.utils.checkGuestAction
import com.example.recipeapp.utils.showConfirmDialog
import com.example.recipeapp.utils.showStyledSnackBar
import com.example.recipeapp.viewmodel.MealViewModel
import com.example.recipeapp.viewmodel.MealViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import java.util.Calendar

class DetailsFragment : Fragment() {

    private var mealImage: ImageView? = null
    private var Name: TextView? = null
    private var mealFavoriteIcon: ImageButton? = null
    private var Category: TextView? = null
    private var Area: TextView? = null
    private var details: TextView? = null
    private var btnMore: TextView? = null
    private var youTubePlayerView: YouTubePlayerView? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewModel : MealViewModel
    private lateinit var scheduleViewModel : ScheduledMealsViewModel
    private lateinit var btnScheduleMeal : ImageButton
    private lateinit var YoutubeLabel : TextView
    private lateinit var lottieAnim: LottieAnimationView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private var isExpanded = false

    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val remoteDataSource = MealRemoteDataSourceImpl(RetrofitInstance.api)
        val localDataSource = LocalDataSourceImpl(requireContext())
        val repository = MealRepository(remoteDataSource, localDataSource)

        userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        val factory = MealViewModelFactory(repository, userId)
        viewModel = ViewModelProvider(this, factory)[MealViewModel::class.java]

        val scheduleFactory = ScheduledMealsViewModelFactory(repository)
        scheduleViewModel = ViewModelProvider(this, scheduleFactory)[ScheduledMealsViewModel::class.java]

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
        setupSwipeRefresh()
    }

    private fun initViews(view: View) {
        Name = view.findViewById(R.id.MealName)
        mealFavoriteIcon = view.findViewById(R.id.btnFavorite)
        btnMore = view.findViewById(R.id.btn_more)
        mealImage = view.findViewById(R.id.meal_image)
        Category = view.findViewById(R.id.MealCategory)
        Area = view.findViewById(R.id.MealArea)
        details = view.findViewById(R.id.details)
        YoutubeLabel = view.findViewById(R.id.youtube_label)
        youTubePlayerView = view.findViewById(R.id.youtube_player_view)
        recyclerView = view.findViewById(R.id.recyclerViewIngredients)
        lottieAnim = view.findViewById(R.id.imageLoadingAnimation_Details)
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayoutDetails)
        btnScheduleMeal = view.findViewById(R.id.btnScheduleMeal)

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
        if (mealId.isNullOrEmpty()) return

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
                swipeRefreshLayout.isRefreshing = false
            }
        }
    }

    private fun observeOfflineMeal() {
        viewModel.localMealByIdLiveData.observe(viewLifecycleOwner) { meal ->
            meal?.let {
                updateUI(it, isOnline = false)
                setupIngredients(it)
                swipeRefreshLayout.isRefreshing = false
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
            Glide.with(this).load(meal.strMealThumb)
                .listener(object : com.bumptech.glide.request.RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: com.bumptech.glide.request.target.Target<Drawable>,
                        isFirstResource: Boolean
                    ): Boolean {
                        lottieAnim.visibility = View.GONE
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable,
                        model: Any,
                        target: com.bumptech.glide.request.target.Target<Drawable>?,
                        dataSource: DataSource,
                        isFirstResource: Boolean
                    ): Boolean {
                        lottieAnim.visibility = View.GONE
                        return false
                    }
                })
                .into(imageView)
        }

        if (isOnline) {
            youTubePlayerView?.visibility = View.VISIBLE
            YoutubeLabel.visibility = View.VISIBLE
        } else {
            youTubePlayerView?.visibility = View.GONE
            YoutubeLabel.visibility = View.GONE
        }

        mealFavoriteIcon?.setImageResource(
            if (meal.isFavorite) R.drawable.heart_fill else R.drawable.heart_outline
        )

        mealFavoriteIcon?.setOnClickListener {
            checkGuestAction {
                if (meal.isFavorite) {
                    requireContext().showConfirmDialog(
                        title = "Remove Favorite",
                        message = "Are you sure you want to remove ${meal.strMeal} from favorites?",
                        onConfirm = {
                            viewModel.toggleMeal(meal, userId)
                            meal.isFavorite = false
                            mealFavoriteIcon?.setImageResource(R.drawable.heart_outline)

                            requireView().showStyledSnackBar(
                                message = "${meal.strMeal} removed from favorites",
                                actionText = "Undo"
                            ) {
                                viewModel.toggleMeal(meal, userId)
                                meal.isFavorite = true
                                mealFavoriteIcon?.setImageResource(R.drawable.heart_fill)
                            }
                        }
                    )
                } else {
                    viewModel.toggleMeal(meal, userId)
                    meal.isFavorite = true
                    mealFavoriteIcon?.setImageResource(R.drawable.heart_fill)

                    requireView().showStyledSnackBar(
                        message = "${meal.strMeal} added to favorites",
                        actionText = "Undo"
                    ) {
                        viewModel.toggleMeal(meal, userId)
                        meal.isFavorite = false
                        mealFavoriteIcon?.setImageResource(R.drawable.heart_outline)
                    }
                }
            }
        }
    }

    private fun setupIngredients(meal: Meal) {
        try {
            val ingredients = meal.getIngredients()
            recyclerView.adapter = IngredientAdapter(ingredients)
        } catch (_: Exception) {
            recyclerView.adapter = IngredientAdapter(emptyList())
        }
    }

    private fun setupClickListeners() {
        btnMore?.setOnClickListener { toggleDescription() }
        btnScheduleMeal.setOnClickListener {
            val meal = viewModel.mealByIdLiveData.value ?: return@setOnClickListener
            showDateTimePicker { dateTime ->
                val newScheduledMeal = ScheduledMeal(
                    mealId = meal.idMeal,
                    mealName = meal.strMeal ?: "",
                    mealThumb = meal.strMealThumb ?: "",
                    dateTime = dateTime
                )
                scheduleViewModel.addScheduledMeal(newScheduledMeal)

                requireView().showStyledSnackBar(
                    message = "${meal.strMeal} scheduled successfully!"
                )
            }
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

    private fun setupSwipeRefresh() {
        swipeRefreshLayout.setOnRefreshListener {
            val mealId = arguments?.getString("mealId")
            if (!mealId.isNullOrEmpty()) {
                if (isNetworkAvailable()) {
                    viewModel.getMealById(mealId)
                } else {
                    viewModel.getLocalMealById(mealId)
                }
            } else {
                swipeRefreshLayout.isRefreshing = false
            }
        }
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
        } catch (_: Exception) {
            ""
        }
    }

    private fun showDateTimePicker(onDateTimeSelected: (Long) -> Unit) {
        val calendar = Calendar.getInstance()

        DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                TimePickerDialog(
                    requireContext(),
                    { _, hourOfDay, minute ->
                        calendar.set(year, month, dayOfMonth, hourOfDay, minute)
                        onDateTimeSelected(calendar.timeInMillis)
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true
                ).show()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
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
    }
}
