package hr.algebra.cocktailexplorer.api

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class CocktailWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            CocktailFetcher(context).fetchCocktails()
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}
