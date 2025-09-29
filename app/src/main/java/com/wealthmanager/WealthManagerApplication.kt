package com.wealthmanager

import android.app.Application
import android.content.Context
import com.wealthmanager.auth.AuthStateManager
import com.wealthmanager.auth.AuthStateManagerEntryPoint
import com.wealthmanager.preferences.LocalePreferencesManager
import com.wealthmanager.util.LocaleHelper
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class WealthManagerApplication : Application(), AuthStateManagerEntryPoint {

    @Inject
    lateinit var authStateManager: AuthStateManager

    @Inject
    lateinit var localePreferencesManager: LocalePreferencesManager

    override fun attachBaseContext(base: Context?) {
        // Skip locale configuration during app initialization to avoid crashes
        // Locale will be applied later when the app is fully initialized
        super.attachBaseContext(base)
    }

    override fun onCreate() {
        super.onCreate()
        // Apply locale settings after the app is fully initialized
        try {
            val languageCode = localePreferencesManager.getLanguageCode()
            if (languageCode.isNotEmpty()) {
                LocaleHelper.applyLocaleToContext(this, languageCode)
            }
        } catch (e: Exception) {
            // Ignore locale application errors during startup
        }
    }

    override fun authStateManager(): AuthStateManager = authStateManager
}