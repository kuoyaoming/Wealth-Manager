package com.wealthmanager.preferences

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalePreferencesManager @Inject constructor(
    @ApplicationContext private val appContext: Context
) {

    fun getLanguageCode(): String {
        val storedLanguage = getStoredLanguage(appContext)
        if (storedLanguage.isNotBlank()) {
            return storedLanguage
        }

        val defaultLanguage = resolveDefaultLanguage(Locale.getDefault())
        setStoredLanguage(appContext, defaultLanguage)
        return defaultLanguage
    }

    fun setLanguageCode(languageCode: String) {
        setStoredLanguage(appContext, languageCode)
    }

    companion object {
        private const val PREFS_NAME = "locale_preferences"
        private const val KEY_LANGUAGE_CODE = "language_code"

        fun getStoredLanguage(context: Context): String {
            return try {
                val appContext = context.applicationContext ?: context
                val prefs = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                prefs.getString(KEY_LANGUAGE_CODE, "") ?: ""
            } catch (e: Exception) {
                // Return empty string if there's any issue accessing preferences
                ""
            }
        }

        fun setStoredLanguage(context: Context, languageCode: String) {
            try {
                val appContext = context.applicationContext ?: context
                val prefs = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                prefs.edit().putString(KEY_LANGUAGE_CODE, languageCode).apply()
            } catch (e: Exception) {
                // Ignore if there's any issue saving preferences
            }
        }

        private fun resolveDefaultLanguage(locale: Locale): String {
            return if (locale.language.equals("zh", ignoreCase = true)) {
                "zh-TW"
            } else {
                "en"
            }
        }
    }
}
