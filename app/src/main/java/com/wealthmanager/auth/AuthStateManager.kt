package com.wealthmanager.auth

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthStateManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs: SharedPreferences = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
    
    private val AUTH_KEY = "is_authenticated"
    private val AUTH_TIMESTAMP_KEY = "auth_timestamp"
    private val BIOMETRIC_ENABLED_KEY = "biometric_enabled"
    private val AUTH_SESSION_TIMEOUT = 24 * 60 * 60 * 1000L // 24 hours in milliseconds
    
    fun setAuthenticated(isAuthenticated: Boolean) {
        prefs.edit().apply {
            putBoolean(AUTH_KEY, isAuthenticated)
            if (isAuthenticated) {
                putLong(AUTH_TIMESTAMP_KEY, System.currentTimeMillis())
            } else {
                remove(AUTH_TIMESTAMP_KEY)
            }
            apply()
        }
    }
    
    fun isAuthenticated(): Boolean {
        val isAuth = prefs.getBoolean(AUTH_KEY, false)
        val authTimestamp = prefs.getLong(AUTH_TIMESTAMP_KEY, 0L)
        val currentTime = System.currentTimeMillis()
        
        // Check if authentication is still valid (within session timeout)
        return isAuth && (currentTime - authTimestamp) < AUTH_SESSION_TIMEOUT
    }
    
    fun clearAuthentication() {
        prefs.edit().apply {
            remove(AUTH_KEY)
            remove(AUTH_TIMESTAMP_KEY)
            apply()
        }
    }

    fun setBiometricEnabled(enabled: Boolean) {
        prefs.edit().apply {
            putBoolean(BIOMETRIC_ENABLED_KEY, enabled)
            if (!enabled) {
                remove(AUTH_KEY)
                remove(AUTH_TIMESTAMP_KEY)
            }
            apply()
        }
    }

    fun isBiometricEnabled(): Boolean {
        return prefs.getBoolean(BIOMETRIC_ENABLED_KEY, true)
    }
    
    fun getAuthTimestamp(): Long {
        return prefs.getLong(AUTH_TIMESTAMP_KEY, 0L)
    }
}