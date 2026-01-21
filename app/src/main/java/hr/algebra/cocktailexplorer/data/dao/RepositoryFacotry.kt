package hr.algebra.cocktailexplorer.data.dao

import android.content.Context

fun getCocktailRepository(context: Context?) = ImplCocktailRepository(context)
