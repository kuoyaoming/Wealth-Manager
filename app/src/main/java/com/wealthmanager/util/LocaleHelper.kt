package com.wealthmanager.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import java.util.Locale

object LocaleHelper {

    fun applyLocaleToContext(context: Context, languageCode: String) {
        try {
            val locale = resolveLocale(languageCode)
            Locale.setDefault(locale)
            
            val configuration = context.resources.configuration
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                configuration.setLocales(android.os.LocaleList(locale))
            } else {
                configuration.setLocale(locale)
            }
            
            // Update the context with the new configuration
            context.resources.updateConfiguration(configuration, context.resources.displayMetrics)
        } catch (e: Exception) {
            // Ignore locale application errors
        }
    }

    fun wrapContext(base: Context, languageCode: String?): ContextWrapper {
        return try {
            val locale = resolveLocale(languageCode)
            Locale.setDefault(locale)

            val configuration = base.resources.configuration
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                configuration.setLocales(android.os.LocaleList(locale))
                val context = base.createConfigurationContext(configuration)
                ContextWrapper(context)
            } else {
                configuration.setLocale(locale)
                val context = base.createConfigurationContext(configuration)
                ContextWrapper(context)
            }
        } catch (e: Exception) {
            // If there's any issue with locale configuration, return a simple wrapper
            ContextWrapper(base)
        }
    }

    private fun resolveLocale(languageCode: String?): Locale {
        if (languageCode.isNullOrBlank()) {
            return Locale.getDefault()
        }
        return when (languageCode.lowercase(Locale.getDefault())) {
            "en" -> Locale.ENGLISH
            "zh-tw", "zh_tw" -> Locale.TRADITIONAL_CHINESE
            else -> Locale(languageCode)
        }
    }
}
