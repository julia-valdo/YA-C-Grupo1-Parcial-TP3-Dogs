package team.ya.c.grupo1.dogit.entities

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager

class ThemeProviderEntity(private val context: Context) {

fun getThemeFromPreferences(): Int {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val themePreference = preferences.getBoolean("darkMode", false)
        return getTheme(themePreference)
    }

    fun getTheme(themePreference: Any?): Int {
        return when (themePreference) {
            false -> AppCompatDelegate.MODE_NIGHT_NO
            true -> AppCompatDelegate.MODE_NIGHT_YES
            else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }
    }
}