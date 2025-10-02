package com.wealthmanager.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wealthmanager.R
import com.wealthmanager.auth.AuthStateManager
import com.wealthmanager.backup.BackupPreferencesManager
import com.wealthmanager.data.FirstLaunchManager
import com.wealthmanager.data.service.ApiTestService
import com.wealthmanager.preferences.LocalePreferencesManager
import com.wealthmanager.security.KeyRepository
import com.wealthmanager.ui.security.SecureApiKeyManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
 

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
    // Biometric fallback dialog state
    val showBiometricFallbackDialog: Boolean = false,
    val pendingKeyAction: (() -> Unit)? = null
)

/**
 * Data class representing a language option for the settings screen.
 * 
 * @property languageCode The language code (e.g., "en", "zh-TW")
 * @property displayNameRes Resource ID for the display name string
 */
data class LanguageOption(
    val languageCode: String,
    val displayNameRes: Int
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
class SettingsViewModel @Inject constructor(
    private val authStateManager: AuthStateManager,
    private val backupPreferencesManager: BackupPreferencesManager,
    private val localePreferencesManager: LocalePreferencesManager,
    private val apiTestService: ApiTestService,
    private val keyRepository: KeyRepository,
    private val secureApiKeyManager: SecureApiKeyManager,
    val firstLaunchManager: FirstLaunchManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        SettingsUiState(
            biometricEnabled = authStateManager.isBiometricEnabled(),
            financialBackupEnabled = backupPreferencesManager.isFinancialBackupEnabled(),
            currentLanguageCode = localePreferencesManager.getLanguageCode(),
            availableLanguages = listOf(
                LanguageOption(languageCode = "en", displayNameRes = R.string.language_option_english),
                LanguageOption(languageCode = "zh-TW", displayNameRes = R.string.language_option_traditional_chinese)
            ),
            finnhubKeyPreview = keyRepository.preview(keyRepository.getUserFinnhubKey()),
            exchangeKeyPreview = keyRepository.preview(keyRepository.getUserExchangeKey())
        )
    )
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

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
        }
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
                _uiState.value = _uiState.value.copy(
                    apiTestResults = results,
                    isTestingApis = false
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
                
                _uiState.value = _uiState.value.copy(
                    apiTestResults = results,
                    isTestingApis = false,
                    lastKeyActionMessage = "安全測試完成: ${secureResult.overallSecurity}"
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isTestingApis = false,
                    lastKeyActionMessage = "安全測試失敗: ${e.message}"
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
                    _uiState.value = _uiState.value.copy(
                        finnhubKeyPreview = keyRepository.preview(trimmed),
                        lastKeyActionMessage = "Finnhub key saved with ${validationResult.strength} strength"
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        lastKeyActionMessage = "Key validation failed: ${validationResult.issues.joinToString(", ")}"
                    )
                }
            } else {
                _uiState.value = _uiState.value.copy(
                    lastKeyActionMessage = "Finnhub key invalid: ${result.message}"
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
                    _uiState.value = _uiState.value.copy(
                        exchangeKeyPreview = keyRepository.preview(trimmed),
                        lastKeyActionMessage = "Exchange Rate key saved with ${validationResult.strength} strength"
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        lastKeyActionMessage = "Key validation failed: ${validationResult.issues.joinToString(", ")}"
                    )
                }
            } else {
                _uiState.value = _uiState.value.copy(
                    lastKeyActionMessage = "Exchange Rate key invalid: ${result.message}"
                )
            }
        }
    }

    fun clearFinnhubKey() {
        keyRepository.clearUserFinnhubKey()
        _uiState.value = _uiState.value.copy(
            finnhubKeyPreview = "",
            lastKeyActionMessage = "Finnhub key cleared"
        )
    }

    fun clearExchangeKey() {
        keyRepository.clearUserExchangeKey()
        _uiState.value = _uiState.value.copy(
            exchangeKeyPreview = "",
            lastKeyActionMessage = "Exchange Rate key cleared"
        )
    }
    
    /**
     * Sets API key using secure mechanism with biometric fallback.
     */
    fun setApiKeySecurely(key: String, keyType: String, activity: androidx.fragment.app.FragmentActivity? = null) {
        viewModelScope.launch {
            try {
                val validationResult = secureApiKeyManager.validateKeyStrength(key, keyType)
                if (!validationResult.isValid) {
                    _uiState.value = _uiState.value.copy(
                        lastKeyActionMessage = "金鑰驗證失敗: ${validationResult.issues.joinToString(", ")}"
                    )
                    return@launch
                }
                
                val testResult = when (keyType.lowercase()) {
                    "finnhub" -> apiTestService.testFinnhubApiWithKey(key)
                    "exchange" -> apiTestService.testExchangeRateApiWithKey(key)
                    else -> {
                        _uiState.value = _uiState.value.copy(
                            lastKeyActionMessage = "不支援的金鑰類型: $keyType"
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
                            _uiState.value = _uiState.value.copy(
                                finnhubKeyPreview = if (keyType.lowercase() == "finnhub") keyRepository.preview(key) else _uiState.value.finnhubKeyPreview,
                                exchangeKeyPreview = if (keyType.lowercase() == "exchange") keyRepository.preview(key) else _uiState.value.exchangeKeyPreview,
                                lastKeyActionMessage = "${keyType}金鑰已安全儲存 (強度: ${saveResult.strength})"
                            )
                        },
                        onError = { error ->
                            _uiState.value = _uiState.value.copy(
                                lastKeyActionMessage = "金鑰儲存失敗: $error"
                            )
                        },
                        onBiometricRequired = {
                            _uiState.value = _uiState.value.copy(
                                lastKeyActionMessage = "需要生物識別驗證才能儲存金鑰"
                            )
                        },
                        onFallbackRequired = {
                            _uiState.value = _uiState.value.copy(
                                showBiometricFallbackDialog = true,
                                pendingKeyAction = { setApiKeyWithFallback(key, keyType) }
                            )
                        }
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        lastKeyActionMessage = "${keyType}金鑰無效: ${testResult.message}"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    lastKeyActionMessage = "設定金鑰時發生錯誤: ${e.message}"
                )
            }
        }
    }
    
    /**
     * Sets API key with fallback security (lower security level).
     */
    fun setApiKeyWithFallback(key: String, keyType: String) {
        viewModelScope.launch {
            try {
                secureApiKeyManager.setApiKeyWithFallback(
                    key = key,
                    keyType = keyType,
                    onSuccess = { saveResult ->
                        _uiState.value = _uiState.value.copy(
                            finnhubKeyPreview = if (keyType.lowercase() == "finnhub") keyRepository.preview(key) else _uiState.value.finnhubKeyPreview,
                            exchangeKeyPreview = if (keyType.lowercase() == "exchange") keyRepository.preview(key) else _uiState.value.exchangeKeyPreview,
                            lastKeyActionMessage = "${keyType}金鑰已儲存 (備案安全級別)"
                        )
                    },
                    onError = { error ->
                        _uiState.value = _uiState.value.copy(
                            lastKeyActionMessage = "金鑰儲存失敗: $error"
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    lastKeyActionMessage = "設定金鑰時發生錯誤: ${e.message}"
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
        _uiState.value = _uiState.value.copy(
            showBiometricFallbackDialog = false,
            pendingKeyAction = null
        )
    }
    
    fun onBiometricFallbackRetry() {
        _uiState.value = _uiState.value.copy(
            showBiometricFallbackDialog = false,
            pendingKeyAction = null
        )
        _uiState.value = _uiState.value.copy(
            lastKeyActionMessage = "請重新嘗試生物識別驗證"
        )
    }
    
    fun onBiometricFallbackCancel() {
        _uiState.value = _uiState.value.copy(
            showBiometricFallbackDialog = false,
            pendingKeyAction = null,
            lastKeyActionMessage = "操作已取消"
        )
    }
    
    /**
     * Sets security level.
     */
    // Note: Security level management is now handled internally by SecureApiKeyManager
    // This method is kept for potential future use but is currently unused
    /*
    fun setSecurityLevel(level: com.wealthmanager.security.SecurityLevelManager.SecurityLevel) {
        viewModelScope.launch {
            // secureApiKeyManager.securityLevelManager.setSecurityLevel(level)
            _uiState.value = _uiState.value.copy(
                lastKeyActionMessage = "安全級別已設定為: $level"
            )
        }
    }
    */
    
}

