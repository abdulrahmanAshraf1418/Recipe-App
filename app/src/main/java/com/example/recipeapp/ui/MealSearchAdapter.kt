package com.example.recipeapp.ui

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

class MealSearchAdapter(
    private val onMealClick: (Meal) -> Unit,
    private val onFavoriteClick: (Meal) -> Unit
) : RecyclerView.Adapter<MealSearchAdapter.MealViewHolder>() {

    private var meals = ArrayList<Meal>()

    fun submitList(list: List<Meal>) {
        meals = ArrayList(list)
        notifyDataSetChanged()
    }

    inner class MealViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mealName: TextView = itemView.findViewById(R.id.mealName)
        val mealImage: ImageView = itemView.findViewById(R.id.mealImage)
        val btnFavorite: ImageButton = itemView.findViewById(R.id.mealFavoriteIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_search_meal, parent, false)
        return MealViewHolder(view)
    }

    override fun onBindViewHolder(holder: MealViewHolder, position: Int) {
        val meal = meals[position]

        holder.mealName.text = meal.strMeal
        Glide.with(holder.itemView).load(meal.strMealThumb).into(holder.mealImage)

        holder.btnFavorite.setImageResource(
            if (meal.isFavorite) R.drawable.heart_fill else R.drawable.heart_outline
        )

        holder.btnFavorite.setOnClickListener {
            if (meal.isFavorite) {
                // ✅ استخدم الـ reusable dialog
                holder.itemView.context.showConfirmDialog(
                    title = "Remove Favorite",
                    message = "Are you sure you want to remove ${meal.strMeal} from favorites?",
                    onConfirm = {
                        // شيل الوجبة من المفضلة
                        onFavoriteClick(meal)
                        notifyItemChanged(position)

                        // ✅ Snackbar مع Undo
                        Snackbar.make(holder.itemView, "${meal.strMeal} removed from favorites", Snackbar.LENGTH_LONG)
                            .setAction("Undo") {
                                onFavoriteClick(meal) // رجعها
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

        holder.itemView.setOnClickListener { onMealClick(meal) }
    }

    override fun getItemCount() = meals.size
}

