package com.wealthmanager

import android.app.Application
import android.content.Context
import com.wealthmanager.auth.AuthStateManager
import com.wealthmanager.auth.AuthStateManagerEntryPoint
import com.wealthmanager.preferences.LocalePreferencesManager
import com.wealthmanager.util.LanguageManager
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

/**
 * Main application class for the Wealth Manager app.
 *
 * This class handles:
 * - Dependency injection setup with Hilt
 * - Authentication state management
 * - Locale and language preferences
 * - Application lifecycle management
 *
 * @property authStateManager Manages user authentication state
 * @property localePreferencesManager Handles language and locale preferences
 */
@HiltAndroidApp
class WealthManagerApplication : Application(), AuthStateManagerEntryPoint {
    @Inject
    lateinit var authStateManager: AuthStateManager

    @Inject
    lateinit var localePreferencesManager: LocalePreferencesManager

    companion object {
        @Volatile
        private var INSTANCE: WealthManagerApplication? = null

        fun getInstance(): WealthManagerApplication {
            return INSTANCE ?: throw IllegalStateException("Application not initialized")
        }
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
    }

    /**
     * Initializes the application and sets up language preferences.
     *
     * This method is called when the application starts and handles:
     * - Language preference restoration
     * - Error handling for locale setup
     */
    override fun onCreate() {
        super.onCreate()
        INSTANCE = this

        try {
            val languageCode = localePreferencesManager.getLanguageCode()
            if (languageCode.isNotEmpty()) {
                LanguageManager.setAppLanguage(this, languageCode)
            }
        } catch (e: Exception) {
            // Ignore locale application errors during startup
        }
    }

    override fun authStateManager(): AuthStateManager = authStateManager
}
