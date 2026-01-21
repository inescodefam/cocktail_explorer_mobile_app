package hr.algebra.cocktailexplorer.framework

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import hr.algebra.cocktailexplorer.COCKTAIL_PROVIDER_CONTENT_URI
import hr.algebra.cocktailexplorer.INGREDIENT_PROVIDER_CONTENT_URI
import hr.algebra.cocktailexplorer.data.IngredientWithMeasure
import hr.algebra.cocktailexplorer.data.local.database.CocktailsTable
import hr.algebra.cocktailexplorer.data.local.database.IngredientsTable
import hr.algebra.cocktailexplorer.models.Cocktail


fun Context.cursorToCocktailList(cursor: Cursor?, defaultIsFavorite: Boolean? = null): List<Cocktail> {
    val cocktails = mutableListOf<Cocktail>()

    cursor ?: return cocktails
    cursor.moveToPosition(-1)

    while (cursor.moveToNext()) {
        val cocktailId = cursor.getInt(cursor.getColumnIndexOrThrow(CocktailsTable.COLUMN_ID))
        val ingredients = getIngredientsForCocktail(cocktailId)

        val isFavorite = defaultIsFavorite
            ?: (cursor.getInt(cursor.getColumnIndexOrThrow(CocktailsTable.COLUMN_IS_FAVORITE)) == 1)

        cocktails.add(
            Cocktail(
                id = cocktailId,
                name = cursor.getString(cursor.getColumnIndexOrThrow(CocktailsTable.COLUMN_NAME)),
                category = cursor.getString(cursor.getColumnIndexOrThrow(CocktailsTable.COLUMN_CATEGORY)) ?: "Unknown",
                alcoholic = cursor.getString(cursor.getColumnIndexOrThrow(CocktailsTable.COLUMN_ALCOHOLIC)) ?: "Unknown",
                glass = cursor.getString(cursor.getColumnIndexOrThrow(CocktailsTable.COLUMN_GLASS)) ?: "Unknown",
                instructions = cursor.getString(cursor.getColumnIndexOrThrow(CocktailsTable.COLUMN_INSTRUCTIONS)) ?: "",
                thumbnailUrl = cursor.getString(cursor.getColumnIndexOrThrow(CocktailsTable.COLUMN_THUMBNAIL_URL)) ?: "",
                imageUrl = cursor.getString(cursor.getColumnIndexOrThrow(CocktailsTable.COLUMN_IMAGE_URL)) ?: "",
                ingredients = ingredients,
                tags = cursor.getString(cursor.getColumnIndexOrThrow(CocktailsTable.COLUMN_TAGS))
                    ?.split(",")?.map { tag -> tag.trim() } ?: emptyList(),
                video = cursor.getString(cursor.getColumnIndexOrThrow(CocktailsTable.COLUMN_VIDEO)),
                isFavorite = isFavorite
            )
        )
    }
    return cocktails
}

fun Context.getIngredientsForCocktail(cocktailId: Int): List<IngredientWithMeasure> {
    val ingredients = mutableListOf<IngredientWithMeasure>()
    val cursor = contentResolver.query(
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


fun Context.deleteCocktailFromDatabase(cocktailId: Int): Boolean {
    contentResolver.delete(
        INGREDIENT_PROVIDER_CONTENT_URI,
        "${IngredientsTable.COLUMN_COCKTAIL_ID} = ?",
        arrayOf(cocktailId.toString())
    )

    val uri = ContentUris.withAppendedId(COCKTAIL_PROVIDER_CONTENT_URI, cocktailId.toLong())
    val deletedRows = contentResolver.delete(uri, null, null)

    return deletedRows > 0
}


fun List<Cocktail>.filterByCategory(category: String?): List<Cocktail> {
    if (category.isNullOrEmpty()) return this
    return filter { it.category.equals(category, ignoreCase = true) }
}

fun List<Cocktail>.filterByName(query: String?): List<Cocktail> {
    if (query.isNullOrEmpty()) return this
    return filter { it.name.contains(query, ignoreCase = true) }
}

fun List<Cocktail>.extractCategories(): List<String> {
    return map { it.category }
        .distinct()
        .sorted()
}
