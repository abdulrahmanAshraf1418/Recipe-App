package com.example.recipeapp.ui

import android.app.AlertDialog
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
import com.example.recipeapp.utils.showConfirmDialog
import com.google.android.material.snackbar.Snackbar

class MealsAdapter(
    private val onMealClick: (mealId: String?) -> Unit,
    private val onFavoriteClick: (meal: Meal) -> Unit
) : RecyclerView.Adapter<MealsAdapter.MealViewHolder>() {

    private var meals = ArrayList<Meal>()

    fun setMeals(mealsList: List<Meal>) {
        meals = ArrayList(mealsList)
        notifyDataSetChanged()
    }

    inner class MealViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mealName: TextView = itemView.findViewById(R.id.mealName)
        val mealCategory: TextView = itemView.findViewById(R.id.mealCategory)
        val mealArea: TextView = itemView.findViewById(R.id.mealArea)
        val mealImage: ImageView = itemView.findViewById(R.id.mealImage)
        val btnFavorite: ImageButton = itemView.findViewById(R.id.mealFavoriteIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_meal, parent, false)
        return MealViewHolder(view)
    }

    override fun onBindViewHolder(holder: MealViewHolder, position: Int) {
        val meal = meals[position]

        holder.mealName.text = meal.strMeal
        holder.mealCategory.text = "Category: ${meal.strCategory}"
        holder.mealArea.text = "Area: ${meal.strArea}"
        Glide.with(holder.itemView).load(meal.strMealThumb).into(holder.mealImage)

        holder.btnFavorite.setImageResource(
            if (meal.isFavorite) R.drawable.heart_fill else R.drawable.heart_outline
        )

        holder.btnFavorite.setOnClickListener {
            if (meal.isFavorite) {
                holder.itemView.context.showConfirmDialog(
                    title = "Remove Favorite",
                    message = "Are you sure you want to remove ${meal.strMeal} from favorites?",
                    onConfirm = {
                        onFavoriteClick(meal)
                        notifyItemChanged(position)

                        Snackbar.make(holder.itemView, "${meal.strMeal} removed from favorites", Snackbar.LENGTH_LONG)
                            .setAction("Undo") {
                                onFavoriteClick(meal)
                                notifyItemChanged(position)
                            }
                            .show()
                    }
                )
            } else {
                onFavoriteClick(meal)
                notifyItemChanged(position)

                Snackbar.make(holder.itemView, "${meal.strMeal} added to favorites", Snackbar.LENGTH_LONG)
                    .setAction("Undo") {
                        onFavoriteClick(meal)
                        notifyItemChanged(position)
                    }
                    .show()
            }
        }

        holder.itemView.setOnClickListener {
            onMealClick(meal.idMeal)
        }
    }

    override fun getItemCount() = meals.size
}
