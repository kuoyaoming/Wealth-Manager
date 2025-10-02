package com.wealthmanager.ui.security

import android.content.Context
import androidx.fragment.app.FragmentActivity
import com.wealthmanager.R
import com.wealthmanager.debug.DebugLogManager
import com.wealthmanager.security.BiometricProtectionManager
import com.wealthmanager.security.BiometricStatus
import com.wealthmanager.security.KeyRepository
import com.wealthmanager.security.KeyValidationResult
import com.wealthmanager.security.SecurityFeature
import com.wealthmanager.security.SecurityLevelManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SecureApiKeyManager
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
        private val keyRepository: KeyRepository,
        private val biometricProtectionManager: BiometricProtectionManager,
        private val securityLevelManager: SecurityLevelManager,
        private val debugLogManager: DebugLogManager,
    ) {
        /**
         * Securely sets API key.
         */
        suspend fun setApiKeySecurely(
            key: String,
            keyType: String,
            activity: FragmentActivity? = null,
            onSuccess: (KeyValidationResult) -> Unit,
            onError: (String) -> Unit,
            onBiometricRequired: () -> Unit,
            onFallbackRequired: () -> Unit = {},
        ) {
            debugLogManager.log("SECURE_KEY_MANAGER", "Setting $keyType key securely")

            val permission = securityLevelManager.checkFeaturePermission(SecurityFeature.API_KEY_MANAGEMENT)
            when (permission) {
                com.wealthmanager.security.SecurityLevelManager.FeaturePermission.NO_ACCESS -> {
                    onError(context.getString(R.string.feature_permission_no_access))
                    return
                }
                com.wealthmanager.security.SecurityLevelManager.FeaturePermission.LIMITED_ACCESS -> {
                    setKeyWithValidation(key, keyType, onSuccess, onError)
                    return
                }
                com.wealthmanager.security.SecurityLevelManager.FeaturePermission.FULL_ACCESS -> {
                }
            }

            val requiresBiometric = securityLevelManager.requiresBiometricAuth(SecurityFeature.API_KEY_MANAGEMENT)

            if (requiresBiometric && biometricProtectionManager.isBiometricAvailable() == BiometricStatus.AVAILABLE) {
                if (activity != null) {
                    biometricProtectionManager.showBiometricPrompt(
                        activity = activity,
                        onSuccess = {
                            setKeyWithValidation(key, keyType, onSuccess, onError)
                        },
                        onError = { error ->
                            onError(context.getString(R.string.biometric_error_retry))
                        },
                        onCancel = {
                            onFallbackRequired()
                        },
                    )
                } else {
                    onBiometricRequired()
                }
            } else {
                setKeyWithValidation(key, keyType, onSuccess, onError)
            }
        }

        /**
         * Validates and sets key.
         */
        private fun setKeyWithValidation(
            key: String,
            keyType: String,
            onSuccess: (KeyValidationResult) -> Unit,
            onError: (String) -> Unit,
        ) {
            try {
                val validation =
                    when (keyType.lowercase()) {
                        "finnhub" -> keyRepository.setUserFinnhubKey(key)
                        "exchange" -> keyRepository.setUserExchangeKey(key)
                        else -> {
                            onError(context.getString(R.string.api_key_unsupported_type, keyType))
                            return
                        }
                    }

                if (validation.isValid) {
                    debugLogManager.log("SECURE_KEY_MANAGER", "$keyType key set successfully")
                    onSuccess(validation)
                } else {
                    debugLogManager.logError(
                        "SECURE_KEY_MANAGER",
                        "$keyType key validation failed: ${validation.issues}",
                    )
                    onError(context.getString(R.string.api_key_validation_failed, validation.issues.joinToString(", ")))
                }
            } catch (e: Exception) {
                debugLogManager.logError("SECURE_KEY_MANAGER", "Failed to set $keyType key: ${e.message}")
                onError(context.getString(R.string.api_key_setup_error, e.message))
            }
        }

        /**
         * Securely gets API key.
         */
        suspend fun getApiKeySecurely(
            keyType: String,
            activity: FragmentActivity? = null,
            onSuccess: (String?) -> Unit,
            onError: (String) -> Unit,
            onBiometricRequired: () -> Unit,
            onFallbackRequired: () -> Unit = {},
        ) {
            debugLogManager.log("SECURE_KEY_MANAGER", "Getting $keyType key securely")

            if (keyRepository.isAuthenticationRequired()) {
                if (activity != null) {
                    biometricProtectionManager.showBiometricPrompt(
                        activity = activity,
                        onSuccess = {
                            val key =
                                when (keyType.lowercase()) {
                                    "finnhub" -> keyRepository.getUserFinnhubKey()
                                    "exchange" -> keyRepository.getUserExchangeKey()
                                    else -> null
                                }
                            onSuccess(key)
                        },
                        onError = { error ->
                            onError(context.getString(R.string.biometric_error_retry))
                        },
                        onCancel = {
                            onFallbackRequired()
                        },
                    )
                } else {
                    onBiometricRequired()
                }
            } else {
                val key =
                    when (keyType.lowercase()) {
                        "finnhub" -> keyRepository.getUserFinnhubKey()
                        "exchange" -> keyRepository.getUserExchangeKey()
                        else -> null
                    }
                onSuccess(key)
            }
        }

        /**
         * Validates key strength (without saving).
         */
        fun validateKeyStrength(
            key: String,
            keyType: String,
        ): KeyValidationResult = keyRepository.validateKeyStrength(key, keyType)

        /**
         * Generates key suggestions.
         */
        fun generateKeySuggestions(validationResult: KeyValidationResult): List<String> =
            keyRepository.generateKeySuggestions(validationResult)

        /**
         * Checks security status.
         */
        fun getSecurityStatus(): SecurityStatus {
            val keystoreAvailable = keyRepository.isKeystoreAvailable()
            val biometricStatus = biometricProtectionManager.isBiometricAvailable()
            val authenticationRequired = keyRepository.isAuthenticationRequired()

            return SecurityStatus(
                keystoreAvailable = keystoreAvailable,
                biometricStatus = biometricStatus,
                authenticationRequired = authenticationRequired,
                securityLevel =
                    when {
                        keystoreAvailable && biometricStatus == BiometricStatus.AVAILABLE -> SecurityLevel.HIGH
                        keystoreAvailable -> SecurityLevel.MEDIUM
                        else -> SecurityLevel.LOW
                    },
            )
        }

        /**
         * Sets API key with fallback security (lower security level).
         */
        suspend fun setApiKeyWithFallback(
            key: String,
            keyType: String,
            onSuccess: (KeyValidationResult) -> Unit,
            onError: (String) -> Unit,
        ) {
            debugLogManager.log("SECURE_KEY_MANAGER", "Setting $keyType key with fallback security")

            try {
                setKeyWithValidation(key, keyType, onSuccess, onError)
            } finally {
            }
        }

        /**
         * Gets API key with fallback security (lower security level).
         */
        suspend fun getApiKeyWithFallback(
            keyType: String,
            onSuccess: (String?) -> Unit,
            onError: (String) -> Unit,
        ) {
            debugLogManager.log("SECURE_KEY_MANAGER", "Getting $keyType key with fallback security")

            try {
                val key =
                    when (keyType.lowercase()) {
                        "finnhub" -> keyRepository.getUserFinnhubKey()
                        "exchange" -> keyRepository.getUserExchangeKey()
                        else -> null
                    }
                onSuccess(key)
            } catch (e: Exception) {
                debugLogManager.logError("SECURE_KEY_MANAGER", "Failed to get $keyType key with fallback: ${e.message}")
                onError(context.getString(R.string.api_key_setup_error, e.message))
            }
        }

        /**
         * Handles security level fallback for API key operations.
         */
        fun handleSecurityFallback() {
            debugLogManager.log("SECURE_KEY_MANAGER", "Handling security fallback")
            securityLevelManager.setFallbackMode(true)
        }

        /**
         * Gets current security level information.
         */
        fun getSecurityLevelInfo(): String {
            val currentLevel = securityLevelManager.getCurrentSecurityLevel()
            val recommendation = securityLevelManager.getSecurityLevelRecommendation()
            return context.getString(R.string.api_key_current_security_level, currentLevel, recommendation)
        }

        /**
         * Clears all keys.
         */
        fun clearAllKeys() {
            debugLogManager.log("SECURE_KEY_MANAGER", "Clearing all API keys")
            keyRepository.clearUserFinnhubKey()
            keyRepository.clearUserExchangeKey()
        }
    }

/**
 * Security status.
 */
data class SecurityStatus(
    val keystoreAvailable: Boolean,
    val biometricStatus: BiometricStatus,
    val authenticationRequired: Boolean,
    val securityLevel: SecurityLevel,
)

/**
 * Security levels.
 */
enum class SecurityLevel {
    LOW,
    MEDIUM,
    HIGH,
}
