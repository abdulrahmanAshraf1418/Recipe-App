package com.example.recipeapp.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.example.recipeapp.R
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.recipeapp.models.Meal

class MealsAdapter (
    private val onMealClick: (mealId: String?) -> Unit
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
        val mealImage: ImageView = itemView.findViewById(R.id.ingredientImage)


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

        holder.itemView.setOnClickListener {
            onMealClick(meal.idMeal)
        }

    }

    override fun getItemCount() = meals.size
}
