package hr.algebra.cocktailexplorer.models

import hr.algebra.cocktailexplorer.data.IngredientWithMeasure

data class Cocktail(
    val id: Int,
    val name: String,
    val category: String,
    val alcoholic: String,
    val glass: String,
    val instructions: String,
    val thumbnailUrl: String,
    val imageUrl: String,
    val ingredients: List<IngredientWithMeasure>,
    val tags: List<String>,
    val video: String?,
    val isFavorite: Boolean = false
) {

    fun getCategoryDisplay(): String {
        return when (category) {
            "Ordinary Drink" -> "Ordinary Drink"
            "Cocktail" -> "Cocktail"
            "Shot" -> "Shot"
            "Coffee / Tea" -> "Coffee / Tea"
            "Homemade Liqueur" -> "Homemade Liqueur"
            "Punch / Party Drink" -> "Punch / Party Drink"
            "Beer" -> "Beer"
            "Soft Drink" -> "Soft Drink"
            else -> category
        }
    }

    fun isAlcoholic(): Boolean = alcoholic == "Alcoholic"

    fun getIngredientsText(): String {
        return ingredients.joinToString("\n") {
            "• ${it.measure?.let { m -> "$m " } ?: ""}${it.name}"
        }
    }
}