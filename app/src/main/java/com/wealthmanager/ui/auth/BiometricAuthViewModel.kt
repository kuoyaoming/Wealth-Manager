package com.wealthmanager.ui.auth

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wealthmanager.debug.DebugLogManager
import com.wealthmanager.security.BiometricProtectionManager
import com.wealthmanager.security.BiometricStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BiometricAuthViewModel @Inject constructor(
    private val biometricProtectionManager: BiometricProtectionManager,
    private val debugLogManager: DebugLogManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow(BiometricAuthUiState())
    val uiState: StateFlow<BiometricAuthUiState> = _uiState.asStateFlow()

    fun checkBiometricAvailability() {
        val status = biometricProtectionManager.isBiometricAvailable()
        debugLogManager.logBiometric("Biometric Status Check", "Status: $status")
        _uiState.value = _uiState.value.copy(biometricStatus = status)
    }

    fun authenticate(activity: FragmentActivity) {
        debugLogManager.logBiometric("Authentication Started", "Activity: ${activity::class.simpleName}")

        viewModelScope.launch {
            biometricProtectionManager.showBiometricPrompt(
                activity = activity,
                onSuccess = {
                    debugLogManager.logBiometric("Authentication Success", "User authenticated successfully")
                    _uiState.value = _uiState.value.copy(
                        isAuthenticated = true,
                        errorMessage = "",
                    )
                },
                onError = { error ->
                    debugLogManager.logBiometric("Authentication Error", "Error: $error")
                    _uiState.value = _uiState.value.copy(
                        errorMessage = error,
                        isAuthenticated = false,
                    )
                },
                onCancel = {
                    debugLogManager.logBiometric("Authentication Cancelled", "User cancelled authentication")
                    _uiState.value = _uiState.value.copy(
                        errorMessage = "",
                        isAuthenticated = false,
                    )
                }
            )
        }
    }

    fun getBiometricStatusDescription(): String {
        return biometricProtectionManager.getBiometricStatusDescription()
    }
}

data class BiometricAuthUiState(
    val biometricStatus: BiometricStatus = BiometricStatus.UNKNOWN,
    val isAuthenticated: Boolean = false,
    val errorMessage: String = "",
)
