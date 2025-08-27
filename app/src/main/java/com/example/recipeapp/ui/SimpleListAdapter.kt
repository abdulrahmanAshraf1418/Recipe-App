package com.example.recipeapp.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.recipeapp.R
import com.example.recipeapp.utils.ImageProvider

class SimpleListAdapter(private val onClick: (String) -> Unit)
    : ListAdapter<String, SimpleListAdapter.VH>(Diff()) {

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tv: TextView = itemView.findViewById(R.id.itemName)
        private val img: ImageView = itemView.findViewById(R.id.itemImage)

        fun bind(txt: String, onClick: (String) -> Unit) {
            tv.text = txt

            when {
                ImageProvider.categoryImages.containsKey(txt) -> {
                    img.setImageResource(ImageProvider.categoryImages[txt]!!)
                }
                ImageProvider.areaImages.containsKey(txt) -> {
                    img.setImageResource(ImageProvider.areaImages[txt]!!)
                }
                else -> {
                    img.setImageResource(ImageProvider.ingredientImage)
                }
            }

            itemView.setOnClickListener { onClick(txt) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_simple, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position), onClick)
    }

    class Diff : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(o: String, n: String) = o == n
        override fun areContentsTheSame(o: String, n: String) = o == n
    }
}
