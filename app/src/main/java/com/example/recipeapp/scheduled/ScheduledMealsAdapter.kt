package com.example.recipeapp.scheduled

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.recipeapp.R

class ScheduledMealsAdapter(
    private val onMealClick: (mealId: String?) -> Unit,
    private val onDeleteClick: (ScheduledMeal) -> Unit
) : ListAdapter<ScheduledMeal, ScheduledMealsAdapter.ScheduledMealViewHolder>(DiffCallback()) {

    inner class ScheduledMealViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mealName: TextView = itemView.findViewById(R.id.tvMealNameScheduled)
        val mealDate: TextView = itemView.findViewById(R.id.tvMealDateScheduled)
        val btnDelete: Button = itemView.findViewById(R.id.btnDeleteScheduled)
        val mealImage : ImageView = itemView.findViewById(R.id.tvMealImageScheduled)

        fun bind(meal: ScheduledMeal) {
            mealName.text = meal.mealName

            val formattedDate = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault())
                .format(java.util.Date(meal.dateTime))

            mealDate.text = formattedDate

            Glide.with(itemView.context)
                .load(meal.mealThumb)
                .into(mealImage)

            btnDelete.setOnClickListener { onDeleteClick(meal) }
            itemView.setOnClickListener {
                onMealClick(meal.mealId)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduledMealViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_scheduled_meal, parent, false)
        return ScheduledMealViewHolder(view)
    }

    override fun onBindViewHolder(holder: ScheduledMealViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<ScheduledMeal>() {
        override fun areItemsTheSame(oldItem: ScheduledMeal, newItem: ScheduledMeal) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: ScheduledMeal, newItem: ScheduledMeal) =
            oldItem == newItem
    }
}
