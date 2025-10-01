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
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
 

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
    val lastKeyActionMessage: String = ""
)

data class LanguageOption(
    val languageCode: String,
    val displayNameRes: Int
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val authStateManager: AuthStateManager,
    private val backupPreferencesManager: BackupPreferencesManager,
    private val localePreferencesManager: LocalePreferencesManager,
    private val apiTestService: ApiTestService,
    private val keyRepository: KeyRepository,
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

    fun validateAndSaveFinnhubKey(input: String) {
        viewModelScope.launch {
            val trimmed = input.trim()
            val result = apiTestService.testFinnhubApiWithKey(trimmed)
            if (result.isWorking) {
                keyRepository.setUserFinnhubKey(trimmed)
                _uiState.value = _uiState.value.copy(
                    finnhubKeyPreview = keyRepository.preview(trimmed),
                    lastKeyActionMessage = "Finnhub key saved"
                )
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
                keyRepository.setUserExchangeKey(trimmed)
                _uiState.value = _uiState.value.copy(
                    exchangeKeyPreview = keyRepository.preview(trimmed),
                    lastKeyActionMessage = "Exchange Rate key saved"
                )
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
}

