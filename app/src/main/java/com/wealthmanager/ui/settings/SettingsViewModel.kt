package com.wealthmanager.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wealthmanager.R
import com.wealthmanager.auth.AuthStateManager
import com.wealthmanager.backup.BackupPreferencesManager
import com.wealthmanager.data.FirstLaunchManager
import com.wealthmanager.data.service.ApiTestService
import com.wealthmanager.preferences.LocalePreferencesManager
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
    val isTestingApis: Boolean = false
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
            )
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
}

