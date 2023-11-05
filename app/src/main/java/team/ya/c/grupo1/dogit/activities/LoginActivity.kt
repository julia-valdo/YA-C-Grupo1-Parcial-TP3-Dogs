package team.ya.c.grupo1.dogit.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import team.ya.c.grupo1.dogit.R
import team.ya.c.grupo1.dogit.entities.ThemeProviderEntity

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val theme = ThemeProviderEntity(this).getThemeFromPreferences()
        AppCompatDelegate.setDefaultNightMode(theme)
    }
}