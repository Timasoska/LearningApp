package com.example.learningapp

import android.app.Application
import android.app.UiModeManager.MODE_NIGHT_NO
import android.app.UiModeManager.MODE_NIGHT_YES
import android.preference.PreferenceManager
import androidx.appcompat.app.AppCompatDelegate
import dagger.hilt.android.HiltAndroidApp
import kotlin.properties.Delegates

@HiltAndroidApp
class App : Application() {
    private val prefs by lazy { PreferenceManager.getDefaultSharedPreferences(this) }
    var isDarkTheme: Boolean by Delegates.observable(false) { _, _, new ->
        prefs.edit().putBoolean("dark_theme", new).apply()
        AppCompatDelegate.setDefaultNightMode(
            if (new) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )
    }

    override fun onCreate() {
        super.onCreate()
        isDarkTheme = prefs.getBoolean("dark_theme", false)
    }
}