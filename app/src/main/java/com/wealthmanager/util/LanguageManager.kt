package com.wealthmanager.util

import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import java.util.Locale

object LanguageManager {
    fun setAppLanguage(
        context: Context,
        languageTag: String,
    ) {
        val appLocales = LocaleListCompat.forLanguageTags(languageTag)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            try {
                val localeManager = context.getSystemService(android.app.LocaleManager::class.java)
                localeManager?.applicationLocales = android.os.LocaleList.forLanguageTags(languageTag)
            } catch (_: Exception) {
                AppCompatDelegate.setApplicationLocales(appLocales)
            }
        } else {
            AppCompatDelegate.setApplicationLocales(appLocales)
        }
    }

    fun getAppLanguage(): String {
        val locales = AppCompatDelegate.getApplicationLocales()
        val tag = locales.toLanguageTags()
        return if (tag.isNullOrBlank()) "" else tag
    }

    fun getCurrentLocale(context: Context): Locale {
        val appLocales = AppCompatDelegate.getApplicationLocales()
        val tags = appLocales.toLanguageTags()
        if (!tags.isNullOrBlank()) {
            val firstTag = tags.split(",").firstOrNull()?.trim()
            if (!firstTag.isNullOrBlank()) {
                return Locale.forLanguageTag(firstTag)
            }
        }

        val resLocales = context.resources.configuration.locales
        if (!resLocales.isEmpty) {
            return resLocales[0]
        }
        return Locale.getDefault()
    }
}
