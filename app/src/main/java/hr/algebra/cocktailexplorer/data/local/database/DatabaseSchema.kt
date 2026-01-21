package hr.algebra.cocktailexplorer.data.local.database

object DatabaseConstants {
    const val DATABASE_NAME = "cocktail_explorer.db"
    const val DATABASE_VERSION = 1

    const val TABLE_COCKTAILS = "cocktails"
    const val TABLE_INGREDIENTS = "ingredients"
    const val TABLE_CATEGORIES = "categories"
    const val TABLE_SEARCH_HISTORY = "search_history"
}

object CocktailsTable {
    const val TABLE_NAME = DatabaseConstants.TABLE_COCKTAILS

    const val COLUMN_ID = "id"
    const val COLUMN_NAME = "name"
    const val COLUMN_CATEGORY = "category"
    const val COLUMN_ALCOHOLIC = "alcoholic"
    const val COLUMN_GLASS = "glass"
    const val COLUMN_INSTRUCTIONS = "instructions"
    const val COLUMN_THUMBNAIL_URL = "thumbnail_url"
    const val COLUMN_IMAGE_URL = "image_url"
    const val COLUMN_TAGS = "tags"
    const val COLUMN_VIDEO = "video"
    const val COLUMN_IS_FAVORITE = "is_favorite"
    const val COLUMN_CREATED_AT = "created_at"
    const val COLUMN_UPDATED_AT = "updated_at"
}

object IngredientsTable {
    const val TABLE_NAME = DatabaseConstants.TABLE_INGREDIENTS

    const val COLUMN_ID = "id"
    const val COLUMN_COCKTAIL_ID = "cocktail_id"
    const val COLUMN_INGREDIENT_NAME = "ingredient_name"
    const val COLUMN_MEASURE = "measure"
}

object CategoriesTable {
    const val TABLE_NAME = DatabaseConstants.TABLE_CATEGORIES

    const val COLUMN_ID = "id"
    const val COLUMN_NAME = "name"
}

object SearchHistoryTable {
    const val TABLE_NAME = DatabaseConstants.TABLE_SEARCH_HISTORY

    const val COLUMN_ID = "id"
    const val COLUMN_QUERY = "query"
    const val COLUMN_TIMESTAMP = "timestamp"
}