package com.example.learningapp

import android.app.Application
import android.app.UiModeManager.MODE_NIGHT_NO
import android.app.UiModeManager.MODE_NIGHT_YES
import android.preference.PreferenceManager
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dagger.hilt.android.HiltAndroidApp
import kotlin.properties.Delegates

@HiltAndroidApp
class App : Application() {
    private val prefs by lazy { getSharedPreferences("app_preferences", MODE_PRIVATE) }
    private val _isDarkTheme = mutableStateOf(false)
    var isDarkTheme: Boolean
        get() = _isDarkTheme.value
        set(value) {
            _isDarkTheme.value = value
            prefs.edit().putBoolean("dark_theme", value).apply()
            AppCompatDelegate.setDefaultNightMode(
                if (value) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            )
        }

    override fun onCreate() {
        super.onCreate()
        isDarkTheme = prefs.getBoolean("dark_theme", false)
    }
}
