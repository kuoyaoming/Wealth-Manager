package com.wealthmanager.data.service

import com.wealthmanager.debug.DebugLogManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiStatusManager @Inject constructor(
    private val debugLogManager: DebugLogManager
) {
    
    private val _apiStatus = MutableStateFlow(ApiStatus())
    val apiStatus: StateFlow<ApiStatus> = _apiStatus.asStateFlow()
    
    fun setApiError(error: String, isDataStale: Boolean = false, isRetrying: Boolean = false) {
        debugLogManager.logWarning("API_STATUS", "API Error: $error, Data Stale: $isDataStale, Retrying: $isRetrying")
        _apiStatus.value = ApiStatus(
            hasError = true,
            errorMessage = error,
            isRetrying = isRetrying,
            isDataStale = isDataStale,
            lastErrorTime = System.currentTimeMillis()
        )
    }
    
    fun setApiSuccess() {
        debugLogManager.log("API_STATUS", "API Success - clearing error state")
        _apiStatus.value = ApiStatus(
            hasError = false,
            errorMessage = "",
            isRetrying = false,
            isDataStale = false,
            lastErrorTime = 0L
        )
    }
    
    fun setRetrying(isRetrying: Boolean) {
        debugLogManager.log("API_STATUS", "Retry state changed: $isRetrying")
        _apiStatus.value = _apiStatus.value.copy(isRetrying = isRetrying)
    }
    
    fun clearError() {
        debugLogManager.log("API_STATUS", "Error state cleared by user")
        _apiStatus.value = ApiStatus()
    }
}

data class ApiStatus(
    val hasError: Boolean = false,
    val errorMessage: String = "",
    val isRetrying: Boolean = false,
    val isDataStale: Boolean = false,
    val lastErrorTime: Long = 0L
)