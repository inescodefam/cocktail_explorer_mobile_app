package hr.algebra.cocktailexplorer.api

import android.content.Context
import android.content.Intent
import android.util.Log
import hr.algebra.cocktailexplorer.COCKTAIL_PROVIDER_CONTENT_URI
import hr.algebra.cocktailexplorer.INGREDIENT_PROVIDER_CONTENT_URI
import hr.algebra.cocktailexplorer.Receiver
import hr.algebra.cocktailexplorer.data.*
import hr.algebra.cocktailexplorer.framework.sendBroadcast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

// inicijaliziramo interface
class CocktailFetcher(private val context: Context) {

    private var cocktailApi: CocktailApi

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        cocktailApi = retrofit.create<CocktailApi>()
    }

    private val TAG = "CocktailFetcher"

    // private val dao = CocktailDao(CocktailDatabaseHelper(context))

    suspend fun fetchCocktails() {
        try {
            val cocktails = mutableListOf<CocktailDto>()

            for (letter in 'a'..'z') {
                try {
                    val response = withContext(Dispatchers.IO) {
                        cocktailApi.fetchByLetterSuspend(letter)
                    }

                    if (response.isSuccessful) {
                        val drinks = response.body()?.drinks ?: emptyList()
                        cocktails.addAll(drinks)
                        Log.d(TAG, "Fetched ${drinks.size} cocktails for letter $letter")
                    } else {
                        Log.w(TAG, "Failed to fetch cocktails for letter $letter: ${response.code()}")
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error fetching cocktails for letter $letter: ${e.message}")
                }
            }

            if (cocktails.isNotEmpty()) {
                withContext(Dispatchers.IO) {
                    saveToDatabase(cocktails)
                }

                val intent = Intent(context, Receiver::class.java).apply {
                    action = "hr.algebra.cocktailexplorer.ACTION_DATA_IMPORTED"
                }
                context.sendBroadcast(intent)
                Log.d(TAG, "Successfully imported ${cocktails.size} cocktails")
            } else {
                Log.w(TAG, "No cocktails fetched from API")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in fetchCocktails: ${e.message}", e)
        }
    }

    private fun saveToDatabase(cocktails: List<CocktailDto>) {
        val contentResolver = context.contentResolver

        cocktails.forEach { dto ->
            try {
                val entity = dto.toEntity()
                contentResolver.insert(COCKTAIL_PROVIDER_CONTENT_URI, entity.toContentValues())

                dto.getIngredientsList().forEach { ingredient ->
                    contentResolver.insert(
                        INGREDIENT_PROVIDER_CONTENT_URI,
                        ingredient.toContentValues(dto.id.toInt())
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error inserting cocktail ${dto.id}: ${e.message}")
            }
        }

        context.sendBroadcast<Receiver>()
    }


    /*
    private fun saveToDatabase(cocktails: List<CocktailDto>) {
        val cocktailsWithIngredients = cocktails.map { dto ->
            val entity = dto.toEntity()
            val ingredients = dto.getIngredientsList().map { ingredient ->
                IngredientEntity(
                    cocktailId = dto.id.toInt(),
                    ingredientName = ingredient.name,
                    measure = ingredient.measure
                )
            }
            Pair(entity, ingredients)
        }

        dao.insertCocktails(cocktailsWithIngredients)
        context.sendBroadcast<Receiver>()
    }
    */


//    private fun populate(cocktails: MutableList<CocktailDto>) {
//
//        //val coctailsBaselist = cocktails.map {it.toCocktail()}
//
//    }

    // populateItems
    /* fg

    val items = mutable list
    bg

    val scope CorutineScope

    * coctailItems.forEach
    * val picturePath = null
    *
    *
    * broadcast



    fun download image

    fun create file


user gaent mozilaa !!!
    
    * */
}
