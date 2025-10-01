package com.wealthmanager.security

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class KeyRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefsName = "api_keys_prefs"

    private val masterKey: MasterKey by lazy {
        MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
    }

    private val prefs by lazy {
        EncryptedSharedPreferences.create(
            context,
            prefsName,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    private val KEY_FINNHUB = "user_finnhub_api_key"
    private val KEY_EXCHANGE = "user_exchange_rate_api_key"

    fun getUserFinnhubKey(): String? = prefs.getString(KEY_FINNHUB, null)?.takeIf { it.isNotBlank() }
    fun getUserExchangeKey(): String? = prefs.getString(KEY_EXCHANGE, null)?.takeIf { it.isNotBlank() }

    fun setUserFinnhubKey(value: String) {
        prefs.edit().putString(KEY_FINNHUB, value.trim()).apply()
    }

    fun setUserExchangeKey(value: String) {
        prefs.edit().putString(KEY_EXCHANGE, value.trim()).apply()
    }

    fun clearUserFinnhubKey() {
        prefs.edit().remove(KEY_FINNHUB).apply()
    }

    fun clearUserExchangeKey() {
        prefs.edit().remove(KEY_EXCHANGE).apply()
    }

    fun preview(key: String?, take: Int = 6): String {
        if (key.isNullOrBlank()) return ""
        val shown = key.take(take)
        return "$shown..."
    }
}


