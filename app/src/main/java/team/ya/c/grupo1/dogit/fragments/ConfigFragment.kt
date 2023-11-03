package team.ya.c.grupo1.dogit.fragments

import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import team.ya.c.grupo1.dogit.R
import team.ya.c.grupo1.dogit.activities.MainActivity
import team.ya.c.grupo1.dogit.entities.ThemeProviderEntity

class ConfigFragment : PreferenceFragmentCompat() {

    private lateinit var view : View

    private val themeProvider by lazy { ThemeProviderEntity(requireContext()) }
    private val themePreference by lazy {
        findPreference<SwitchPreference>("darkMode")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        setThemePreference()
    }

    private fun setThemePreference() {
        val theme = themeProvider.getThemeFromPreferences()
        themePreference?.isChecked = theme == AppCompatDelegate.MODE_NIGHT_YES
        themePreference?.setOnPreferenceChangeListener { _, newValue ->
            val theme = themeProvider.getTheme(newValue)
            AppCompatDelegate.setDefaultNightMode(theme)
            true
        }
    }

    override fun onStart() {
        super.onStart()

        val activity = activity as MainActivity
        activity.hideBottomNavMenu()
    }

    override fun onStop() {
        super.onStop()

        val activity = activity as MainActivity
        activity.showBottomNavMenu()
    }
}