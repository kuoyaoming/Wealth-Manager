package com.wealthmanager.security

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.security.keystore.UserNotAuthenticatedException
import android.security.keystore.KeyPermanentlyInvalidatedException
import android.util.Base64
import com.wealthmanager.debug.DebugLogManager
import java.security.KeyStore
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AndroidKeystoreManager @Inject constructor(
    private val context: Context,
    private val debugLogManager: DebugLogManager
) {

    companion object {
        private const val KEYSTORE_ALIAS = "WealthManager_MasterKey"
        private const val KEY_SIZE = 256
        private const val GCM_IV_LENGTH = 12
        private const val GCM_TAG_LENGTH = 128
        private const val AUTHENTICATION_VALIDITY_DURATION = 300 // 5 minutes
    }

    private val keyStore: KeyStore by lazy {
        val keystore = KeyStore.getInstance("AndroidKeyStore")
        keystore.load(null)
        keystore
    }

    /**
     * 生成或取得主金鑰
     */
    fun getOrCreateMasterKey(): SecretKey? {
        return try {
            val existingKey = keyStore.getKey(KEYSTORE_ALIAS, null) as? SecretKey
            if (existingKey != null) {
                debugLogManager.log("KEYSTORE", "Using existing master key")
                existingKey
            } else {
                debugLogManager.log("KEYSTORE", "Generating new master key")
                generateMasterKey()
            }
        } catch (e: Exception) {
            debugLogManager.logError("KEYSTORE", "Failed to get master key: ${e.message}")
            null
        }
    }

    /**
     * 生成新的主金鑰
     */
    private fun generateMasterKey(): SecretKey? {
        return try {
            val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")

            val keyGenParameterSpec = KeyGenParameterSpec.Builder(
                KEYSTORE_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(KEY_SIZE)
                .setUserAuthenticationRequired(true)
                .setUserAuthenticationValidityDurationSeconds(AUTHENTICATION_VALIDITY_DURATION)
                .setRandomizedEncryptionRequired(true)
                .build()

            keyGenerator.init(keyGenParameterSpec)
            val secretKey = keyGenerator.generateKey()
            debugLogManager.log("KEYSTORE", "Master key generated successfully")
            secretKey
        } catch (e: Exception) {
            debugLogManager.logError("KEYSTORE", "Failed to generate master key: ${e.message}")
            null
        }
    }

    /**
     * 使用主金鑰加密資料
     */
    fun encryptData(data: String): String? {
        return try {
            val masterKey = getOrCreateMasterKey() ?: return null

            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            cipher.init(Cipher.ENCRYPT_MODE, masterKey)

            val iv = cipher.iv
            val encryptedData = cipher.doFinal(data.toByteArray())

            val combined = iv + encryptedData
            val encoded = Base64.encodeToString(combined, Base64.DEFAULT)

            debugLogManager.log("KEYSTORE", "Data encrypted successfully")
            encoded
        } catch (e: UserNotAuthenticatedException) {
            debugLogManager.logError("KEYSTORE", "User authentication required for encryption")
            null
        } catch (e: KeyPermanentlyInvalidatedException) {
            debugLogManager.logError("KEYSTORE", "Key permanently invalidated, need to regenerate")
            clearMasterKey()
            encryptData(data)
        } catch (e: Exception) {
            debugLogManager.logError("KEYSTORE", "Encryption failed: ${e.message}")
            null
        }
    }

    /**
     * 使用主金鑰解密資料
     */
    fun decryptData(encryptedData: String): String? {
        return try {
            val masterKey = getOrCreateMasterKey() ?: return null

            val combined = Base64.decode(encryptedData, Base64.DEFAULT)

            val cipher = Cipher.getInstance("AES/GCM/NoPadding")

            val iv = combined.sliceArray(0 until GCM_IV_LENGTH)
            val cipherText = combined.sliceArray(GCM_IV_LENGTH until combined.size)

            cipher.init(Cipher.DECRYPT_MODE, masterKey, GCMParameterSpec(GCM_TAG_LENGTH, iv))
            val decryptedData = cipher.doFinal(cipherText)

            debugLogManager.log("KEYSTORE", "Data decrypted successfully")
            String(decryptedData)
        } catch (e: UserNotAuthenticatedException) {
            debugLogManager.logError("KEYSTORE", "User authentication required for decryption")
            null
        } catch (e: KeyPermanentlyInvalidatedException) {
            debugLogManager.logError("KEYSTORE", "Key permanently invalidated, need to regenerate")
            null
        } catch (e: Exception) {
            debugLogManager.logError("KEYSTORE", "Decryption failed: ${e.message}")
            null
        }
    }

    /**
     * 檢查是否需要生物識別驗證
     */
    fun isAuthenticationRequired(): Boolean {
        return try {
            val masterKey = getOrCreateMasterKey() ?: return true

            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            cipher.init(Cipher.ENCRYPT_MODE, masterKey)
            false
        } catch (e: UserNotAuthenticatedException) {
            true
        } catch (e: Exception) {
            debugLogManager.logError("KEYSTORE", "Authentication check failed: ${e.message}")
            true
        }
    }

    /**
     * 清除主金鑰
     */
    fun clearMasterKey() {
        try {
            keyStore.deleteEntry(KEYSTORE_ALIAS)
            debugLogManager.log("KEYSTORE", "Master key cleared")
        } catch (e: Exception) {
            debugLogManager.logError("KEYSTORE", "Failed to clear master key: ${e.message}")
        }
    }

    /**
     * 檢查Keystore是否可用
     */
    fun isKeystoreAvailable(): Boolean {
        return try {
            val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
            true
        } catch (e: Exception) {
            debugLogManager.logError("KEYSTORE", "Keystore not available: ${e.message}")
            false
        }
    }

    /**
     * 生成隨機IV
     */
    private fun generateRandomIV(): ByteArray {
        val iv = ByteArray(GCM_IV_LENGTH)
        SecureRandom().nextBytes(iv)
        return iv
    }
}

