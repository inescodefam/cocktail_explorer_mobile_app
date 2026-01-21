package hr.algebra.cocktailexplorer

import android.annotation.SuppressLint
import android.os.Bundle
import hr.algebra.cocktailexplorer.databinding.ActivitySplashScreenBinding
import hr.algebra.cocktailexplorer.framework.applyAnimation
import hr.algebra.cocktailexplorer.framework.callDelayed
import hr.algebra.cocktailexplorer.framework.getBooleanPreference
import hr.algebra.cocktailexplorer.framework.isOnline
import android.content.Intent
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import hr.algebra.cocktailexplorer.api.CocktailWorker

private const val DELAY = 3000L

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : BaseActivity() {

    private lateinit var binding: ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        startAnimations()
        redirect()
    }

    private fun startAnimations() {
        binding.tvSplash.applyAnimation(R.anim.blink)
        binding.ivSplash.applyAnimation(R.anim.rotate)
    }

    private fun redirect() {
        if (getBooleanPreference(DATA_IMPORTED)) callDelayed(DELAY) {
            startActivity(Intent(this, HostActivity::class.java))
            finish()
        }
        else if (isOnline()) WorkManager.getInstance(this).apply {
            enqueueUniqueWork(
                DATA_IMPORTED,
                ExistingWorkPolicy.KEEP,
                OneTimeWorkRequest.from(CocktailWorker::class.java)
            )
        }
        else {
            binding.tvSplash.text = getString(R.string.no_internet)
            callDelayed(DELAY) { finish() }
        }
    }
}