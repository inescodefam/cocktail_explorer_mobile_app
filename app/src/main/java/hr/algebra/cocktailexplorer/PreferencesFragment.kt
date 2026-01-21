package hr.algebra.cocktailexplorer

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.content.edit
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat

class PreferencesFragment : PreferenceFragmentCompat() {

        override fun onCreatePreferences(
            savedInstanceState: Bundle?,
            rootKey: String?
        ) {
            setPreferencesFromResource(R.xml.prefs, rootKey)

            val languagePref = findPreference<ListPreference>("app_language")

            languagePref?.setOnPreferenceChangeListener { _, newValue ->
                val languageCode = newValue as String
                saveLanguage(languageCode)
                restartApp()
                true
            }
        }

        private fun saveLanguage(languageCode: String) {
            requireContext()
                .getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
                .edit {
                    putString("language", languageCode)
                }
        }

        private fun restartApp() {
            val intent = Intent(requireContext(), HostActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }