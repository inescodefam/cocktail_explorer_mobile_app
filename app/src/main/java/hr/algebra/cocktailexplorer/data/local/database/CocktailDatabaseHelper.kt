package hr.algebra.cocktailexplorer.data.local.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import hr.algebra.cocktailexplorer.data.CocktailEntity
import hr.algebra.cocktailexplorer.data.IngredientEntity
import androidx.core.database.sqlite.transaction

private val CREATE_TABLE_COCKTAILS = """
             CREATE TABLE IF NOT EXISTS  ${DatabaseConstants.TABLE_COCKTAILS}(
                ${CocktailsTable.COLUMN_ID} INTEGER PRIMARY KEY,
                ${CocktailsTable.COLUMN_NAME} TEXT NOT NULL,
                ${CocktailsTable.COLUMN_CATEGORY} TEXT,
                ${CocktailsTable.COLUMN_ALCOHOLIC} TEXT,
                ${CocktailsTable.COLUMN_GLASS} TEXT,
                ${CocktailsTable.COLUMN_INSTRUCTIONS} TEXT,
                ${CocktailsTable.COLUMN_THUMBNAIL_URL} TEXT,
                ${CocktailsTable.COLUMN_IMAGE_URL} TEXT,
                ${CocktailsTable.COLUMN_TAGS} TEXT,
                ${CocktailsTable.COLUMN_VIDEO} TEXT,
                ${CocktailsTable.COLUMN_IS_FAVORITE} INTEGER DEFAULT 0,
                ${CocktailsTable.COLUMN_CREATED_AT} INTEGER NOT NULL,
                ${CocktailsTable.COLUMN_UPDATED_AT} INTEGER NOT NULL
            )
        """.trimIndent()
private val CREATE_TABLE_INGREDIENTS = """
            CREATE TABLE IF NOT EXISTS  ${DatabaseConstants.TABLE_INGREDIENTS} (
                ${IngredientsTable.COLUMN_ID} INTEGER PRIMARY KEY AUTOINCREMENT,
                ${IngredientsTable.COLUMN_COCKTAIL_ID} INTEGER NOT NULL,
                ${IngredientsTable.COLUMN_INGREDIENT_NAME} TEXT NOT NULL,
                ${IngredientsTable.COLUMN_MEASURE} TEXT,
                FOREIGN KEY (${IngredientsTable.COLUMN_COCKTAIL_ID}) 
                    REFERENCES ${DatabaseConstants.TABLE_COCKTAILS}(${CocktailsTable.COLUMN_ID}) 
                    ON DELETE CASCADE
            )
        """.trimIndent()
private val CREATE_TABLE_CATEGORIES = """
            CREATE TABLE IF NOT EXISTS ${DatabaseConstants.TABLE_CATEGORIES} (
                ${CategoriesTable.COLUMN_ID} INTEGER PRIMARY KEY AUTOINCREMENT,
                ${CategoriesTable.COLUMN_NAME} TEXT UNIQUE NOT NULL
            )
        """.trimIndent()
private val CREATE_TABLE_SEARCH_HISTORY = """
            CREATE TABLE IF NOT EXISTS ${DatabaseConstants.TABLE_SEARCH_HISTORY} (
                ${SearchHistoryTable.COLUMN_ID} INTEGER PRIMARY KEY AUTOINCREMENT,
                ${SearchHistoryTable.COLUMN_QUERY}TEXT NOT NULL,
                ${SearchHistoryTable.COLUMN_TIMESTAMP} INTEGER NOT NULL
            ) 
        """.trimIndent()

private val CREATE_INDEX_ON_TABLE_COCKTAILS_FOR_COLUMN_NAME ="""
            CREATE INDEX IF NOT EXISTS idx_cocktails_name
            ON ${DatabaseConstants.TABLE_COCKTAILS}(${CocktailsTable.COLUMN_NAME})
        """.trimIndent()
private val CREATE_INDEX_ON_TABLE_COCKTAILS_FOR_COLUMN_CATEGORY = """
            CREATE INDEX IF NOT EXISTS idx_cocktails_category
            ON ${DatabaseConstants.TABLE_COCKTAILS}(${CocktailsTable.COLUMN_CATEGORY})
        """.trimIndent()
private val CREATE_INDEX_ON_TABLE_COCKTAILS_COLUMN_IS_FAVORITE ="""
            CREATE INDEX IF NOT EXISTS idx_cocktails_favorite
            ON ${DatabaseConstants.TABLE_COCKTAILS}(${CocktailsTable.COLUMN_IS_FAVORITE})
        """.trimIndent()
private val CREATE_INDEX_ON_TABLE_COCKTAILS_COLUMN_COCKTAIL_ID ="""
            CREATE INDEX IF NOT EXISTS idx_ingredients_cocktail_id
            ON ${DatabaseConstants.TABLE_INGREDIENTS}(${IngredientsTable.COLUMN_COCKTAIL_ID})
        """.trimIndent()

class CocktailDatabaseHelper(context: Context) : SQLiteOpenHelper(
    context,
    DatabaseConstants.DATABASE_NAME,
    null,
    DatabaseConstants.DATABASE_VERSION
) {

    override fun onCreate(db: SQLiteDatabase) {
        Log.d(TAG, "onCreate() - Creating database tables")
        db.execSQL("PRAGMA foreign_keys=ON;")

        db.execSQL(CREATE_TABLE_COCKTAILS)
        Log.d(TAG, "Created ${DatabaseConstants.TABLE_COCKTAILS}")

        db.execSQL(CREATE_TABLE_INGREDIENTS)
        Log.d(TAG, "Created ${DatabaseConstants.TABLE_INGREDIENTS}")

        db.execSQL(CREATE_TABLE_CATEGORIES)
        Log.d(TAG, "Created ${DatabaseConstants.TABLE_CATEGORIES}")

        db.execSQL(CREATE_TABLE_SEARCH_HISTORY)
        Log.d(TAG, "Created ${DatabaseConstants.TABLE_SEARCH_HISTORY}")

        createIndices(db)
        Log.d(TAG, "Database created successfully")
    }

    private fun createIndices(db: SQLiteDatabase) {
        db.execSQL(CREATE_INDEX_ON_TABLE_COCKTAILS_FOR_COLUMN_NAME)
        db.execSQL(CREATE_INDEX_ON_TABLE_COCKTAILS_FOR_COLUMN_CATEGORY)
        db.execSQL(CREATE_INDEX_ON_TABLE_COCKTAILS_COLUMN_IS_FAVORITE)
        db.execSQL(CREATE_INDEX_ON_TABLE_COCKTAILS_COLUMN_COCKTAIL_ID)

        Log.d(TAG, "Indices created successfully")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

        Log.d(TAG, "onUpgrade() - Upgrading from v$oldVersion to v$newVersion")
        db.execSQL("DROP TABLE IF EXISTS ${DatabaseConstants.TABLE_INGREDIENTS}")
        db.execSQL("DROP TABLE IF EXISTS ${DatabaseConstants.TABLE_COCKTAILS}")
        db.execSQL("DROP TABLE IF EXISTS ${DatabaseConstants.TABLE_CATEGORIES}")
        db.execSQL("DROP TABLE IF EXISTS ${DatabaseConstants.TABLE_SEARCH_HISTORY}")

        onCreate(db)
    }

    override fun onOpen(db: SQLiteDatabase) {
        super.onOpen(db)
        db.execSQL("PRAGMA foreign_keys=ON;")
    }

    companion object {
        private const val TAG = "CocktailDB"
    }
}

//class CocktailDao(private val dbHelper: CocktailDatabaseHelper) {
//    private val TAG = "CocktailDao"
//    fun insert(cocktail: CocktailEntity, ingredients: List<IngredientEntity>): Long {
//        val db = dbHelper.writableDatabase
//        var result = -1L
//
//        db.beginTransaction()
//        try {
//            val cocktailValues = ContentValues().apply {
//                put(CocktailsTable.COLUMN_ID, cocktail.id)
//                put(CocktailsTable.COLUMN_NAME, cocktail.name)
//                put(CocktailsTable.COLUMN_CATEGORY, cocktail.category)
//                put(CocktailsTable.COLUMN_ALCOHOLIC, cocktail.alcoholic)
//                put(CocktailsTable.COLUMN_GLASS, cocktail.glass)
//                put(CocktailsTable.COLUMN_INSTRUCTIONS, cocktail.instructions)
//                put(CocktailsTable.COLUMN_THUMBNAIL_URL, cocktail.thumbnailUrl)
//                put(CocktailsTable.COLUMN_IMAGE_URL, cocktail.imageUrl)
//                put(CocktailsTable.COLUMN_TAGS, cocktail.tags)
//                put(CocktailsTable.COLUMN_VIDEO, cocktail.video)
//                put(CocktailsTable.COLUMN_IS_FAVORITE, if (cocktail.isFavorite) 1 else 0)
//                put(CocktailsTable.COLUMN_CREATED_AT, cocktail.createdAt)
//                put(CocktailsTable.COLUMN_UPDATED_AT, cocktail.updatedAt)
//            }
//
//            result = db.insertWithOnConflict(
//                DatabaseConstants.TABLE_COCKTAILS,
//                null,
//                cocktailValues,
//                SQLiteDatabase.CONFLICT_REPLACE
//            )
//
//            ingredients.forEach { ingredient ->
//                val ingredientValues = ContentValues().apply {
//                    put(IngredientsTable.COLUMN_COCKTAIL_ID, cocktail.id)
//                    put(IngredientsTable.COLUMN_INGREDIENT_NAME, ingredient.ingredientName)
//                    put(IngredientsTable.COLUMN_MEASURE, ingredient.measure)
//                }
//
//                db.insert(
//                    DatabaseConstants.TABLE_INGREDIENTS,
//                    null,
//                    ingredientValues
//                )
//            }
//
//            db.setTransactionSuccessful()
//        } catch (e: Exception) {
//            Log.e(TAG, "Error inserting cocktail: ${e.message}", e)
//        } finally {
//            db.endTransaction()
//        }
//        return result
//    }
//
//    fun insertCocktails(cocktails: List<Pair<CocktailEntity, List<IngredientEntity>>>) {
//        val db = dbHelper.writableDatabase
//
//        db.transaction {
//            try {
//                cocktails.forEach { (cocktail, ingredients) ->
//                    insert(cocktail, ingredients)
//                }
//                Log.d(TAG, "Successfully inserted ${cocktails.size} cocktails")
//            } catch (e: Exception) {
//                Log.e(TAG, "Error in batch insert: ${e.message}", e)
//            } finally {
//            }
//        }
//    }
//
//    fun getById(id: Int): Pair<CocktailEntity, List<IngredientEntity>>? {
//        val db = dbHelper.readableDatabase
//
//        val cocktailCursor = db.query(
//            DatabaseConstants.TABLE_COCKTAILS,
//            null,
//            "${CocktailsTable.COLUMN_ID} = ?",
//            arrayOf(id.toString()),
//            null,
//            null,
//            null
//        )
//
//        val cocktail = cocktailCursor.use {
//            if (it.moveToFirst()) {
//                it.toCocktailEntity()
//            } else {
//                null
//            }
//        } ?: return null
//
//        val ingredients = getIngredientsByCocktailId(id)
//
//        return Pair(cocktail, ingredients)
//    }
//
//    fun getAll(): List<Pair<CocktailEntity, List<IngredientEntity>>> {
//        val db = dbHelper.readableDatabase
//        val cocktails = mutableListOf<Pair<CocktailEntity, List<IngredientEntity>>>()
//
//        val cursor = db.query(
//            DatabaseConstants.TABLE_COCKTAILS,
//            null,
//            null,
//            null,
//            null,
//            null,
//            "${CocktailsTable.COLUMN_NAME} ASC"
//        )
//
//        cursor.use {
//            while (it.moveToNext()) {
//                val cocktail = it.toCocktailEntity()
//                val ingredients = getIngredientsByCocktailId(cocktail.id)
//                cocktails.add(Pair(cocktail, ingredients))
//            }
//        }
//
//        return cocktails
//    }
//
//    fun getFavorite(): List<Pair<CocktailEntity, List<IngredientEntity>>> {
//        val db = dbHelper.readableDatabase
//        val cocktails = mutableListOf<Pair<CocktailEntity, List<IngredientEntity>>>()
//
//        val cursor = db.query(
//            DatabaseConstants.TABLE_COCKTAILS,
//            null,
//            "${CocktailsTable.COLUMN_IS_FAVORITE} = 1",
//            null,
//            null,
//            null,
//            "${CocktailsTable.COLUMN_NAME} ASC"
//        )
//
//        cursor.use {
//            while (it.moveToNext()) {
//                val cocktail = it.toCocktailEntity()
//                val ingredients = getIngredientsByCocktailId(cocktail.id)
//                cocktails.add(Pair(cocktail, ingredients))
//            }
//        }
//
//        return cocktails
//    }
//
//    fun search(query: String): List<Pair<CocktailEntity, List<IngredientEntity>>> {
//        val db = dbHelper.readableDatabase
//        val cocktails = mutableListOf<Pair<CocktailEntity, List<IngredientEntity>>>()
//
//        val cursor = db.query(
//            DatabaseConstants.TABLE_COCKTAILS,
//            null,
//            "${CocktailsTable.COLUMN_NAME} LIKE ?",
//            arrayOf("%$query%"),
//            null,
//            null,
//            "${CocktailsTable.COLUMN_NAME} ASC"
//        )
//
//        cursor.use {
//            while (it.moveToNext()) {
//                val cocktail = it.toCocktailEntity()
//                val ingredients = getIngredientsByCocktailId(cocktail.id)
//                cocktails.add(Pair(cocktail, ingredients))
//            }
//        }
//
//        return cocktails
//    }
//
//    fun getByCategory(category: String): List<Pair<CocktailEntity, List<IngredientEntity>>> {
//        val db = dbHelper.readableDatabase
//        val cocktails = mutableListOf<Pair<CocktailEntity, List<IngredientEntity>>>()
//
//        val cursor = db.query(
//            DatabaseConstants.TABLE_COCKTAILS,
//            null,
//            "${CocktailsTable.COLUMN_CATEGORY} = ?",
//            arrayOf(category),
//            null,
//            null,
//            "${CocktailsTable.COLUMN_NAME} ASC"
//        )
//
//        cursor.use {
//            while (it.moveToNext()) {
//                val cocktail = it.toCocktailEntity()
//                val ingredients = getIngredientsByCocktailId(cocktail.id)
//                cocktails.add(Pair(cocktail, ingredients))
//            }
//        }
//
//        return cocktails
//    }
//
//    private fun getIngredientsByCocktailId(cocktailId: Int): List<IngredientEntity> {
//        val db = dbHelper.readableDatabase
//        val ingredients = mutableListOf<IngredientEntity>()
//
//        val cursor = db.query(
//            DatabaseConstants.TABLE_INGREDIENTS,
//            null,
//            "${IngredientsTable.COLUMN_COCKTAIL_ID} = ?",
//            arrayOf(cocktailId.toString()),
//            null,
//            null,
//            null
//        )
//
//        cursor.use {
//            while (it.moveToNext()) {
//                ingredients.add(it.toIngredientEntity())
//            }
//        }
//
//        return ingredients
//    }
//
//    fun updateFavoriteStatus(cocktailId: Int, isFavorite: Boolean): Int {
//        val db = dbHelper.writableDatabase
//
//        val values = ContentValues().apply {
//            put(CocktailsTable.COLUMN_IS_FAVORITE, if (isFavorite) 1 else 0)
//            put(CocktailsTable.COLUMN_UPDATED_AT, System.currentTimeMillis())
//        }
//
//        return db.update(
//            DatabaseConstants.TABLE_COCKTAILS,
//            values,
//            "${CocktailsTable.COLUMN_ID} = ?",
//            arrayOf(cocktailId.toString())
//        )
//    }
//
//    fun updateCocktail(cocktail: CocktailEntity): Int {
//        val db = dbHelper.writableDatabase
//
//        val values = ContentValues().apply {
//            put(CocktailsTable.COLUMN_NAME, cocktail.name)
//            put(CocktailsTable.COLUMN_CATEGORY, cocktail.category)
//            put(CocktailsTable.COLUMN_ALCOHOLIC, cocktail.alcoholic)
//            put(CocktailsTable.COLUMN_GLASS, cocktail.glass)
//            put(CocktailsTable.COLUMN_INSTRUCTIONS, cocktail.instructions)
//            put(CocktailsTable.COLUMN_THUMBNAIL_URL, cocktail.thumbnailUrl)
//            put(CocktailsTable.COLUMN_IMAGE_URL, cocktail.imageUrl)
//            put(CocktailsTable.COLUMN_IS_FAVORITE, if (cocktail.isFavorite) 1 else 0)
//            put(CocktailsTable.COLUMN_UPDATED_AT, System.currentTimeMillis())
//        }
//
//        return db.update(
//            DatabaseConstants.TABLE_COCKTAILS,
//            values,
//            "${CocktailsTable.COLUMN_ID} = ?",
//            arrayOf(cocktail.id.toString())
//        )
//    }
//
//    fun deleteCocktail(cocktailId: Int): Int {
//        val db = dbHelper.writableDatabase
//
//        return db.delete(
//            DatabaseConstants.TABLE_COCKTAILS,
//            "${CocktailsTable.COLUMN_ID} = ?",
//            arrayOf(cocktailId.toString())
//        )
//    }
//
//    fun deleteAll(): Int {
//        val db = dbHelper.writableDatabase
//        return db.delete(DatabaseConstants.TABLE_COCKTAILS, null, null)
//    }
//
//    fun deleteNonFavorites(): Int {
//        val db = dbHelper.writableDatabase
//
//        return db.delete(
//            DatabaseConstants.TABLE_COCKTAILS,
//            "${CocktailsTable.COLUMN_IS_FAVORITE} = 0",
//            null
//        )
//    }
//}
//private fun Cursor.toCocktailEntity(): CocktailEntity {
//    return CocktailEntity(
//        id = getInt(getColumnIndexOrThrow(CocktailsTable.COLUMN_ID)),
//        name = getString(getColumnIndexOrThrow(CocktailsTable.COLUMN_NAME)),
//        category = getString(getColumnIndexOrThrow(CocktailsTable.COLUMN_CATEGORY)),
//        alcoholic = getString(getColumnIndexOrThrow(CocktailsTable.COLUMN_ALCOHOLIC)),
//        glass = getString(getColumnIndexOrThrow(CocktailsTable.COLUMN_GLASS)),
//        instructions = getString(getColumnIndexOrThrow(CocktailsTable.COLUMN_INSTRUCTIONS)),
//        thumbnailUrl = getString(getColumnIndexOrThrow(CocktailsTable.COLUMN_THUMBNAIL_URL)),
//        imageUrl = getString(getColumnIndexOrThrow(CocktailsTable.COLUMN_IMAGE_URL)),
//        tags = getString(getColumnIndexOrThrow(CocktailsTable.COLUMN_TAGS)),
//        video = getString(getColumnIndexOrThrow(CocktailsTable.COLUMN_VIDEO)),
//        isFavorite = getInt(getColumnIndexOrThrow(CocktailsTable.COLUMN_IS_FAVORITE)) == 1,
//        createdAt = getLong(getColumnIndexOrThrow(CocktailsTable.COLUMN_CREATED_AT)),
//        updatedAt = getLong(getColumnIndexOrThrow(CocktailsTable.COLUMN_UPDATED_AT))
//    )
//}
//
//private fun Cursor.toIngredientEntity(): IngredientEntity {
//    return IngredientEntity(
//        id = getInt(getColumnIndexOrThrow(IngredientsTable.COLUMN_ID)),
//        cocktailId = getInt(getColumnIndexOrThrow(IngredientsTable.COLUMN_COCKTAIL_ID)),
//        ingredientName = getString(getColumnIndexOrThrow(IngredientsTable.COLUMN_INGREDIENT_NAME)),
//        measure = getString(getColumnIndexOrThrow(IngredientsTable.COLUMN_MEASURE))
//    )
//}