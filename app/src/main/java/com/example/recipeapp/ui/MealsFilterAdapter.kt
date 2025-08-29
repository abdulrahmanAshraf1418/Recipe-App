package com.example.recipeapp.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.recipeapp.R
import com.example.recipeapp.models.MealItem

class MealsFilterAdapter(
    private val onClick: (MealItem) -> Unit
) : RecyclerView.Adapter<MealsFilterAdapter.MealFilterViewHolder>() {

    private var meals = ArrayList<MealItem>()

    fun setMeals(mealsList: List<MealItem>) {
        meals = ArrayList(mealsList)
        notifyDataSetChanged()
    }

    inner class MealFilterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mealName: TextView = itemView.findViewById(R.id.mealName)
        val mealImage: ImageView = itemView.findViewById(R.id.mealImage)

        fun bind(meal: MealItem) {
            mealName.text = meal.strMeal
            Glide.with(itemView)
                .load(meal.strMealThumb)
                .into(mealImage)

            itemView.setOnClickListener {
                onClick(meal)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealFilterViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_meal_filter, parent, false)
        return MealFilterViewHolder(view)
    }

    override fun onBindViewHolder(holder: MealFilterViewHolder, position: Int) {
        holder.bind(meals[position])
    }

    override fun getItemCount() = meals.size
}

