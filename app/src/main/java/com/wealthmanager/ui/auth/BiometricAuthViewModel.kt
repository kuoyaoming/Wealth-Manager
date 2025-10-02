package com.wealthmanager.ui.auth

import android.content.Context
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wealthmanager.auth.BiometricAuthManager
import com.wealthmanager.auth.BiometricStatus
import com.wealthmanager.debug.DebugLogManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BiometricAuthViewModel
    @Inject
    constructor(
        private val biometricAuthManager: BiometricAuthManager,
        private val debugLogManager: DebugLogManager,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(BiometricAuthUiState())
        val uiState: StateFlow<BiometricAuthUiState> = _uiState.asStateFlow()

        fun checkBiometricAvailability(context: Context) {
            val status = biometricAuthManager.isBiometricAvailable(context)
            debugLogManager.logBiometric("Biometric Status Check", "Status: $status")
            _uiState.value = _uiState.value.copy(biometricStatus = status)
        }

        fun authenticate(context: Context) {
            debugLogManager.logBiometric("Authentication Started", "Context type: ${context::class.simpleName}")
            if (context is FragmentActivity) {
                val prompt =
                    biometricAuthManager.createBiometricPrompt(
                        activity = context,
                        onSuccess = {
                            debugLogManager.logBiometric("Authentication Success", "User authenticated successfully")
                            _uiState.value =
                                _uiState.value.copy(
                                    isAuthenticated = true,
                                    errorMessage = "",
                                )
                        },
                        onError = { error ->
                            debugLogManager.logBiometric("Authentication Error", "Error: $error")
                            _uiState.value =
                                _uiState.value.copy(
                                    errorMessage = error,
                                    isAuthenticated = false,
                                )
                        },
                        onCancel = {
                            debugLogManager.logBiometric("Authentication Cancelled", "User cancelled authentication")
                            _uiState.value =
                                _uiState.value.copy(
                                    errorMessage = "Authentication cancelled",
                                    isAuthenticated = false,
                                )
                        },
                    )

                viewModelScope.launch {
                    biometricAuthManager.showBiometricPrompt(
                        prompt = prompt,
                        title = context.getString(com.wealthmanager.R.string.biometric_auth_title),
                        subtitle = context.getString(com.wealthmanager.R.string.biometric_auth_subtitle),
                        negativeButtonText = context.getString(com.wealthmanager.R.string.cancel),
                    )
                }
            } else {
                debugLogManager.logBiometric(
                    "Authentication Error",
                    "Context is not FragmentActivity: ${context::class.simpleName}",
                )
                _uiState.value =
                    _uiState.value.copy(
                        errorMessage = "Context is not FragmentActivity",
                        isAuthenticated = false,
                    )
            }
        }
    }

data class BiometricAuthUiState(
    val biometricStatus: BiometricStatus = BiometricStatus.UNKNOWN_ERROR,
    val isAuthenticated: Boolean = false,
    val errorMessage: String = "",
)
