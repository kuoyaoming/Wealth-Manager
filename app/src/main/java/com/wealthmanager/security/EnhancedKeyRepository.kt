package com.wealthmanager.security

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.wealthmanager.debug.DebugLogManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Enhanced KeyRepository that integrates Google Password Manager with existing local storage.
 * Provides smart key retrieval with fallback mechanisms.
 */
@Singleton
class EnhancedKeyRepository
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
        private val androidKeystoreManager: AndroidKeystoreManager,
        private val keyValidator: KeyValidator,
        private val credentialManagerService: CredentialManagerService,
        private val debugLogManager: DebugLogManager,
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

        /**
         * Smart key retrieval with fallback to Google Password Manager.
         */
        suspend fun getSmartFinnhubKey(): String? {
            return try {
                // 1. Try local storage first
                val localKey = getLocalFinnhubKey()
                if (!localKey.isNullOrBlank()) {
                    debugLogManager.log("ENHANCED_KEY_REPO", "Retrieved Finnhub key from local storage")
                    return localKey
                }

                // 2. Fallback to Google Password Manager
                val googleResult = credentialManagerService.getApiKeyFromGooglePasswordManager("finnhub")
                if (googleResult.isSuccess) {
                    val googleKey = googleResult.getOrNull()
                    if (!googleKey.isNullOrBlank()) {
                        debugLogManager.log("ENHANCED_KEY_REPO", "Retrieved Finnhub key from Google Password Manager")
                        return googleKey
                    }
                }

                debugLogManager.log(
                    "ENHANCED_KEY_REPO",
                    "No Finnhub key found in local storage or Google Password Manager",
                )
                null
            } catch (e: Exception) {
                debugLogManager.logError("ENHANCED_KEY_REPO", "Failed to retrieve Finnhub key: ${e.message}")
                null
            }
        }

        /**
         * Smart key retrieval with fallback to Google Password Manager.
         */
        suspend fun getSmartExchangeKey(): String? {
            return try {
                // 1. Try local storage first
                val localKey = getLocalExchangeKey()
                if (!localKey.isNullOrBlank()) {
                    debugLogManager.log("ENHANCED_KEY_REPO", "Retrieved Exchange key from local storage")
                    return localKey
                }

                // 2. Fallback to Google Password Manager
                val googleResult = credentialManagerService.getApiKeyFromGooglePasswordManager("exchange")
                if (googleResult.isSuccess) {
                    val googleKey = googleResult.getOrNull()
                    if (!googleKey.isNullOrBlank()) {
                        debugLogManager.log("ENHANCED_KEY_REPO", "Retrieved Exchange key from Google Password Manager")
                        return googleKey
                    }
                }

                debugLogManager.log(
                    "ENHANCED_KEY_REPO",
                    "No Exchange key found in local storage or Google Password Manager",
                )
                null
            } catch (e: Exception) {
                debugLogManager.logError("ENHANCED_KEY_REPO", "Failed to retrieve Exchange key: ${e.message}")
                null
            }
        }

        /**
         * Enhanced key setting with optional Google Password Manager sync.
         */
        suspend fun setSmartFinnhubKey(
            value: String,
            syncToGoogle: Boolean = false,
        ): KeyValidationResult {
            val trimmedValue = value.trim()
            val validation = keyValidator.validateApiKey(trimmedValue, "finnhub")

            if (validation.isValid) {
                try {
                    // 1. Save to local storage
                    val encryptedKey = androidKeystoreManager.encryptData(trimmedValue)
                    if (encryptedKey != null) {
                        prefs.edit().putString(KEY_FINNHUB, encryptedKey).apply()
                        debugLogManager.log("ENHANCED_KEY_REPO", "Saved Finnhub key to local storage")
                    }

                    // 2. Optionally sync to Google Password Manager
                    if (syncToGoogle) {
                        val googleResult =
                            credentialManagerService.saveApiKeyToGooglePasswordManager(
                                "finnhub",
                                trimmedValue,
                            )
                        if (googleResult.isSuccess) {
                            debugLogManager.log("ENHANCED_KEY_REPO", "Synced Finnhub key to Google Password Manager")
                        } else {
                            debugLogManager.logError(
                                "ENHANCED_KEY_REPO",
                                "Failed to sync Finnhub key to Google Password Manager",
                            )
                        }
                    }
                } catch (e: Exception) {
                    debugLogManager.logError("ENHANCED_KEY_REPO", "Failed to save Finnhub key: ${e.message}")
                }
            }

            return validation
        }

        /**
         * Enhanced key setting with optional Google Password Manager sync.
         */
        suspend fun setSmartExchangeKey(
            value: String,
            syncToGoogle: Boolean = false,
        ): KeyValidationResult {
            val trimmedValue = value.trim()
            val validation = keyValidator.validateApiKey(trimmedValue, "exchange")

            if (validation.isValid) {
                try {
                    // 1. Save to local storage
                    val encryptedKey = androidKeystoreManager.encryptData(trimmedValue)
                    if (encryptedKey != null) {
                        prefs.edit().putString(KEY_EXCHANGE, encryptedKey).apply()
                        debugLogManager.log("ENHANCED_KEY_REPO", "Saved Exchange key to local storage")
                    }

                    // 2. Optionally sync to Google Password Manager
                    if (syncToGoogle) {
                        val googleResult =
                            credentialManagerService.saveApiKeyToGooglePasswordManager(
                                "exchange",
                                trimmedValue,
                            )
                        if (googleResult.isSuccess) {
                            debugLogManager.log("ENHANCED_KEY_REPO", "Synced Exchange key to Google Password Manager")
                        } else {
                            debugLogManager.logError(
                                "ENHANCED_KEY_REPO",
                                "Failed to sync Exchange key to Google Password Manager",
                            )
                        }
                    }
                } catch (e: Exception) {
                    debugLogManager.logError("ENHANCED_KEY_REPO", "Failed to save Exchange key: ${e.message}")
                }
            }

            return validation
        }

        /**
         * Gets key from local storage only (original behavior).
         */
        fun getLocalFinnhubKey(): String? {
            val encryptedKey = prefs.getString(KEY_FINNHUB, null)?.takeIf { it.isNotBlank() }
            return encryptedKey?.let { androidKeystoreManager.decryptData(it) }
        }

        /**
         * Gets key from local storage only (original behavior).
         */
        fun getLocalExchangeKey(): String? {
            val encryptedKey = prefs.getString(KEY_EXCHANGE, null)?.takeIf { it.isNotBlank() }
            return encryptedKey?.let { androidKeystoreManager.decryptData(it) }
        }

        /**
         * Sets key to local storage only (original behavior).
         */
        fun setLocalFinnhubKey(value: String): KeyValidationResult {
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

        /**
         * Sets key to local storage only (original behavior).
         */
        fun setLocalExchangeKey(value: String): KeyValidationResult {
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

        /**
         * Clears key from local storage.
         */
        fun clearLocalFinnhubKey() {
            prefs.edit().remove(KEY_FINNHUB).apply()
            debugLogManager.log("ENHANCED_KEY_REPO", "Cleared Finnhub key from local storage")
        }

        /**
         * Clears key from local storage.
         */
        fun clearLocalExchangeKey() {
            prefs.edit().remove(KEY_EXCHANGE).apply()
            debugLogManager.log("ENHANCED_KEY_REPO", "Cleared Exchange key from local storage")
        }

        /**
         * Clears key from both local storage and Google Password Manager.
         */
        suspend fun clearSmartFinnhubKey() {
            clearLocalFinnhubKey()
            // Note: Google Password Manager doesn't provide a direct way to delete credentials
            // The user would need to manually delete them from their Google account
            debugLogManager.log(
                "ENHANCED_KEY_REPO",
                "Cleared Finnhub key from local storage (Google Password Manager requires manual deletion)",
            )
        }

        /**
         * Clears key from both local storage and Google Password Manager.
         */
        suspend fun clearSmartExchangeKey() {
            clearLocalExchangeKey()
            // Note: Google Password Manager doesn't provide a direct way to delete credentials
            // The user would need to manually delete them from their Google account
            debugLogManager.log(
                "ENHANCED_KEY_REPO",
                "Cleared Exchange key from local storage (Google Password Manager requires manual deletion)",
            )
        }

        /**
         * Checks if key exists in local storage.
         */
        fun hasLocalFinnhubKey(): Boolean {
            return !getLocalFinnhubKey().isNullOrBlank()
        }

        /**
         * Checks if key exists in local storage.
         */
        fun hasLocalExchangeKey(): Boolean {
            return !getLocalExchangeKey().isNullOrBlank()
        }

        /**
         * Checks if key exists in Google Password Manager.
         */
        suspend fun hasGoogleFinnhubKey(): Boolean {
            return try {
                credentialManagerService.isKeyTypeStored("finnhub")
            } catch (e: Exception) {
                debugLogManager.logError("ENHANCED_KEY_REPO", "Failed to check Google Finnhub key: ${e.message}")
                false
            }
        }

        /**
         * Checks if key exists in Google Password Manager.
         */
        suspend fun hasGoogleExchangeKey(): Boolean {
            return try {
                credentialManagerService.isKeyTypeStored("exchange")
            } catch (e: Exception) {
                debugLogManager.logError("ENHANCED_KEY_REPO", "Failed to check Google Exchange key: ${e.message}")
                false
            }
        }

        /**
         * Gets key source information.
         */
        suspend fun getKeySourceInfo(): Map<String, String> {
            val info = mutableMapOf<String, String>()

            info["finnhub_local"] = if (hasLocalFinnhubKey()) "available" else "not_available"
            info["finnhub_google"] = if (hasGoogleFinnhubKey()) "available" else "not_available"
            info["exchange_local"] = if (hasLocalExchangeKey()) "available" else "not_available"
            info["exchange_google"] = if (hasGoogleExchangeKey()) "available" else "not_available"

            return info
        }

        /**
         * Preview key with masking (original behavior).
         */
        fun preview(
            key: String?,
            take: Int = 6,
        ): String {
            if (key.isNullOrBlank()) return ""
            val shown = key.take(take)
            return "$shown..."
        }

        /**
         * Checks if authentication is required (original behavior).
         */
        fun isAuthenticationRequired(): Boolean {
            return androidKeystoreManager.isAuthenticationRequired()
        }

        /**
         * Checks if keystore is available (original behavior).
         */
        fun isKeystoreAvailable(): Boolean {
            return androidKeystoreManager.isKeystoreAvailable()
        }

        /**
         * Validates key strength (original behavior).
         */
        fun validateKeyStrength(
            key: String,
            keyType: String,
        ): KeyValidationResult {
            return keyValidator.validateApiKey(key.trim(), keyType)
        }

        /**
         * Generates key strength suggestions (original behavior).
         */
        fun generateKeySuggestions(validationResult: KeyValidationResult): List<String> {
            return keyValidator.generateKeyStrengthSuggestions(validationResult)
        }
    }
