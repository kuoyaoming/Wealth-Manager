package com.wealthmanager.ui.auth

import android.content.Context
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wealthmanager.auth.BiometricAuthManager
import com.wealthmanager.auth.BiometricStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BiometricAuthViewModel @Inject constructor(
    private val biometricAuthManager: BiometricAuthManager
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(BiometricAuthUiState())
    val uiState: StateFlow<BiometricAuthUiState> = _uiState.asStateFlow()
    
    fun checkBiometricAvailability(context: Context) {
        val status = biometricAuthManager.isBiometricAvailable(context)
        _uiState.value = _uiState.value.copy(biometricStatus = status)
    }
    
    fun authenticate(context: Context) {
        if (context is FragmentActivity) {
            val prompt = biometricAuthManager.createBiometricPrompt(
                activity = context,
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        isAuthenticated = true,
                        errorMessage = ""
                    )
                },
                onError = { error ->
                    _uiState.value = _uiState.value.copy(
                        errorMessage = error,
                        isAuthenticated = false
                    )
                },
                onCancel = {
                    _uiState.value = _uiState.value.copy(
                        errorMessage = "Authentication cancelled",
                        isAuthenticated = false
                    )
                }
            )
            
            viewModelScope.launch {
                biometricAuthManager.showBiometricPrompt(
                    prompt = prompt,
                    title = context.getString(com.wealthmanager.R.string.biometric_auth_title),
                    subtitle = context.getString(com.wealthmanager.R.string.biometric_auth_subtitle),
                    negativeButtonText = context.getString(com.wealthmanager.R.string.cancel)
                )
            }
        }
    }
}

data class BiometricAuthUiState(
    val biometricStatus: BiometricStatus = BiometricStatus.UNKNOWN_ERROR,
    val isAuthenticated: Boolean = false,
    val errorMessage: String = ""
)