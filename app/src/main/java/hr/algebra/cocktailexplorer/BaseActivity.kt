package hr.algebra.cocktailexplorer

import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale

abstract class BaseActivity : AppCompatActivity() {


    override fun attachBaseContext(newBase: Context) {
        val prefs = newBase.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        val lang = prefs.getString("language", "en")!!

        val context = LocaleHelper.applyLocale(newBase, lang)
        super.attachBaseContext(context)
    }
}

object LocaleHelper {
    @Suppress("DEPRECATION")
    fun applyLocale(context: Context, language: String): Context {
        val locale = Locale(language)
        Locale.setDefault(locale)

        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)

        return context.createConfigurationContext(config)
    }
}
