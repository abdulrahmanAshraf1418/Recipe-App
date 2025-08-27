package com.example.recipeapp.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.recipeapp.R
import com.example.recipeapp.models.Meal

class MealSearchAdapter(
    private val onItemClick: (Meal) -> Unit
) : ListAdapter<Meal, MealSearchAdapter.MealViewHolder>(DiffCallback()) {

    class MealViewHolder(itemView: View, val onItemClick: (Meal) -> Unit) :
        RecyclerView.ViewHolder(itemView) {
        private val mealImage: ImageView = itemView.findViewById(R.id.mealImage)
        private val mealName: TextView = itemView.findViewById(R.id.mealName)
        private val mealCategory: TextView = itemView.findViewById(R.id.mealCategory)
        private val mealArea: TextView = itemView.findViewById(R.id.mealArea)

        fun bind(meal: Meal) {
            mealName.text = meal.strMeal
            mealCategory.text = "Category: ${meal.strCategory}"
            mealArea.text = "Area: ${meal.strArea}"

            Glide.with(itemView.context)
                .load(meal.strMealThumb)
                .into(mealImage)

            itemView.setOnClickListener {
                onItemClick(meal)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_meal, parent, false)
        return MealViewHolder(view, onItemClick)
    }

    override fun onBindViewHolder(holder: MealViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<Meal>() {
        override fun areItemsTheSame(oldItem: Meal, newItem: Meal) = oldItem.idMeal == newItem.idMeal
        override fun areContentsTheSame(oldItem: Meal, newItem: Meal) = oldItem == newItem
    }
}
