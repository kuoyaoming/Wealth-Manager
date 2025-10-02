package com.wealthmanager.security

import androidx.fragment.app.FragmentActivity
import com.wealthmanager.debug.DebugLogManager
import com.wealthmanager.ui.security.SecureApiKeyManager
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 安全增強功能使用範例
 * 展示如何使用新的安全機制
 */
@Singleton
class SecurityEnhancementExample
    @Inject
    constructor(
        private val secureApiKeyManager: SecureApiKeyManager,
        private val debugLogManager: DebugLogManager,
    ) {
        /**
         * 範例：安全地設定Finnhub API金鑰
         */
        suspend fun setFinnhubKeyExample(
            apiKey: String,
            activity: FragmentActivity,
            onSuccess: () -> Unit,
            onError: (String) -> Unit,
        ) {
            debugLogManager.log("SECURITY_EXAMPLE", "Setting Finnhub API key with enhanced security")

            secureApiKeyManager.setApiKeySecurely(
                key = apiKey,
                keyType = "finnhub",
                activity = activity,
                onSuccess = { validationResult ->
                    debugLogManager.log(
                        "SECURITY_EXAMPLE",
                        "Finnhub key set successfully with strength: ${validationResult.strength}",
                    )
                    onSuccess()
                },
                onError = { error ->
                    debugLogManager.logError("SECURITY_EXAMPLE", "Failed to set Finnhub key: $error")
                    onError(error)
                },
                onBiometricRequired = {
                    debugLogManager.log("SECURITY_EXAMPLE", "Biometric authentication required")
                    onError("需要生物識別驗證")
                },
            )
        }

        /**
         * 範例：安全地取得Exchange Rate API金鑰
         */
        suspend fun getExchangeRateKeyExample(
            activity: FragmentActivity,
            onSuccess: (String?) -> Unit,
            onError: (String) -> Unit,
        ) {
            debugLogManager.log("SECURITY_EXAMPLE", "Getting Exchange Rate API key securely")

            secureApiKeyManager.getApiKeySecurely(
                keyType = "exchange",
                activity = activity,
                onSuccess = { key ->
                    debugLogManager.log("SECURITY_EXAMPLE", "Exchange Rate key retrieved successfully")
                    onSuccess(key)
                },
                onError = { error ->
                    debugLogManager.logError("SECURITY_EXAMPLE", "Failed to get Exchange Rate key: $error")
                    onError(error)
                },
                onBiometricRequired = {
                    debugLogManager.log("SECURITY_EXAMPLE", "Biometric authentication required")
                    onError("需要生物識別驗證")
                },
            )
        }

        /**
         * 範例：驗證金鑰強度
         */
        fun validateKeyStrengthExample(
            apiKey: String,
            keyType: String,
        ) {
            debugLogManager.log("SECURITY_EXAMPLE", "Validating key strength for $keyType")

            val validationResult = secureApiKeyManager.validateKeyStrength(apiKey, keyType)

            debugLogManager.log("SECURITY_EXAMPLE", "Key validation result:")
            debugLogManager.log("SECURITY_EXAMPLE", "- Valid: ${validationResult.isValid}")
            debugLogManager.log("SECURITY_EXAMPLE", "- Strength: ${validationResult.strength}")
            debugLogManager.log("SECURITY_EXAMPLE", "- Score: ${validationResult.score}")

            if (validationResult.issues.isNotEmpty()) {
                debugLogManager.log("SECURITY_EXAMPLE", "Issues found:")
                validationResult.issues.forEach { issue ->
                    debugLogManager.log("SECURITY_EXAMPLE", "- $issue")
                }
            }

            val suggestions = secureApiKeyManager.generateKeySuggestions(validationResult)
            if (suggestions.isNotEmpty()) {
                debugLogManager.log("SECURITY_EXAMPLE", "Suggestions:")
                suggestions.forEach { suggestion ->
                    debugLogManager.log("SECURITY_EXAMPLE", "- $suggestion")
                }
            }
        }

        /**
         * 範例：檢查安全狀態
         */
        fun checkSecurityStatusExample() {
            debugLogManager.log("SECURITY_EXAMPLE", "Checking security status")

            val securityStatus = secureApiKeyManager.getSecurityStatus()

            debugLogManager.log("SECURITY_EXAMPLE", "Security Status:")
            debugLogManager.log("SECURITY_EXAMPLE", "- Keystore Available: ${securityStatus.keystoreAvailable}")
            debugLogManager.log("SECURITY_EXAMPLE", "- Biometric Status: ${securityStatus.biometricStatus}")
            debugLogManager.log(
                "SECURITY_EXAMPLE",
                "- Authentication Required: ${securityStatus.authenticationRequired}",
            )
            debugLogManager.log("SECURITY_EXAMPLE", "- Security Level: ${securityStatus.securityLevel}")
        }

        /**
         * 範例：完整的安全設定流程
         */
        suspend fun completeSecuritySetupExample(
            finnhubKey: String,
            exchangeKey: String,
            activity: FragmentActivity,
            onComplete: () -> Unit,
            onError: (String) -> Unit,
        ) {
            debugLogManager.log("SECURITY_EXAMPLE", "Starting complete security setup")

            checkSecurityStatusExample()

            validateKeyStrengthExample(finnhubKey, "finnhub")
            validateKeyStrengthExample(exchangeKey, "exchange")

            var finnhubSet = false
            var exchangeSet = false

            fun checkCompletion() {
                if (finnhubSet && exchangeSet) {
                    debugLogManager.log("SECURITY_EXAMPLE", "All keys set successfully")
                    onComplete()
                }
            }

            secureApiKeyManager.setApiKeySecurely(
                key = finnhubKey,
                keyType = "finnhub",
                activity = activity,
                onSuccess = {
                    finnhubSet = true
                    checkCompletion()
                },
                onError = { error ->
                    onError("Finnhub金鑰設定失敗: $error")
                },
                onBiometricRequired = {
                    onError("需要生物識別驗證")
                },
            )

            secureApiKeyManager.setApiKeySecurely(
                key = exchangeKey,
                keyType = "exchange",
                activity = activity,
                onSuccess = {
                    exchangeSet = true
                    checkCompletion()
                },
                onError = { error ->
                    onError("Exchange Rate金鑰設定失敗: $error")
                },
                onBiometricRequired = {
                    onError("需要生物識別驗證")
                },
            )
        }
    }
