import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.recipeapp.R
import com.example.recipeapp.models.Ingredient


class IngredientAdapter(
    private var ingredients : List<Ingredient>
) : RecyclerView.Adapter<IngredientAdapter.IngredientViewHolder>() {

    class IngredientViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ingredientName: TextView = itemView.findViewById(R.id.Ingredient)
        val ingredientMeasure: TextView = itemView.findViewById(R.id.Measure)
        val ingredientImage: ImageView = itemView.findViewById(R.id.ingredientImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_ingredients, parent, false)
        return IngredientViewHolder(view)
    }

    override fun onBindViewHolder(holder: IngredientViewHolder, position: Int) {
        val ingredient = ingredients[position]
        holder.ingredientName.text = ingredient.name
        holder.ingredientMeasure.text = ingredient.measure
        Glide.with(holder.itemView).load(ingredient.imageUrl).into(holder.ingredientImage)
    }

    override fun getItemCount() = ingredients.size
}
