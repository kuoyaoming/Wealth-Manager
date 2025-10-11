package com.wealthmanager.preferences

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

private const val PREFS_NAME = "locale_preferences"
private const val KEY_LANGUAGE_CODE = "language_code"

/**
 * Manages locale-related preferences, such as the application's display language.
 *
 * This class provides a simple interface to get and set the current language code,
 * with logic to determine a sensible default based on the device's locale.
 */
@Singleton
class LocalePreferencesManager @Inject constructor(@ApplicationContext private val context: Context) {

    private val prefs: SharedPreferences by lazy {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    /**
     * The currently selected language code for the application (e.g., "en", "zh-TW").
     *
     * When read, if no language has been previously set, it resolves a default language
     * based on the device's current locale and saves it.
     */
    var languageCode: String
        get() {
            val storedLanguage = prefs.getString(KEY_LANGUAGE_CODE, null)
            return if (storedLanguage.isNullOrBlank()) {
                val defaultLanguage = resolveDefaultLanguage(Locale.getDefault())
                languageCode = defaultLanguage // Use the setter to save the default
                defaultLanguage
            } else {
                storedLanguage
            }
        }
        set(value) {
            prefs.edit().putString(KEY_LANGUAGE_CODE, value).apply()
        }

    private fun resolveDefaultLanguage(locale: Locale): String {
        return if (locale.language.equals("zh", ignoreCase = true)) {
            "zh-TW"
        } else {
            "en"
        }
    }
}
