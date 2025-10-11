package com.wealthmanager.ui.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wealthmanager.R
import com.wealthmanager.auth.AuthStateManager
import com.wealthmanager.backup.BackupPreferencesManager
import com.wealthmanager.backup.EnhancedBackupManager
import com.wealthmanager.data.FirstLaunchManager
import com.wealthmanager.preferences.LocalePreferencesManager
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
 */
data class SettingsUiState(
    val biometricEnabled: Boolean = false,
    val financialBackupEnabled: Boolean = false,
    val currentLanguageCode: String = "",
    val availableLanguages: List<LanguageOption> = emptyList(),
    // Widget privacy settings
    val widgetShowAssetAmount: Boolean = true,
    val widgetPrivacyEnabled: Boolean = false,
    val widgetInstalledCount: Int = 0,
    // Enhanced backup state
    val backupStatus: EnhancedBackupManager.BackupStatus? = null,
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
                    // Initialize widget privacy settings
                    widgetShowAssetAmount =
                        com.wealthmanager.widget.WidgetPrivacyManager.shouldShowAssetAmount(
                            context,
                        ),
                    widgetPrivacyEnabled = com.wealthmanager.widget.WidgetPrivacyManager.isPrivacyEnabled(context),
                    widgetInstalledCount = com.wealthmanager.widget.WidgetManager.getInstalledWidgetCount(context),
                ),
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
                    _uiState.value =
                        _uiState.value.copy(
                            backupStatus = backupStatus,
                        )
                } catch (e: Exception) {
                    // Handle error silently or log it
                }
            }
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
    }
