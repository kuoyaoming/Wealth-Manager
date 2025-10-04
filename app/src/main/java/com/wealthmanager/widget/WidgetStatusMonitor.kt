package com.wealthmanager.widget

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for monitoring widget status and providing recommendations.
 * 
 * This ViewModel provides:
 * - Real-time widget status monitoring
 * - Error state detection
 * - User recommendations
 * - Widget management actions
 */
@HiltViewModel
class WidgetStatusMonitor @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(WidgetStatusUiState())
    val uiState: StateFlow<WidgetStatusUiState> = _uiState.asStateFlow()
    
    init {
        refreshStatus()
    }
    
    /**
     * Refresh widget status and update UI state
     */
    fun refreshStatus() {
        viewModelScope.launch {
            try {
                val displayState = WidgetErrorHandler.determineDisplayState(context)
                val hasAssets = WidgetErrorHandler.hasAssets(context)
                val hasNetwork = WidgetErrorHandler.isNetworkAvailable(context)
                val hasApiKeys = WidgetErrorHandler.hasApiKeys(context)
                val installedCount = WidgetManager.getInstalledWidgetCount(context)
                val recommendations = WidgetErrorHandler.getRecommendations(context)
                val statusMessage = WidgetErrorHandler.getStatusMessage(context)
                
                _uiState.value = _uiState.value.copy(
                    displayState = displayState,
                    hasAssets = hasAssets,
                    hasNetwork = hasNetwork,
                    hasApiKeys = hasApiKeys,
                    installedWidgetCount = installedCount,
                    recommendations = recommendations,
                    statusMessage = statusMessage,
                    isLoading = false,
                    lastUpdated = System.currentTimeMillis()
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to refresh status: ${e.message}"
                )
            }
        }
    }
    
    /**
     * Update widget privacy settings
     */
    fun updatePrivacySettings(showAmount: Boolean, privacyEnabled: Boolean) {
        WidgetPrivacyManager.setShowAssetAmount(context, showAmount)
        WidgetPrivacyManager.setPrivacyEnabled(context, privacyEnabled)
        
        // Refresh status after privacy change
        refreshStatus()
        
        // Update all widgets
        WidgetManager.updateAllWidgets(context)
    }
    
    /**
     * Force update all widgets
     */
    fun forceUpdateWidgets() {
        WidgetManager.updateAllWidgets(context)
        refreshStatus()
    }
    
    /**
     * Get widget installation instructions
     */
    fun getInstallationInstructions(): List<String> {
        return listOf(
            "Long press on home screen",
            "Select 'Widgets' or 'Add Widget'",
            "Find 'Wealth Manager'",
            "Select 'Total Asset Widget'",
            "Choose size and position",
            "Tap to confirm"
        )
    }
    
    /**
     * Check if widget is properly configured
     */
    fun isWidgetProperlyConfigured(): Boolean {
        val state = _uiState.value
        return state.installedWidgetCount > 0 && 
               state.displayState == WidgetErrorHandler.WidgetDisplayState.NORMAL
    }
}

/**
 * UI state for widget status monitoring
 */
data class WidgetStatusUiState(
    val displayState: WidgetErrorHandler.WidgetDisplayState = WidgetErrorHandler.WidgetDisplayState.NORMAL,
    val hasAssets: Boolean = false,
    val hasNetwork: Boolean = true,
    val hasApiKeys: Boolean = false,
    val installedWidgetCount: Int = 0,
    val recommendations: List<String> = emptyList(),
    val statusMessage: String = "",
    val isLoading: Boolean = true,
    val error: String? = null,
    val lastUpdated: Long = 0L
)