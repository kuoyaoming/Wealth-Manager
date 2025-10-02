package com.wealthmanager.security

import android.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

/**
 * API key encryption tool for generating encrypted developer API keys.
 */
object KeyEncryptionTool {

    private const val DECRYPTION_KEY = "dev_key_2024_secure"

    /**
     * Encrypts API key.
     */
    fun encryptKey(apiKey: String): String {
        return try {
            val keySpec = SecretKeySpec(DECRYPTION_KEY.toByteArray(), "AES")
            val cipher = Cipher.getInstance("AES")
            cipher.init(Cipher.ENCRYPT_MODE, keySpec)

            val encryptedBytes = cipher.doFinal(apiKey.toByteArray())
            Base64.encodeToString(encryptedBytes, Base64.DEFAULT)
        } catch (e: Exception) {
            throw RuntimeException("Failed to encrypt key: ${e.message}", e)
        }
    }

    /**
     * Decrypts API key.
     */
    fun decryptKey(encryptedKey: String): String {
        return try {
            val keySpec = SecretKeySpec(DECRYPTION_KEY.toByteArray(), "AES")
            val cipher = Cipher.getInstance("AES")
            cipher.init(Cipher.DECRYPT_MODE, keySpec)

            val encryptedBytes = Base64.decode(encryptedKey, Base64.DEFAULT)
            val decryptedBytes = cipher.doFinal(encryptedBytes)
            String(decryptedBytes)
        } catch (e: Exception) {
            throw RuntimeException("Failed to decrypt key: ${e.message}", e)
        }
    }
}

/**
 * Usage example:
 *
 * fun main() {
 *     val tool = KeyEncryptionTool()
 *
 *     // Encrypt your API keys
 *     val finnhubKey = "your_finnhub_key_here"
 *     val exchangeKey = "your_exchange_key_here"
 *
 *     val encryptedFinnhub = tool.encryptKey(finnhubKey)
 *     val encryptedExchange = tool.encryptKey(exchangeKey)
 *
 *     println("Encrypted Finnhub Key: $encryptedFinnhub")
 *     println("Encrypted Exchange Key: $encryptedExchange")
 * }
 */
