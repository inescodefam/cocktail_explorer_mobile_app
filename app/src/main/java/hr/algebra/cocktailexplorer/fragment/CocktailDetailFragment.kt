package hr.algebra.cocktailexplorer.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.ContentValues
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresPermission
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import hr.algebra.cocktailexplorer.COCKTAIL_PROVIDER_CONTENT_URI
import hr.algebra.cocktailexplorer.INGREDIENT_PROVIDER_CONTENT_URI
import hr.algebra.cocktailexplorer.R
import hr.algebra.cocktailexplorer.data.IngredientWithMeasure
import hr.algebra.cocktailexplorer.data.local.database.CocktailsTable
import hr.algebra.cocktailexplorer.data.local.database.IngredientsTable
import hr.algebra.cocktailexplorer.databinding.FragmentCocktailDetailBinding
import hr.algebra.cocktailexplorer.models.Cocktail
import hr.algebra.cocktailexplorer.notification.NotificationHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CocktailDetailFragment : Fragment() {

    private var _binding: FragmentCocktailDetailBinding? = null
    private val binding get() = _binding!!
    private var cocktailId: Int = -1
    private var currentCocktail: Cocktail? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCocktailDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cocktailId = arguments?.getInt(ARG_COCKTAIL_ID, -1) ?: -1
        if (cocktailId != -1) {
            loadCocktailDetails()
        }
        setupFavoriteButton()
    }

    private fun loadCocktailDetails() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val cocktail = withContext(Dispatchers.IO) {
                    loadCocktailFromContentProvider(cocktailId)
                }
                cocktail?.let {
                    currentCocktail = it
                    displayCocktail(it)
                }
            } catch (e: Exception) {
                Toast.makeText(
                    requireContext(),
                    "Error loading details: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun loadCocktailFromContentProvider(id: Int): Cocktail? {
        val uri = ContentUris.withAppendedId(COCKTAIL_PROVIDER_CONTENT_URI, id.toLong())
        val cursor = requireContext().contentResolver.query(
            uri,
            null,
            null,
            null,
            null
        )

        cursor?.use {
            if (it.moveToFirst()) {
                val ingredients = getIngredientsForCocktail(id)

                return Cocktail(
                    id = it.getInt(it.getColumnIndexOrThrow(CocktailsTable.COLUMN_ID)),
                    name = it.getString(it.getColumnIndexOrThrow(CocktailsTable.COLUMN_NAME)),
                    category = it.getString(it.getColumnIndexOrThrow(CocktailsTable.COLUMN_CATEGORY)) ?: "Unknown",
                    alcoholic = it.getString(it.getColumnIndexOrThrow(CocktailsTable.COLUMN_ALCOHOLIC)) ?: "Unknown",
                    glass = it.getString(it.getColumnIndexOrThrow(CocktailsTable.COLUMN_GLASS)) ?: "Unknown",
                    instructions = it.getString(it.getColumnIndexOrThrow(CocktailsTable.COLUMN_INSTRUCTIONS)) ?: "",
                    thumbnailUrl = it.getString(it.getColumnIndexOrThrow(CocktailsTable.COLUMN_THUMBNAIL_URL)) ?: "",
                    imageUrl = it.getString(it.getColumnIndexOrThrow(CocktailsTable.COLUMN_IMAGE_URL)) ?: "",
                    ingredients = ingredients,
                    tags = it.getString(it.getColumnIndexOrThrow(CocktailsTable.COLUMN_TAGS))
                        ?.split(",")?.map { tag -> tag.trim() } ?: emptyList(),
                    video = it.getString(it.getColumnIndexOrThrow(CocktailsTable.COLUMN_VIDEO)),
                    isFavorite = it.getInt(it.getColumnIndexOrThrow(CocktailsTable.COLUMN_IS_FAVORITE)) == 1
                )
            }
        }
        return null
    }

    private fun getIngredientsForCocktail(cocktailId: Int): List<IngredientWithMeasure> {
        val ingredients = mutableListOf<IngredientWithMeasure>()
        val cursor = requireContext().contentResolver.query(
            INGREDIENT_PROVIDER_CONTENT_URI,
            null,
            "${IngredientsTable.COLUMN_COCKTAIL_ID} = ?",
            arrayOf(cocktailId.toString()),
            null
        )

        cursor?.use {
            while (it.moveToNext()) {
                ingredients.add(
                    IngredientWithMeasure(
                        name = it.getString(it.getColumnIndexOrThrow(IngredientsTable.COLUMN_INGREDIENT_NAME)),
                        measure = it.getString(it.getColumnIndexOrThrow(IngredientsTable.COLUMN_MEASURE))
                    )
                )
            }
        }
        return ingredients
    }

    private fun displayCocktail(cocktail: Cocktail) {
        binding.apply {
            tvName.text = cocktail.name
            chipCategory.text = cocktail.category
            chipAlcoholic.text = cocktail.alcoholic
            chipGlass.text = cocktail.glass
            tvIngredients.text = cocktail.getIngredientsText()
            tvInstructions.text = cocktail.instructions

            updateFavoriteIcon(cocktail.isFavorite)

            Glide.with(requireContext())
                .load(cocktail.thumbnailUrl)
                .placeholder(R.drawable.placeholder_cocktail)
                .error(R.drawable.placeholder_cocktail)
                .centerCrop()
                .into(ivCocktailImage)
        }
    }


    private fun setupFavoriteButton() {
        binding.btnFavorite.setOnClickListener {
            currentCocktail?.let { cocktail ->
                val newFavoriteStatus = !cocktail.isFavorite

                viewLifecycleOwner.lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        val uri = ContentUris.withAppendedId(COCKTAIL_PROVIDER_CONTENT_URI, cocktail.id.toLong())
                        val values = ContentValues().apply {
                            put(CocktailsTable.COLUMN_IS_FAVORITE, if (newFavoriteStatus) 1 else 0)
                            put(CocktailsTable.COLUMN_UPDATED_AT, System.currentTimeMillis())
                        }
                        requireContext().contentResolver.update(uri, values, null, null)
                    }

                    currentCocktail = cocktail.copy(isFavorite = newFavoriteStatus)
                    updateFavoriteIcon(newFavoriteStatus)

                    val message = if (newFavoriteStatus) {
                        getString(R.string.success_added_favorite)
                    } else {
                        getString(R.string.success_removed_favorite)
                    }
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()

                    if (newFavoriteStatus) {
                        sendFavoriteNotification(cocktail)
                    }
                }
            }
        }
    }


    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private fun sendFavoriteNotification(cocktail: Cocktail) {
        val notificationHelper = NotificationHelper(requireContext())

        notificationHelper.createNotificationChannel()

        notificationHelper.sendFavoriteAddedNotification(cocktail.name, cocktail.id)
    }

    private fun updateFavoriteIcon(isFavorite: Boolean) {
        val icon = if (isFavorite) {
            R.drawable.ic_favorite
        } else {
            R.drawable.ic_favorite_border
        }
        binding.btnFavorite.setImageResource(icon)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val ARG_COCKTAIL_ID = "cocktail_id"

        fun newInstance(cocktailId: Int): CocktailDetailFragment {
            return CocktailDetailFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COCKTAIL_ID, cocktailId)
                }
            }
        }
    }
}
