package com.wealthmanager.security

import android.content.Context
import android.content.SharedPreferences
import com.wealthmanager.R
import com.wealthmanager.debug.DebugLogManager
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Security level manager for controlling feature access based on security requirements.
 * 
 * This manager handles:
 * - Security level configuration and management
 * - Feature permission control based on security level
 * - Biometric authentication requirements
 * - Fallback security modes
 * 
 * @property context Android context for preferences access
 * @property debugLogManager Manager for debug logging
 */
@Singleton
class SecurityLevelManager @Inject constructor(
    private val context: Context,
    private val debugLogManager: DebugLogManager
) {
    
    companion object {
        private const val PREFS_NAME = "security_level_prefs"
        private const val KEY_SECURITY_LEVEL = "security_level"
        private const val KEY_BIOMETRIC_ENABLED = "biometric_enabled"
        private const val KEY_FALLBACK_MODE = "fallback_mode"
    }
    
    private val prefs: SharedPreferences = 
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    
    /**
     * Enumeration of security levels for feature access control.
     */
    enum class SecurityLevel {
        HIGH,      // High security: requires biometric authentication
        MEDIUM,    // Medium security: requires password or PIN
        LOW,       // Low security: basic authentication
        FALLBACK   // Fallback level: minimum security requirements
    }
    
    /**
     * Enumeration of feature permission levels.
     */
    enum class FeaturePermission {
        FULL_ACCESS,        // Full access
        LIMITED_ACCESS,     // Limited access
        NO_ACCESS          // No access
    }
    
    /**
     * Gets the current security level configuration.
     * 
     * @return Current security level, defaults to HIGH if not set
     */
    fun getCurrentSecurityLevel(): SecurityLevel {
        val levelString = prefs.getString(KEY_SECURITY_LEVEL, SecurityLevel.HIGH.name)
        return try {
            SecurityLevel.valueOf(levelString ?: SecurityLevel.HIGH.name)
        } catch (e: IllegalArgumentException) {
            SecurityLevel.HIGH
        }
    }
    
    /**
     * Sets the security level configuration.
     * 
     * @param level The security level to set
     */
    fun setSecurityLevel(level: SecurityLevel) {
        prefs.edit().apply {
            putString(KEY_SECURITY_LEVEL, level.name)
            apply()
        }
        debugLogManager.log("SECURITY_LEVEL", "Security level set to: $level")
    }
    
    /**
     * 檢查功能權限
     */
    fun checkFeaturePermission(feature: SecurityFeature): FeaturePermission {
        val currentLevel = getCurrentSecurityLevel()
        return when (feature) {
            SecurityFeature.API_KEY_MANAGEMENT -> {
                when (currentLevel) {
                    SecurityLevel.HIGH -> FeaturePermission.FULL_ACCESS
                    SecurityLevel.MEDIUM -> FeaturePermission.LIMITED_ACCESS
                    SecurityLevel.LOW -> FeaturePermission.LIMITED_ACCESS
                    SecurityLevel.FALLBACK -> FeaturePermission.LIMITED_ACCESS
                }
            }
            SecurityFeature.FINANCIAL_DATA_ACCESS -> {
                when (currentLevel) {
                    SecurityLevel.HIGH -> FeaturePermission.FULL_ACCESS
                    SecurityLevel.MEDIUM -> FeaturePermission.FULL_ACCESS
                    SecurityLevel.LOW -> FeaturePermission.LIMITED_ACCESS
                    SecurityLevel.FALLBACK -> FeaturePermission.LIMITED_ACCESS
                }
            }
            SecurityFeature.BACKUP_RESTORE -> {
                when (currentLevel) {
                    SecurityLevel.HIGH -> FeaturePermission.FULL_ACCESS
                    SecurityLevel.MEDIUM -> FeaturePermission.LIMITED_ACCESS
                    SecurityLevel.LOW -> FeaturePermission.NO_ACCESS
                    SecurityLevel.FALLBACK -> FeaturePermission.NO_ACCESS
                }
            }
            SecurityFeature.SETTINGS_ACCESS -> {
                when (currentLevel) {
                    SecurityLevel.HIGH -> FeaturePermission.FULL_ACCESS
                    SecurityLevel.MEDIUM -> FeaturePermission.FULL_ACCESS
                    SecurityLevel.LOW -> FeaturePermission.FULL_ACCESS
                    SecurityLevel.FALLBACK -> FeaturePermission.LIMITED_ACCESS
                }
            }
        }
    }
    
    /**
     * 檢查是否需要生物驗證
     */
    fun requiresBiometricAuth(feature: SecurityFeature): Boolean {
        val currentLevel = getCurrentSecurityLevel()
        return when (currentLevel) {
            SecurityLevel.HIGH -> true
            SecurityLevel.MEDIUM -> feature == SecurityFeature.API_KEY_MANAGEMENT || feature == SecurityFeature.BACKUP_RESTORE
            SecurityLevel.LOW -> feature == SecurityFeature.API_KEY_MANAGEMENT
            SecurityLevel.FALLBACK -> false
        }
    }
    
    /**
     * 獲取功能描述
     */
    fun getFeatureDescription(feature: SecurityFeature): String {
        val permission = checkFeaturePermission(feature)
        return when (permission) {
            FeaturePermission.FULL_ACCESS -> context.getString(R.string.feature_permission_full_access)
            FeaturePermission.LIMITED_ACCESS -> context.getString(R.string.feature_permission_limited_access)
            FeaturePermission.NO_ACCESS -> context.getString(R.string.feature_permission_no_access)
        }
    }
    
    /**
     * 設定備案模式
     */
    fun setFallbackMode(enabled: Boolean) {
        prefs.edit().apply {
            putBoolean(KEY_FALLBACK_MODE, enabled)
            if (enabled) {
                putString(KEY_SECURITY_LEVEL, SecurityLevel.FALLBACK.name)
            }
            apply()
        }
        debugLogManager.log("SECURITY_LEVEL", "Fallback mode set to: $enabled")
    }
    
    /**
     * 檢查是否在備案模式
     */
    fun isInFallbackMode(): Boolean {
        return prefs.getBoolean(KEY_FALLBACK_MODE, false)
    }
    
    /**
     * 獲取安全級別建議
     */
    fun getSecurityLevelRecommendation(): String {
        val currentLevel = getCurrentSecurityLevel()
        return when (currentLevel) {
            SecurityLevel.HIGH -> context.getString(R.string.security_recommendation_high)
            SecurityLevel.MEDIUM -> context.getString(R.string.security_recommendation_medium)
            SecurityLevel.LOW -> context.getString(R.string.security_recommendation_low)
            SecurityLevel.FALLBACK -> context.getString(R.string.security_recommendation_fallback)
        }
    }
}

/**
 * 安全功能枚舉
 */
enum class SecurityFeature {
    API_KEY_MANAGEMENT,      // API key management
    FINANCIAL_DATA_ACCESS,   // Financial data access
    BACKUP_RESTORE,          // Backup and restore
    SETTINGS_ACCESS          // Settings access
}
