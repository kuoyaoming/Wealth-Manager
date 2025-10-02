package com.wealthmanager.security

import android.content.Context
import android.util.Base64
import com.wealthmanager.debug.DebugLogManager
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Developer API key manager for secure test API keys.
 */
@Singleton
class DeveloperKeyManager @Inject constructor(
    private val context: Context,
    private val debugLogManager: DebugLogManager
) {

    companion object {
        private const val ENCRYPTED_FINNHUB_KEY = "encrypted_finnhub_key_here"
        private const val ENCRYPTED_EXCHANGE_KEY = "encrypted_exchange_key_here"

        private const val DECRYPTION_KEY = "dev_key_2024_secure"
    }

    /**
     * Checks if in developer mode.
     */
    fun isDeveloperMode(): Boolean {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            val applicationInfo = context.applicationInfo
            (applicationInfo.flags and android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE) != 0
        } catch (e: Exception) {
            debugLogManager.logError("DEV_KEY", "Failed to check developer mode: ${e.message}")
            false
        }
    }

    /**
     * Gets developer Finnhub API key.
     */
    fun getDeveloperFinnhubKey(): String? {
        if (!isDeveloperMode()) {
            debugLogManager.log("DEV_KEY", "Not in developer mode, skipping developer key")
            return null
        }

        return try {
            val decryptedKey = decryptKey(ENCRYPTED_FINNHUB_KEY)
            debugLogManager.log("DEV_KEY", "Developer Finnhub key retrieved")
            decryptedKey
        } catch (e: Exception) {
            debugLogManager.logError("DEV_KEY", "Failed to get developer Finnhub key: ${e.message}")
            null
        }
    }

    /**
     * Gets developer Exchange Rate API key.
     */
    fun getDeveloperExchangeKey(): String? {
        if (!isDeveloperMode()) {
            debugLogManager.log("DEV_KEY", "Not in developer mode, skipping developer key")
            return null
        }

        return try {
            val decryptedKey = decryptKey(ENCRYPTED_EXCHANGE_KEY)
            debugLogManager.log("DEV_KEY", "Developer Exchange Rate key retrieved")
            decryptedKey
        } catch (e: Exception) {
            debugLogManager.logError("DEV_KEY", "Failed to get developer Exchange Rate key: ${e.message}")
            null
        }
    }

    /**
     * Decrypts API key.
     */
    private fun decryptKey(encryptedKey: String): String {
        return try {
            val keySpec = SecretKeySpec(DECRYPTION_KEY.toByteArray(), "AES")
            val cipher = Cipher.getInstance("AES")
            cipher.init(Cipher.DECRYPT_MODE, keySpec)

            val encryptedBytes = Base64.decode(encryptedKey, Base64.DEFAULT)
            val decryptedBytes = cipher.doFinal(encryptedBytes)
            String(decryptedBytes)
        } catch (e: Exception) {
            debugLogManager.logError("DEV_KEY", "Decryption failed: ${e.message}")
            throw e
        }
    }

    /**
     * Checks if developer keys are available.
     */
    fun areDeveloperKeysAvailable(): Boolean {
        if (!isDeveloperMode()) return false

        return try {
            val finnhubKey = getDeveloperFinnhubKey()
            val exchangeKey = getDeveloperExchangeKey()
            finnhubKey != null && exchangeKey != null
        } catch (e: Exception) {
            debugLogManager.logError("DEV_KEY", "Developer keys not available: ${e.message}")
            false
        }
    }

    /**
     * Gets developer key status.
     */
    fun getDeveloperKeyStatus(): DeveloperKeyStatus {
        return if (!isDeveloperMode()) {
            DeveloperKeyStatus.NOT_DEVELOPER_MODE
        } else if (!areDeveloperKeysAvailable()) {
            DeveloperKeyStatus.KEYS_NOT_AVAILABLE
        } else {
            DeveloperKeyStatus.AVAILABLE
        }
    }
}

/**
 * Developer key status.
 */
enum class DeveloperKeyStatus {
    NOT_DEVELOPER_MODE,
    KEYS_NOT_AVAILABLE,
    AVAILABLE
}
