package com.wealthmanager.ui.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wealthmanager.R
import com.wealthmanager.auth.AuthStateManager
import com.wealthmanager.backup.BackupPreferencesManager
import com.wealthmanager.backup.EnhancedBackupManager
import com.wealthmanager.data.FirstLaunchManager
import com.wealthmanager.data.service.ApiTestService
import com.wealthmanager.preferences.LocalePreferencesManager
import com.wealthmanager.security.CredentialManagerService
import com.wealthmanager.security.GpmStatus
import com.wealthmanager.security.KeyRepository
import com.wealthmanager.ui.security.SecureApiKeyManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI state data class for the settings screen.
 *
 * @property biometricEnabled Whether biometric authentication is enabled
 * @property financialBackupEnabled Whether financial data backup is enabled
 * @property currentLanguageCode Currently selected language code
 * @property availableLanguages List of available language options
 * @property apiTestResults Results from API connectivity tests
 * @property isTestingApis Whether API tests are currently running
 * @property finnhubKeyPreview Preview of the Finnhub API key
 * @property exchangeKeyPreview Preview of the exchange rate API key
 * @property lastKeyActionMessage Last message from key management operations
 * @property showBiometricFallbackDialog Whether to show biometric fallback dialog
 * @property pendingKeyAction Pending key management action
 */
data class SettingsUiState(
    val biometricEnabled: Boolean = false,
    val financialBackupEnabled: Boolean = false,
    val currentLanguageCode: String = "",
    val availableLanguages: List<LanguageOption> = emptyList(),
    val apiTestResults: List<ApiTestService.ApiTestResult> = emptyList(),
    val isTestingApis: Boolean = false,
    // User API key management UI state
    val finnhubKeyPreview: String = "",
    val exchangeKeyPreview: String = "",
    val lastKeyActionMessage: String = "",
    val lastKeyErrorMessage: String = "", // 專門用於錯誤訊息
    // Biometric fallback dialog state
    val showBiometricFallbackDialog: Boolean = false,
    val pendingKeyAction: (() -> Unit)? = null,
    // Enhanced backup state
    val backupStatus: EnhancedBackupManager.BackupStatus? = null,
    val backupRecommendations: List<String> = emptyList(),
    // Google Password Manager status
    val gpmStatus: GpmStatus = GpmStatus.Unavailable,
    // Track previous API keys to detect changes
    val previousFinnhubKey: String = "",
    val previousExchangeKey: String = "",
    val shouldPromptGpmSave: Boolean = false,
)

/**
 * Data class representing a language option for the settings screen.
 *
 * @property languageCode The language code (e.g., "en", "zh-TW")
 * @property displayNameRes Resource ID for the display name string
 */
data class LanguageOption(
    val languageCode: String,
    val displayNameRes: Int,
)

/**
 * ViewModel for the settings screen managing user preferences and configuration.
 *
 * This ViewModel handles:
 * - Biometric authentication settings
 * - Language and locale preferences
 * - API key management and testing
 * - Backup and restore settings
 * - Security configuration
 *
 * @property authStateManager Manager for authentication state
 * @property backupPreferencesManager Manager for backup preferences
 * @property localePreferencesManager Manager for locale preferences
 * @property apiTestService Service for testing API connectivity
 * @property keyRepository Repository for API key management
 * @property secureApiKeyManager Manager for secure API key operations
 * @property firstLaunchManager Manager for first launch logic
 */
@HiltViewModel
class SettingsViewModel
    @Inject
    constructor(
        private val authStateManager: AuthStateManager,
        private val backupPreferencesManager: BackupPreferencesManager,
        private val enhancedBackupManager: EnhancedBackupManager,
        private val localePreferencesManager: LocalePreferencesManager,
        private val apiTestService: ApiTestService,
        private val keyRepository: KeyRepository,
        private val secureApiKeyManager: SecureApiKeyManager,
        private val credentialManagerService: CredentialManagerService,
        val firstLaunchManager: FirstLaunchManager,
        @ApplicationContext private val context: Context,
    ) : ViewModel() {
        private val _uiState =
            MutableStateFlow(
                SettingsUiState(
                    biometricEnabled = authStateManager.isBiometricEnabled(),
                    financialBackupEnabled = backupPreferencesManager.isFinancialBackupEnabled(),
                    currentLanguageCode = localePreferencesManager.getLanguageCode(),
                    availableLanguages =
                        listOf(
                            LanguageOption(languageCode = "en", displayNameRes = R.string.language_option_english),
                            LanguageOption(
                                languageCode = "zh-TW",
                                displayNameRes = R.string.language_option_traditional_chinese,
                            ),
                        ),
                    finnhubKeyPreview = keyRepository.preview(keyRepository.getUserFinnhubKey()),
                    exchangeKeyPreview = keyRepository.preview(keyRepository.getUserExchangeKey()),
                    previousFinnhubKey = keyRepository.getUserFinnhubKey() ?: "",
                    previousExchangeKey = keyRepository.getUserExchangeKey() ?: "",
                ),
            )
        val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

        init {
            // Initialize with default unavailable status to avoid triggering credential dialogs
            // GPM status will be checked only when user explicitly requests it
        }

        /**
         * Checks and updates the Google Password Manager status.
         * This method should only be called when user explicitly requests GPM info.
         */
        suspend fun checkGooglePasswordManagerStatus() {
            try {
                val gpmStatus = credentialManagerService.getGooglePasswordManagerStatus()
                _uiState.value = _uiState.value.copy(gpmStatus = gpmStatus)
            } catch (e: Exception) {
                // If check fails, assume unavailable
                _uiState.value = _uiState.value.copy(gpmStatus = GpmStatus.Unavailable)
            }
        }

        fun setBiometricEnabled(enabled: Boolean) {
            viewModelScope.launch {
                authStateManager.setBiometricEnabled(enabled)
                authStateManager.clearAuthentication()
                _uiState.value = _uiState.value.copy(biometricEnabled = enabled)
            }
        }

        fun setFinancialBackupEnabled(enabled: Boolean) {
            viewModelScope.launch {
                backupPreferencesManager.setFinancialBackupEnabled(enabled)
                _uiState.value = _uiState.value.copy(financialBackupEnabled = enabled)
                // Refresh backup status after change
                refreshBackupStatus()
            }
        }

        /**
         * Refreshes the enhanced backup status and recommendations.
         */
        fun refreshBackupStatus() {
            viewModelScope.launch {
                try {
                    val backupStatus = enhancedBackupManager.getBackupStatus()
                    val recommendations = enhancedBackupManager.getBackupRecommendations()
                    _uiState.value =
                        _uiState.value.copy(
                            backupStatus = backupStatus,
                            backupRecommendations = recommendations,
                        )
                } catch (e: Exception) {
                    // Handle error silently or log it
                }
            }
        }

        /**
         * Gets backup recommendations for the user.
         */
        fun getBackupRecommendations(): List<String> {
            return _uiState.value.backupRecommendations
        }

        /**
         * Gets the current backup status.
         */
        fun getBackupStatus(): EnhancedBackupManager.BackupStatus? {
            return _uiState.value.backupStatus
        }

        fun setLanguage(languageCode: String) {
            viewModelScope.launch {
                localePreferencesManager.setLanguageCode(languageCode)
                _uiState.value = _uiState.value.copy(currentLanguageCode = languageCode)
            }
        }

        /**
         * Test all API keys
         */
        fun testApiKeys() {
            viewModelScope.launch {
                _uiState.value = _uiState.value.copy(isTestingApis = true)
                try {
                    val results = apiTestService.testAllApis()
                    _uiState.value =
                        _uiState.value.copy(
                            apiTestResults = results,
                            isTestingApis = false,
                        )
                } catch (e: Exception) {
                    _uiState.value = _uiState.value.copy(isTestingApis = false)
                }
            }
        }

        /**
         * Securely tests all API keys (includes security status).
         */
        fun testApiKeysSecurely() {
            viewModelScope.launch {
                _uiState.value = _uiState.value.copy(isTestingApis = true)
                try {
                    val secureResult = apiTestService.testApiKeysSecurely()
                    val results = listOf(secureResult.finnhubResult, secureResult.exchangeResult)

                    _uiState.value =
                        _uiState.value.copy(
                            apiTestResults = results,
                            isTestingApis = false,
                            lastKeyActionMessage = context.getString(R.string.api_key_security_test_complete, secureResult.overallSecurity),
                        )
                } catch (e: Exception) {
                    _uiState.value =
                        _uiState.value.copy(
                            isTestingApis = false,
                            lastKeyActionMessage = context.getString(R.string.api_key_security_test_failed, e.message ?: ""),
                        )
                }
            }
        }

        fun validateAndSaveFinnhubKey(input: String) {
            viewModelScope.launch {
                val trimmed = input.trim()
                val result = apiTestService.testFinnhubApiWithKey(trimmed)
                if (result.isWorking) {
                    val validationResult = keyRepository.setUserFinnhubKey(trimmed)
                    if (validationResult.isValid) {
                        _uiState.value =
                            _uiState.value.copy(
                                finnhubKeyPreview = keyRepository.preview(trimmed),
                                lastKeyActionMessage = "Finnhub key saved with ${validationResult.strength} strength",
                            )
                    } else {
                        _uiState.value =
                            _uiState.value.copy(
                                lastKeyActionMessage = "Key validation failed: ${validationResult.issues.joinToString(", ")}",
                            )
                    }
                } else {
                    _uiState.value =
                        _uiState.value.copy(
                            lastKeyActionMessage = "Finnhub key invalid: ${result.message}",
                        )
                }
            }
        }

        fun validateAndSaveExchangeKey(input: String) {
            viewModelScope.launch {
                val trimmed = input.trim()
                val result = apiTestService.testExchangeRateApiWithKey(trimmed)
                if (result.isWorking) {
                    val validationResult = keyRepository.setUserExchangeKey(trimmed)
                    if (validationResult.isValid) {
                        _uiState.value =
                            _uiState.value.copy(
                                exchangeKeyPreview = keyRepository.preview(trimmed),
                                lastKeyActionMessage = "Exchange Rate key saved with ${validationResult.strength} strength",
                            )
                    } else {
                        _uiState.value =
                            _uiState.value.copy(
                                lastKeyActionMessage = "Key validation failed: ${validationResult.issues.joinToString(", ")}",
                            )
                    }
                } else {
                    _uiState.value =
                        _uiState.value.copy(
                            lastKeyActionMessage = "Exchange Rate key invalid: ${result.message}",
                        )
                }
            }
        }

        fun clearFinnhubKey() {
            keyRepository.clearUserFinnhubKey()
            _uiState.value =
                _uiState.value.copy(
                    finnhubKeyPreview = "",
                    lastKeyActionMessage = "Finnhub key cleared",
                )
        }

        fun clearExchangeKey() {
            keyRepository.clearUserExchangeKey()
            _uiState.value =
                _uiState.value.copy(
                    exchangeKeyPreview = "",
                    lastKeyActionMessage = "Exchange Rate key cleared",
                )
        }

        /**
         * Sets API key using secure mechanism with biometric fallback.
         */
        fun setApiKeySecurely(
            key: String,
            keyType: String,
            activity: androidx.fragment.app.FragmentActivity? = null,
        ) {
            viewModelScope.launch {
                try {
                    val validationResult = secureApiKeyManager.validateKeyStrength(key, keyType)
                    if (!validationResult.isValid) {
                        _uiState.value =
                            _uiState.value.copy(
                                lastKeyActionMessage = context.getString(R.string.api_key_validation_failed, validationResult.issues.joinToString(", ")),
                            )
                        return@launch
                    }

                    val testResult =
                        when (keyType.lowercase()) {
                            "finnhub" -> apiTestService.testFinnhubApiWithKey(key)
                            "exchange" -> apiTestService.testExchangeRateApiWithKey(key)
                            else -> {
                                _uiState.value =
                                    _uiState.value.copy(
                                        lastKeyActionMessage = context.getString(R.string.api_key_unsupported_type, keyType),
                                    )
                                return@launch
                            }
                        }

                    if (testResult.isWorking) {
                        secureApiKeyManager.setApiKeySecurely(
                            key = key,
                            keyType = keyType,
                            activity = activity,
                            onSuccess = { saveResult ->
                                // Check if this is a new key or if the key has changed
                                val isNewKey =
                                    when (keyType.lowercase()) {
                                        "finnhub" -> _uiState.value.previousFinnhubKey.isEmpty()
                                        "exchange" -> _uiState.value.previousExchangeKey.isEmpty()
                                        else -> true
                                    }

                                val isKeyChanged =
                                    when (keyType.lowercase()) {
                                        "finnhub" -> _uiState.value.previousFinnhubKey != key
                                        "exchange" -> _uiState.value.previousExchangeKey != key
                                        else -> true
                                    }

                                val shouldPromptGpm = isNewKey || isKeyChanged

                                _uiState.value =
                                    _uiState.value.copy(
                                        finnhubKeyPreview = if (keyType.lowercase() == "finnhub") keyRepository.preview(key) else _uiState.value.finnhubKeyPreview,
                                        exchangeKeyPreview = if (keyType.lowercase() == "exchange") keyRepository.preview(key) else _uiState.value.exchangeKeyPreview,
                                        lastKeyActionMessage = context.getString(R.string.api_key_saved_securely, keyType, saveResult.strength),
                                        // Clear previous error message
                                        lastKeyErrorMessage = "",
                                        // Update previous keys and GPM prompt flag
                                        previousFinnhubKey = if (keyType.lowercase() == "finnhub") key else _uiState.value.previousFinnhubKey,
                                        previousExchangeKey = if (keyType.lowercase() == "exchange") key else _uiState.value.previousExchangeKey,
                                        shouldPromptGpmSave = shouldPromptGpm,
                                    )
                            },
                            onError = { error ->
                                _uiState.value =
                                    _uiState.value.copy(
                                        lastKeyErrorMessage = context.getString(R.string.api_key_save_failed, error),
                                        // Clear success message
                                        lastKeyActionMessage = "",
                                    )
                            },
                            onBiometricRequired = {
                                _uiState.value =
                                    _uiState.value.copy(
                                        lastKeyActionMessage = context.getString(R.string.api_key_biometric_required),
                                    )
                            },
                            onFallbackRequired = {
                                _uiState.value =
                                    _uiState.value.copy(
                                        showBiometricFallbackDialog = true,
                                        pendingKeyAction = { setApiKeyWithFallback(key, keyType) },
                                    )
                            },
                        )
                    } else {
                        _uiState.value =
                            _uiState.value.copy(
                                lastKeyErrorMessage = context.getString(R.string.api_key_invalid, keyType, testResult.message),
                                // Clear success message
                                lastKeyActionMessage = "",
                            )
                    }
                } catch (e: Exception) {
                    _uiState.value =
                        _uiState.value.copy(
                            lastKeyErrorMessage = context.getString(R.string.api_key_setup_error, e.message ?: ""),
                            // Clear success message
                            lastKeyActionMessage = "",
                        )
                }
            }
        }

        /**
         * Sets API key with fallback security (lower security level).
         */
        fun setApiKeyWithFallback(
            key: String,
            keyType: String,
        ) {
            viewModelScope.launch {
                try {
                    secureApiKeyManager.setApiKeyWithFallback(
                        key = key,
                        keyType = keyType,
                        onSuccess = { saveResult ->
                            _uiState.value =
                                _uiState.value.copy(
                                    finnhubKeyPreview = if (keyType.lowercase() == "finnhub") keyRepository.preview(key) else _uiState.value.finnhubKeyPreview,
                                    exchangeKeyPreview = if (keyType.lowercase() == "exchange") keyRepository.preview(key) else _uiState.value.exchangeKeyPreview,
                                    lastKeyActionMessage = context.getString(R.string.api_key_saved_fallback, keyType),
                                )
                        },
                        onError = { error ->
                            _uiState.value =
                                _uiState.value.copy(
                                    lastKeyActionMessage = context.getString(R.string.api_key_save_failed, error),
                                )
                        },
                    )
                } catch (e: Exception) {
                    _uiState.value =
                        _uiState.value.copy(
                            lastKeyActionMessage = context.getString(R.string.api_key_setup_error, e.message ?: ""),
                        )
                }
            }
        }

        /**
         * Gets security status.
         */
        fun getSecurityStatus() = secureApiKeyManager.getSecurityStatus()

        /**
         * Handles biometric fallback dialog actions.
         */
        fun onBiometricFallbackUseFallback() {
            _uiState.value.pendingKeyAction?.invoke()
            _uiState.value =
                _uiState.value.copy(
                    showBiometricFallbackDialog = false,
                    pendingKeyAction = null,
                )
        }

        fun onBiometricFallbackRetry() {
            _uiState.value =
                _uiState.value.copy(
                    showBiometricFallbackDialog = false,
                    pendingKeyAction = null,
                )
            _uiState.value =
                _uiState.value.copy(
                    lastKeyActionMessage = context.getString(R.string.api_key_biometric_retry_required),
                )
        }

        fun onBiometricFallbackCancel() {
            _uiState.value =
                _uiState.value.copy(
                    showBiometricFallbackDialog = false,
                    pendingKeyAction = null,
                    lastKeyActionMessage = context.getString(R.string.api_key_operation_cancelled),
                )
        }

        /**
         * Clears the last action message.
         */
        fun clearLastActionMessage() {
            _uiState.value =
                _uiState.value.copy(
                    lastKeyActionMessage = "",
                    lastKeyErrorMessage = "",
                )
        }

        /**
         * Clears the GPM save prompt flag.
         */
        fun clearGpmSavePrompt() {
            _uiState.value =
                _uiState.value.copy(
                    shouldPromptGpmSave = false,
                )
        }

        /**
         * Sets security level.
         */
        // Note: Security level management is now handled internally by SecureApiKeyManager
        // This method is kept for potential future use but is currently unused
        fun setSecurityLevel(level: com.wealthmanager.security.SecurityLevelManager.SecurityLevel) {
            viewModelScope.launch {
                // secureApiKeyManager.securityLevelManager.setSecurityLevel(level)
                _uiState.value =
                    _uiState.value.copy(
                        lastKeyActionMessage = context.getString(R.string.settings_security_level_set, level.toString()),
                    )
            }
        }

        /**
         * Save API key to Google Password Manager (non-blocking prompt).
         */
        suspend fun saveApiKeyToGpm(
            keyType: String,
            key: String,
        ): Boolean {
            return try {
                val result = credentialManagerService.saveApiKeyToGooglePasswordManager(keyType, key)
                result.isSuccess
            } catch (_: Exception) {
                false
            }
        }

        /**
         * Retrieve API key from Google Password Manager for a given type.
         */
        suspend fun retrieveApiKeyFromGpm(keyType: String): String? {
            return try {
                credentialManagerService.getApiKeyFromGooglePasswordManager(keyType).getOrNull()
            } catch (_: Exception) {
                null
            }
        }
    }
