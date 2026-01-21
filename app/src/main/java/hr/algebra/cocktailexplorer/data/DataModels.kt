package hr.algebra.cocktailexplorer.data

import android.content.ContentValues
import com.google.gson.annotations.SerializedName
import hr.algebra.cocktailexplorer.data.local.database.CocktailsTable
import hr.algebra.cocktailexplorer.data.local.database.IngredientsTable
import hr.algebra.cocktailexplorer.models.Cocktail

data class CocktailResponse(
    @SerializedName("drinks")
    val drinks: List<CocktailDto>?
)

data class CocktailDto(
    @SerializedName("idDrink")
    val id: String,

    @SerializedName("strDrink")
    val name: String,

    @SerializedName("strDrinkAlternate")
    val nameAlternate: String?,

    @SerializedName("strTags")
    val tags: String?,

    @SerializedName("strVideo")
    val video: String?,

    @SerializedName("strCategory")
    val category: String?,

    @SerializedName("strIBA")
    val iba: String?,

    @SerializedName("strAlcoholic")
    val alcoholic: String?,

    @SerializedName("strGlass")
    val glass: String?,

    @SerializedName("strInstructions")
    val instructions: String?,

    @SerializedName("strInstructionsES")
    val instructionsES: String?,

    @SerializedName("strInstructionsDE")
    val instructionsDE: String?,

    @SerializedName("strInstructionsFR")
    val instructionsFR: String?,

    @SerializedName("strInstructionsIT")
    val instructionsIT: String?,

    @SerializedName("strDrinkThumb")
    val thumbnailUrl: String?,

    @SerializedName("strIngredient1")
    val ingredient1: String?,
    @SerializedName("strIngredient2")
    val ingredient2: String?,
    @SerializedName("strIngredient3")
    val ingredient3: String?,
    @SerializedName("strIngredient4")
    val ingredient4: String?,
    @SerializedName("strIngredient5")
    val ingredient5: String?,
    @SerializedName("strIngredient6")
    val ingredient6: String?,
    @SerializedName("strIngredient7")
    val ingredient7: String?,
    @SerializedName("strIngredient8")
    val ingredient8: String?,
    @SerializedName("strIngredient9")
    val ingredient9: String?,
    @SerializedName("strIngredient10")
    val ingredient10: String?,
    @SerializedName("strIngredient11")
    val ingredient11: String?,
    @SerializedName("strIngredient12")
    val ingredient12: String?,
    @SerializedName("strIngredient13")
    val ingredient13: String?,
    @SerializedName("strIngredient14")
    val ingredient14: String?,
    @SerializedName("strIngredient15")
    val ingredient15: String?,

    @SerializedName("strMeasure1")
    val measure1: String?,
    @SerializedName("strMeasure2")
    val measure2: String?,
    @SerializedName("strMeasure3")
    val measure3: String?,
    @SerializedName("strMeasure4")
    val measure4: String?,
    @SerializedName("strMeasure5")
    val measure5: String?,
    @SerializedName("strMeasure6")
    val measure6: String?,
    @SerializedName("strMeasure7")
    val measure7: String?,
    @SerializedName("strMeasure8")
    val measure8: String?,
    @SerializedName("strMeasure9")
    val measure9: String?,
    @SerializedName("strMeasure10")
    val measure10: String?,
    @SerializedName("strMeasure11")
    val measure11: String?,
    @SerializedName("strMeasure12")
    val measure12: String?,
    @SerializedName("strMeasure13")
    val measure13: String?,
    @SerializedName("strMeasure14")
    val measure14: String?,
    @SerializedName("strMeasure15")
    val measure15: String?,

    @SerializedName("strImageSource")
    val imageSource: String?,

    @SerializedName("strImageAttribution")
    val imageAttribution: String?,

    @SerializedName("strCreativeCommonsConfirmed")
    val creativeCommonsConfirmed: String?,

    @SerializedName("dateModified")
    val dateModified: String?
) {
    fun getIngredientsList(): List<IngredientWithMeasure> {
        val ingredients = mutableListOf<IngredientWithMeasure>()

        val ingredientFields = listOf(
            ingredient1, ingredient2, ingredient3, ingredient4, ingredient5,
            ingredient6, ingredient7, ingredient8, ingredient9, ingredient10,
            ingredient11, ingredient12, ingredient13, ingredient14, ingredient15
        )

        val measureFields = listOf(
            measure1, measure2, measure3, measure4, measure5,
            measure6, measure7, measure8, measure9, measure10,
            measure11, measure12, measure13, measure14, measure15
        )

        for (i in ingredientFields.indices) {
            val ingredient = ingredientFields[i]
            val measure = measureFields[i]

            if (!ingredient.isNullOrBlank()) {
                ingredients.add(
                    IngredientWithMeasure(
                        name = ingredient.trim(),
                        measure = measure?.trim()
                    )
                )
            }
        }

        return ingredients
    }

    fun toCocktail(): Cocktail {
        return Cocktail(
            id = id.toIntOrNull() ?: 0,

            name = name.trim(),

            category = category?.trim() ?: "Unknown",

            alcoholic = alcoholic?.trim() ?: "Unknown",

            glass = glass?.trim() ?: "Unknown",

            instructions = instructions?.trim()
                ?: instructionsENFallback(),

            thumbnailUrl = thumbnailUrl?.trim() ?: "",

            imageUrl = imageSource?.trim() ?: thumbnailUrl?.trim() ?: "",

            ingredients = getIngredientsList(),

            tags = tags
                ?.split(",")
                ?.map { it.trim() }
                ?.filter { it.isNotEmpty() }
                ?: emptyList(),

            video = video,

            isFavorite = false
        )
    }

    private fun instructionsENFallback(): String {
        return instructionsES
            ?: instructionsDE
            ?: instructionsFR
            ?: instructionsIT
            ?: "No instructions available"
    }


}


data class FilterListResponse(
    @SerializedName("drinks")
    val items: List<FilterItem>?
)

data class FilterItem(
    @SerializedName("strCategory")
    val category: String?,

    @SerializedName("strGlass")
    val glass: String?,

    @SerializedName("strIngredient1")
    val ingredient: String?,

    @SerializedName("strAlcoholic")
    val alcoholic: String?
)

data class CocktailEntity(
    val id: Int,
    val name: String,
    val category: String?,
    val alcoholic: String?,
    val glass: String?,
    val instructions: String?,
    val thumbnailUrl: String?,
    val imageUrl: String?,
    val tags: String?,
    val video: String?,
    val isFavorite: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

data class IngredientEntity(
    val id: Int? = null,
    val cocktailId: Int,
    val ingredientName: String,
    val measure: String?
)


data class CategoryEntity(
    val id: Int? = null,
    val name: String
)

data class SearchHistoryEntity(
    val id: Int? = null,
    val query: String,
    val timestamp: Long = System.currentTimeMillis()
)



data class IngredientWithMeasure(
    val name: String,
    val measure: String?
)


data class Category(
    val name: String,
    val count: Int = 0
)

data class GlassType(
    val name: String,
    val count: Int = 0
)

fun CocktailDto.toEntity(): CocktailEntity {
    return CocktailEntity(
        id = id.toInt(),
        name = name,
        category = category,
        alcoholic = alcoholic,
        glass = glass,
        instructions = instructions,
        thumbnailUrl = thumbnailUrl,
        imageUrl = thumbnailUrl,
        tags = tags,
        video = video,
        isFavorite = false
    )
}

fun CocktailEntity.toDomain(ingredients: List<IngredientEntity>): Cocktail {
    return Cocktail(
        id = id,
        name = name,
        category = category ?: "Unknown",
        alcoholic = alcoholic ?: "Unknown",
        glass = glass ?: "Unknown",
        instructions = instructions ?: "",
        thumbnailUrl = thumbnailUrl ?: "",
        imageUrl = imageUrl ?: "",
        ingredients = ingredients.map {
            IngredientWithMeasure(it.ingredientName, it.measure)
        },
        tags = tags?.split(",")?.map { it.trim() } ?: emptyList(),
        video = video,
        isFavorite = isFavorite
    )
}


fun CocktailDto.toDomain(): Cocktail {
    return Cocktail(
        id = id.toInt(),
        name = name,
        category = category ?: "Unknown",
        alcoholic = alcoholic ?: "Unknown",
        glass = glass ?: "Unknown",
        instructions = instructions ?: "",
        thumbnailUrl = thumbnailUrl ?: "",
        imageUrl = thumbnailUrl ?: "",
        ingredients = getIngredientsList(),
        tags = tags?.split(",")?.map { it.trim() } ?: emptyList(),
        video = video,
        isFavorite = false
    )
}

sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String, val throwable: Throwable? = null) : UiState<Nothing>()
    object Empty : UiState<Nothing>()
}

data class CocktailFilter(
    val category: String? = null,
    val alcoholic: String? = null,
    val glass: String? = null,
    val ingredient: String? = null,
    val searchQuery: String? = null
) {
    fun isActive(): Boolean {
        return category != null || alcoholic != null ||
                glass != null || ingredient != null || searchQuery != null
    }

    fun clear(): CocktailFilter {
        return CocktailFilter()
    }
}

// Extension functions to convert entities to ContentValues for ContentProvider
fun CocktailEntity.toContentValues(): ContentValues = ContentValues().apply {
    put(CocktailsTable.COLUMN_ID, id)
    put(CocktailsTable.COLUMN_NAME, name)
    put(CocktailsTable.COLUMN_CATEGORY, category)
    put(CocktailsTable.COLUMN_ALCOHOLIC, alcoholic)
    put(CocktailsTable.COLUMN_GLASS, glass)
    put(CocktailsTable.COLUMN_INSTRUCTIONS, instructions)
    put(CocktailsTable.COLUMN_THUMBNAIL_URL, thumbnailUrl)
    put(CocktailsTable.COLUMN_IMAGE_URL, imageUrl)
    put(CocktailsTable.COLUMN_TAGS, tags)
    put(CocktailsTable.COLUMN_VIDEO, video)
    put(CocktailsTable.COLUMN_IS_FAVORITE, if (isFavorite) 1 else 0)
    put(CocktailsTable.COLUMN_CREATED_AT, createdAt)
    put(CocktailsTable.COLUMN_UPDATED_AT, updatedAt)
}

fun IngredientEntity.toContentValues(): ContentValues = ContentValues().apply {
    put(IngredientsTable.COLUMN_COCKTAIL_ID, cocktailId)
    put(IngredientsTable.COLUMN_INGREDIENT_NAME, ingredientName)
    put(IngredientsTable.COLUMN_MEASURE, measure)
}

fun IngredientWithMeasure.toContentValues(cocktailId: Int): ContentValues = ContentValues().apply {
    put(IngredientsTable.COLUMN_COCKTAIL_ID, cocktailId)
    put(IngredientsTable.COLUMN_INGREDIENT_NAME, name)
    put(IngredientsTable.COLUMN_MEASURE, measure)
}
