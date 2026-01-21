package hr.algebra.cocktailexplorer.data.remote.api

import hr.algebra.cocktailexplorer.data.CocktailResponse
import hr.algebra.cocktailexplorer.data.FilterListResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit


/**
 TheCocktailDB API Service
  Base URL: https://www.thecocktaildb.com/api/json/v1/1/
 */
interface CocktailApiService {

    @GET("search.php")
    suspend fun searchCocktailsByName(
        @Query("s") name: String
    ): Response<CocktailResponse>

    /* /search.php?f=a      */
    @GET("search.php")
    suspend fun getCocktailsByFirstLetter(
        @Query("f") letter: String
    ): Response<CocktailResponse>

    /* /lookup.php?i=11007      */
    @GET("lookup.php")
    suspend fun getCocktailById(
        @Query("i") id: Int
    ): Response<CocktailResponse>

    /* /filter.php?i=Gin
     */
    @GET("filter.php")
    suspend fun filterByIngredient(
        @Query("i") ingredient: String
    ): Response<CocktailResponse>

    /* /filter.php?a=Alcoholic
     */
    @GET("filter.php")
    suspend fun filterByAlcoholic(
        @Query("a") alcoholic: String
    ): Response<CocktailResponse>

    /* /filter.php?c=Ordinary_Drink
     */
    @GET("filter.php")
    suspend fun filterByCategory(
        @Query("c") category: String
    ): Response<CocktailResponse>

    /**
      /filter.php?g=Cocktail_glass
     */
    @GET("filter.php")
    suspend fun filterByGlass(
        @Query("g") glass: String
    ): Response<CocktailResponse>

    /**
      /list.php?c=list
     */
    @GET("list.php")
    suspend fun getAllCategories(
        @Query("c") type: String = "list"
    ): Response<FilterListResponse>

    /**
     * /list.php?g=list
     */
    @GET("list.php")
    suspend fun getAllGlasses(
        @Query("g") type: String = "list"
    ): Response<FilterListResponse>

    /**
     * /list.php?i=list
     */
    @GET("list.php")
    suspend fun getAllIngredients(
        @Query("i") type: String = "list"
    ): Response<FilterListResponse>

    /**
     *  /list.php?a=list
     */
    @GET("list.php")
    suspend fun getAllAlcoholicFilters(
        @Query("a") type: String = "list"
    ): Response<FilterListResponse>
}

object RetrofitClient {

    private const val BASE_URL = "https://www.thecocktaildb.com/api/json/v1/1/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()


    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService: CocktailApiService = retrofit.create(CocktailApiService::class.java)
}

sealed class NetworkResult<T> {
    data class Success<T>(val data: T) : NetworkResult<T>()
    data class Error<T>(val message: String, val code: Int? = null) : NetworkResult<T>()
    data class Exception<T>(val exception: Throwable) : NetworkResult<T>()
}

suspend fun <T> safeApiCall(apiCall: suspend () -> Response<T>): NetworkResult<T> {
    return try {
        val response = apiCall()
        if (response.isSuccessful) {
            response.body()?.let {
                NetworkResult.Success(it)
            } ?: NetworkResult.Error("Empty response body", response.code())
        } else {
            NetworkResult.Error(
                message = response.message() ?: "Unknown error",
                code = response.code()
            )
        }
    } catch (e: Exception) {
        NetworkResult.Exception(e)
    }
}

class CocktailApiClient(private val apiService: CocktailApiService) {

    suspend fun searchCocktailsByName(name: String): NetworkResult<CocktailResponse> {
        return safeApiCall { apiService.searchCocktailsByName(name) }
    }

    suspend fun getCocktailsByFirstLetter(letter: String): NetworkResult<CocktailResponse> {
        return safeApiCall { apiService.getCocktailsByFirstLetter(letter) }
    }

    suspend fun getCocktailById(id: Int): NetworkResult<CocktailResponse> {
        return safeApiCall { apiService.getCocktailById(id) }
    }

    suspend fun filterByIngredient(ingredient: String): NetworkResult<CocktailResponse> {
        return safeApiCall { apiService.filterByIngredient(ingredient) }
    }

    suspend fun filterByAlcoholic(alcoholic: String): NetworkResult<CocktailResponse> {
        return safeApiCall { apiService.filterByAlcoholic(alcoholic) }
    }

    suspend fun filterByCategory(category: String): NetworkResult<CocktailResponse> {
        return safeApiCall { apiService.filterByCategory(category) }
    }

    suspend fun filterByGlass(glass: String): NetworkResult<CocktailResponse> {
        return safeApiCall { apiService.filterByGlass(glass) }
    }

    suspend fun getAllCategories(): NetworkResult<FilterListResponse> {
        return safeApiCall { apiService.getAllCategories() }
    }

    suspend fun getAllGlasses(): NetworkResult<FilterListResponse> {
        return safeApiCall { apiService.getAllGlasses() }
    }

    suspend fun getAllIngredients(): NetworkResult<FilterListResponse> {
        return safeApiCall { apiService.getAllIngredients() }
    }

    suspend fun getAllAlcoholicFilters(): NetworkResult<FilterListResponse> {
        return safeApiCall { apiService.getAllAlcoholicFilters() }
    }
}


/*

class CocktailViewModel : ViewModel() {

    private val apiClient = CocktailApiClient(RetrofitClient.apiService)

    fun searchCocktails(query: String) {
        viewModelScope.launch {
            when (val result = apiClient.searchCocktailsByName(query)) {
                is NetworkResult.Success -> {

                    val cocktails = result.data.drinks
                    _cocktails.value = UiState.Success(cocktails)
                }
                is NetworkResult.Error -> {

                    _cocktails.value = UiState.Error(result.message)
                }
                is NetworkResult.Exception -> {

                    _cocktails.value = UiState.Error(result.exception.message ?: "Unknown error")
                }
            }
        }
    }
}
*/