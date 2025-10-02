package com.wealthmanager.security

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class KeyRepository
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
        private val androidKeystoreManager: AndroidKeystoreManager,
        private val keyValidator: KeyValidator,
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
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
            )
        }

        private val KEY_FINNHUB = "user_finnhub_api_key"
        private val KEY_EXCHANGE = "user_exchange_rate_api_key"

        fun getUserFinnhubKey(): String? {
            val encryptedKey = prefs.getString(KEY_FINNHUB, null)?.takeIf { it.isNotBlank() }
            return encryptedKey?.let { androidKeystoreManager.decryptData(it) }
        }

        fun getUserExchangeKey(): String? {
            val encryptedKey = prefs.getString(KEY_EXCHANGE, null)?.takeIf { it.isNotBlank() }
            return encryptedKey?.let { androidKeystoreManager.decryptData(it) }
        }

        fun setUserFinnhubKey(value: String): KeyValidationResult {
            val trimmedValue = value.trim()
            val validation = keyValidator.validateApiKey(trimmedValue, "finnhub")

            if (validation.isValid) {
                val encryptedKey = androidKeystoreManager.encryptData(trimmedValue)
                if (encryptedKey != null) {
                    prefs.edit().putString(KEY_FINNHUB, encryptedKey).apply()
                }
            }

            return validation
        }

        fun setUserExchangeKey(value: String): KeyValidationResult {
            val trimmedValue = value.trim()
            val validation = keyValidator.validateApiKey(trimmedValue, "exchange")

            if (validation.isValid) {
                val encryptedKey = androidKeystoreManager.encryptData(trimmedValue)
                if (encryptedKey != null) {
                    prefs.edit().putString(KEY_EXCHANGE, encryptedKey).apply()
                }
            }

            return validation
        }

        fun clearUserFinnhubKey() {
            prefs.edit().remove(KEY_FINNHUB).apply()
        }

        fun clearUserExchangeKey() {
            prefs.edit().remove(KEY_EXCHANGE).apply()
        }

        fun preview(
            key: String?,
            take: Int = 6,
        ): String {
            if (key.isNullOrBlank()) return ""
            val shown = key.take(take)
            return "$shown..."
        }

        /**
         * 檢查是否需要生物識別驗證
         */
        fun isAuthenticationRequired(): Boolean {
            return androidKeystoreManager.isAuthenticationRequired()
        }

        /**
         * 檢查Keystore是否可用
         */
        fun isKeystoreAvailable(): Boolean {
            return androidKeystoreManager.isKeystoreAvailable()
        }

        /**
         * 驗證金鑰強度（不儲存）
         */
        fun validateKeyStrength(
            key: String,
            keyType: String,
        ): KeyValidationResult {
            return keyValidator.validateApiKey(key.trim(), keyType)
        }

        /**
         * 生成金鑰強度建議
         */
        fun generateKeySuggestions(validationResult: KeyValidationResult): List<String> {
            return keyValidator.generateKeyStrengthSuggestions(validationResult)
        }
    }
