package hr.algebra.cocktailexplorer

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import hr.algebra.cocktailexplorer.data.dao.CocktailRepository
import hr.algebra.cocktailexplorer.data.dao.getCocktailRepository
import hr.algebra.cocktailexplorer.data.local.database.CocktailsTable
import hr.algebra.cocktailexplorer.data.local.database.IngredientsTable

private const val AUTHORITY = "hr.algebra.cocktailexplorer.provider"
private const val PATH_COCKTAILS = "cocktails"
private const val PATH_INGREDIENTS = "ingredients"

val COCKTAIL_PROVIDER_CONTENT_URI: Uri = Uri.parse("content://$AUTHORITY/$PATH_COCKTAILS")
val INGREDIENT_PROVIDER_CONTENT_URI: Uri = Uri.parse("content://$AUTHORITY/$PATH_INGREDIENTS")

private const val COCKTAILS = 10
private const val COCKTAIL_ID = 11
private const val INGREDIENTS = 20
private const val INGREDIENT_ID = 21

class CocktailContentProvider : ContentProvider() {

    private lateinit var repository: CocktailRepository

    private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
        addURI(AUTHORITY, PATH_COCKTAILS, COCKTAILS)
        addURI(AUTHORITY, "$PATH_COCKTAILS/#", COCKTAIL_ID)
        addURI(AUTHORITY, PATH_INGREDIENTS, INGREDIENTS)
        addURI(AUTHORITY, "$PATH_INGREDIENTS/#", INGREDIENT_ID)
    }

    override fun onCreate(): Boolean {
        repository = getCocktailRepository(context)
        return true
    }

    override fun getType(uri: Uri): String {
        return when (uriMatcher.match(uri)) {
            COCKTAILS -> "vnd.android.cursor.dir/vnd.$AUTHORITY.$PATH_COCKTAILS"
            COCKTAIL_ID -> "vnd.android.cursor.item/vnd.$AUTHORITY.$PATH_COCKTAILS"
            INGREDIENTS -> "vnd.android.cursor.dir/vnd.$AUTHORITY.$PATH_INGREDIENTS"
            INGREDIENT_ID -> "vnd.android.cursor.item/vnd.$AUTHORITY.$PATH_INGREDIENTS"
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
    }

    override fun query(
        uri: Uri,
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor? {
        val cursor: Cursor? = when (uriMatcher.match(uri)) {
            COCKTAILS -> {
                repository.query(projection, selection, selectionArgs, sortOrder)
            }
            COCKTAIL_ID -> {
                val id = ContentUris.parseId(uri)
                repository.query(
                    projection,
                    "${CocktailsTable.COLUMN_ID} = ?",
                    arrayOf(id.toString()),
                    sortOrder
                )
            }
            INGREDIENTS -> {
                (repository as? hr.algebra.cocktailexplorer.data.dao.ImplCocktailRepository)
                    ?.queryIngredients(selection, selectionArgs)
            }
            INGREDIENT_ID -> {
                val id = ContentUris.parseId(uri)
                (repository as? hr.algebra.cocktailexplorer.data.dao.ImplCocktailRepository)
                    ?.queryIngredients(
                        "${IngredientsTable.COLUMN_COCKTAIL_ID} = ?",
                        arrayOf(id.toString())
                    )
            }
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }

        cursor?.setNotificationUri(context?.contentResolver, uri)
        return cursor
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        val id: Long = when (uriMatcher.match(uri)) {
            COCKTAILS -> {
                repository.insert(values)
            }
            INGREDIENTS -> {
                (repository as? hr.algebra.cocktailexplorer.data.dao.ImplCocktailRepository)
                    ?.insertIngredient(values) ?: -1L
            }
            else -> throw IllegalArgumentException("Unknown URI for insert: $uri")
        }

        if (id > 0) {
            context?.contentResolver?.notifyChange(uri, null)
            return ContentUris.withAppendedId(uri, id)
        }
        return null
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        val count: Int = when (uriMatcher.match(uri)) {
            COCKTAILS -> {
                repository.update(values, selection, selectionArgs)
            }
            COCKTAIL_ID -> {
                val id = ContentUris.parseId(uri)
                repository.update(
                    values,
                    "${CocktailsTable.COLUMN_ID} = ?",
                    arrayOf(id.toString())
                )
            }
            else -> throw IllegalArgumentException("Unknown URI for update: $uri")
        }

        if (count > 0) {
            context?.contentResolver?.notifyChange(uri, null)
        }
        return count
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        val count: Int = when (uriMatcher.match(uri)) {
            COCKTAILS -> {
                repository.delete(selection, selectionArgs)
            }
            COCKTAIL_ID -> {
                val id = ContentUris.parseId(uri)
                repository.delete(
                    "${CocktailsTable.COLUMN_ID} = ?",
                    arrayOf(id.toString())
                )
            }
            INGREDIENTS -> {
                (repository as? hr.algebra.cocktailexplorer.data.dao.ImplCocktailRepository)
                    ?.deleteIngredients(selection, selectionArgs) ?: 0
            }
            else -> throw IllegalArgumentException("Unknown URI for delete: $uri")
        }

        if (count > 0) {
            context?.contentResolver?.notifyChange(uri, null)
        }
        return count
    }
}
