package com.wealthmanager

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wealthmanager.auth.AuthStateManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MainUiState(
    val isAuthenticated: Boolean? = null // null on initial load
)

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authStateManager: AuthStateManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState = _uiState.asStateFlow()

    init {
        checkAuthStatus()
    }

    private fun checkAuthStatus() {
        viewModelScope.launch {
            val isAuthenticated = authStateManager.isAuthenticated()
            _uiState.update { it.copy(isAuthenticated = isAuthenticated) }
        }
    }

    fun onAuthSuccess() {
        authStateManager.setAuthenticated(true)
        _uiState.update { it.copy(isAuthenticated = true) }
    }

    fun onAuthSkipped() {
        // If user skips, we treat them as not authenticated for this session.
        authStateManager.setAuthenticated(false)
        _uiState.update { it.copy(isAuthenticated = false) }
    }
}
