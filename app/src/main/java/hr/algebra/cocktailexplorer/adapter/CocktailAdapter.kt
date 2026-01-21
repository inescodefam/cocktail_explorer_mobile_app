package hr.algebra.cocktailexplorer.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import hr.algebra.cocktailexplorer.R
import hr.algebra.cocktailexplorer.databinding.ItemCocktailBinding
import hr.algebra.cocktailexplorer.models.Cocktail

class CocktailAdapter(
    private val onItemClick: (Cocktail) -> Unit = {}
) : ListAdapter<Cocktail, CocktailAdapter.CocktailViewHolder>(CocktailDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CocktailViewHolder {
        val binding = ItemCocktailBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CocktailViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CocktailViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CocktailViewHolder(
        private val binding: ItemCocktailBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = this.adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(getItem(position))
                }
            }
        }

        fun bind(cocktail: Cocktail) {
            binding.apply {
                tvName.text = cocktail.name
                tvCategory.text = cocktail.category
                tvAlcoholic.text = cocktail.alcoholic

                Glide.with(ivCocktail.context)
                    .load(cocktail.thumbnailUrl)
                    .placeholder(R.drawable.placeholder_cocktail)
                    .error(R.drawable.placeholder_cocktail)
                    .centerCrop()
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(ivCocktail)
            }
        }
    }

    class CocktailDiffCallback : DiffUtil.ItemCallback<Cocktail>() {
        override fun areItemsTheSame(oldItem: Cocktail, newItem: Cocktail): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Cocktail, newItem: Cocktail): Boolean {
            return oldItem == newItem
        }
    }
}