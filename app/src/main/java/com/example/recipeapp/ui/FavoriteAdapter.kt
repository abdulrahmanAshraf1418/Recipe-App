package com.example.recipeapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.recipeapp.R
import com.example.recipeapp.models.Meal

class FavoriteAdapter (
    private val onMealClick: (mealId: String?) -> Unit,
    private val onFavoriteClick: (meal: Meal) -> Unit
) : RecyclerView.Adapter<FavoriteAdapter.FavViewHolder>() {

    private var meals: List<Meal> = ArrayList()

    fun setMeals(meals: List<Meal>) {
        this.meals = meals
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_favorite_meal, parent, false)
        return FavViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavViewHolder, position: Int) {
        val meal = meals[position]

        holder.mealName.text = meal.strMeal
        holder.mealCategory.text = "Category: ${meal.strCategory}"
        holder.mealArea.text = "Area: ${meal.strArea}"
        holder.btnFavorite.setImageResource(R.drawable.heart_fill)

        Glide.with(holder.itemView.context)
            .load(meal.strMealThumb)
            .into(holder.mealImage)

        holder.btnFavorite.setOnClickListener {
            onFavoriteClick(meal)
        }
        holder.itemView.setOnClickListener {
            onMealClick(meal.idMeal)
        }

    }

    override fun getItemCount(): Int = meals.size

    inner class FavViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mealImage: ImageView = itemView.findViewById(R.id.meal_fav_img)
        val mealName: TextView = itemView.findViewById(R.id.meal_fav_name)
        val mealCategory: TextView = itemView.findViewById(R.id.meal_fav_category)
        val mealArea: TextView = itemView.findViewById(R.id.meal_fav_area)
        val btnFavorite : ImageButton = itemView.findViewById(R.id.btn_favorite_item_favorite)
    }

}
