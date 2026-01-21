package hr.algebra.cocktailexplorer.api

import hr.algebra.cocktailexplorer.data.CocktailResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

const val API_URL = "https://www.thecocktaildb.com/api/json/v1/1/"
interface CocktailApi {
    @GET("search.php")
    fun fetchByLetter(
        @Query("f") letter: Char
    ): Call<CocktailResponse>

    @GET("search.php")
    suspend fun fetchByLetterSuspend(
        @Query("f") letter: Char
    ): Response<CocktailResponse>

    @GET("search.php?f=")
    fun fetchItems(@Query(value="letter") letter : Char) : Call<List<CocktailResponse>>
}

/*

Search cocktail by name
www.thecocktaildb.com/api/json/v1/1/search.php?s=margarita

List all cocktails by first letter
www.thecocktaildb.com/api/json/v1/1/search.php?f=a

Search ingredient by name
www.thecocktaildb.com/api/json/v1/1/search.php?i=vodka

Lookup full cocktail details by id
www.thecocktaildb.com/api/json/v1/1/lookup.php?i=11007

Lookup ingredient by ID
www.thecocktaildb.com/api/json/v1/1/lookup.php?iid=552

Lookup a random cocktail
www.thecocktaildb.com/api/json/v1/1/random.php

Lookup a selection of 10 random cocktails *Premium API only
www.thecocktaildb.com/api/json/v1/1/randomselection.php

Search by ingredient
www.thecocktaildb.com/api/json/v1/1/filter.php?i=Gin
www.thecocktaildb.com/api/json/v1/1/filter.php?i=Vodka

Filter by alcoholic
www.thecocktaildb.com/api/json/v1/1/filter.php?a=Alcoholic
www.thecocktaildb.com/api/json/v1/1/filter.php?a=Non_Alcoholic

Filter by Category
www.thecocktaildb.com/api/json/v1/1/filter.php?c=Ordinary_Drink
www.thecocktaildb.com/api/json/v1/1/filter.php?c=Cocktail

Filter by Glass
www.thecocktaildb.com/api/json/v1/1/filter.php?g=Cocktail_glass
www.thecocktaildb.com/api/json/v1/1/filter.php?g=Champagne_flute

List the categories, glasses, ingredients or alcoholic filters
www.thecocktaildb.com/api/json/v1/1/list.php?c=list
www.thecocktaildb.com/api/json/v1/1/list.php?g=list
www.thecocktaildb.com/api/json/v1/1/list.php?i=list
www.thecocktaildb.com/api/json/v1/1/list.php?a=list

 Images
Drink thumbnails
Add /small to the end of the cocktail image URL
/images/media/drink/vrwquq1478252802.jpg/small (200x200 px)
/images/media/drink/vrwquq1478252802.jpg/medium (350x350 px)
/images/media/drink/vrwquq1478252802.jpg/large (500x500 px)

Ingredient Thumbnails
www.thecocktaildb.com/images/ingredients/gin-small.png
(100x100 px)
www.thecocktaildb.com/images/ingredients/gin-medium.png
(350x350 px)
www.thecocktaildb.com/images/ingredients/gin.png
(700x700 px)
* */