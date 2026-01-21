package hr.algebra.cocktailexplorer.data.dao

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import hr.algebra.cocktailexplorer.data.local.database.CocktailDatabaseHelper
import hr.algebra.cocktailexplorer.data.local.database.CocktailsTable
import hr.algebra.cocktailexplorer.data.local.database.DatabaseConstants


class ImplCocktailRepository(context: Context?) : CocktailRepository {

    private val dbHelper: CocktailDatabaseHelper = CocktailDatabaseHelper(context!!)

    override fun delete(selection: String?, selectionArgs: Array<String>?): Int {
        val db = dbHelper.writableDatabase
        return db.delete(
            DatabaseConstants.TABLE_COCKTAILS,
            selection,
            selectionArgs
        )
    }

    override fun insert(values: ContentValues?): Long {
        if (values == null) return -1L

        val db = dbHelper.writableDatabase
        return db.insertWithOnConflict(
            DatabaseConstants.TABLE_COCKTAILS,
            null,
            values,
            android.database.sqlite.SQLiteDatabase.CONFLICT_REPLACE
        )
    }

    override fun query(
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor {
        val db = dbHelper.readableDatabase
        return db.query(
            DatabaseConstants.TABLE_COCKTAILS,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            sortOrder ?: "${CocktailsTable.COLUMN_NAME} ASC"
        )
    }

    override fun update(
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        if (values == null) return 0

        val db = dbHelper.writableDatabase
        return db.update(
            DatabaseConstants.TABLE_COCKTAILS,
            values,
            selection,
            selectionArgs
        )
    }

    fun insertIngredient(values: ContentValues?): Long {
        if (values == null) return -1L

        val db = dbHelper.writableDatabase
        return db.insert(
            DatabaseConstants.TABLE_INGREDIENTS,
            null,
            values
        )
    }

    fun queryIngredients(
        selection: String?,
        selectionArgs: Array<String>?
    ): Cursor {
        val db = dbHelper.readableDatabase
        return db.query(
            DatabaseConstants.TABLE_INGREDIENTS,
            null,
            selection,
            selectionArgs,
            null,
            null,
            null
        )
    }

    fun deleteIngredients(selection: String?, selectionArgs: Array<String>?): Int {
        val db = dbHelper.writableDatabase
        return db.delete(
            DatabaseConstants.TABLE_INGREDIENTS,
            selection,
            selectionArgs
        )
    }
}
