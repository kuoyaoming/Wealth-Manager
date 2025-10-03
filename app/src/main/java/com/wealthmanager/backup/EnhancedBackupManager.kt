package com.wealthmanager.backup

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.wealthmanager.debug.DebugLogManager
import com.wealthmanager.security.CredentialManagerService
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Enhanced backup manager that integrates existing Android Auto Backup with Google Password Manager.
 * Provides multiple backup strategies while maintaining backward compatibility.
 */
@Singleton
class EnhancedBackupManager
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
        private val backupPreferencesManager: BackupPreferencesManager,
        private val credentialManagerService: CredentialManagerService,
        private val debugLogManager: DebugLogManager,
    ) {
        // Cache for backup status to avoid repeated calls
        private var cachedBackupStatus: BackupStatus? = null
        private var lastCacheTime: Long = 0
        private val cacheTimeoutMs = 30000L // 30 seconds cache

        /**
         * Backup strategies available to the user.
         */
        enum class BackupStrategy {
            NO_BACKUP, // No backup enabled
            LOCAL_ONLY, // Android Auto Backup only (existing)
            GOOGLE_ONLY, // Google Password Manager only
            DUAL_BACKUP, // Both Android Auto Backup and Google Password Manager
        }

        /**
         * Backup status information.
         */
        data class BackupStatus(
            val strategy: BackupStrategy,
            val isLocalBackupEnabled: Boolean,
            val isGooglePasswordManagerAvailable: Boolean,
            val isGoogleAccountSignedIn: Boolean,
            val storedKeyTypes: List<String>,
            val lastLocalBackupTime: Long,
            val lastGpmBackupTime: Long,
        )

        /**
         * Gets the current backup status and available strategies.
         * Uses caching to avoid repeated calls that could trigger credential dialogs.
         */
        suspend fun getBackupStatus(): BackupStatus {
            val currentTime = System.currentTimeMillis()

            // Return cached result if still valid
            if (cachedBackupStatus != null && (currentTime - lastCacheTime) < cacheTimeoutMs) {
                debugLogManager.log("ENHANCED_BACKUP", "Returning cached backup status")
                return cachedBackupStatus!!
            }

            val isLocalBackupEnabled = backupPreferencesManager.isFinancialBackupEnabled()
            val isGooglePasswordManagerAvailable = credentialManagerService.isGooglePasswordManagerAvailable()
            val isGoogleAccountSignedIn = isGoogleAccountSignedIn()
            val storedKeyTypes =
                if (isGooglePasswordManagerAvailable) {
                    credentialManagerService.listStoredKeyTypes()
                } else {
                    emptyList()
                }
            val lastLocalBackupTime = backupPreferencesManager.getLastLocalBackupTime()
            val lastGpmBackupTime = backupPreferencesManager.getLastGpmBackupTime()

            val strategy =
                determineBackupStrategy(
                    isLocalBackupEnabled,
                    isGooglePasswordManagerAvailable,
                    isGoogleAccountSignedIn,
                )

            debugLogManager.log(
                "ENHANCED_BACKUP",
                "Backup status: $strategy, Local: $isLocalBackupEnabled, Google: $isGooglePasswordManagerAvailable",
            )

            val backupStatus =
                BackupStatus(
                    strategy = strategy,
                    isLocalBackupEnabled = isLocalBackupEnabled,
                    isGooglePasswordManagerAvailable = isGooglePasswordManagerAvailable,
                    isGoogleAccountSignedIn = isGoogleAccountSignedIn,
                    storedKeyTypes = storedKeyTypes,
                    lastLocalBackupTime = lastLocalBackupTime,
                    lastGpmBackupTime = lastGpmBackupTime,
                )

            // Cache the result
            cachedBackupStatus = backupStatus
            lastCacheTime = currentTime

            return backupStatus
        }

        /**
         * Gets the recommended backup strategy based on current conditions.
         */
        suspend fun getRecommendedBackupStrategy(): BackupStrategy {
            val status = getBackupStatus()

            return when {
                status.isGooglePasswordManagerAvailable && status.isGoogleAccountSignedIn -> {
                    BackupStrategy.DUAL_BACKUP
                }
                status.isGooglePasswordManagerAvailable -> {
                    BackupStrategy.GOOGLE_ONLY
                }
                status.isLocalBackupEnabled -> {
                    BackupStrategy.LOCAL_ONLY
                }
                else -> {
                    BackupStrategy.NO_BACKUP
                }
            }
        }

        /**
         * Enables or disables local backup (Android Auto Backup).
         */
        fun setLocalBackupEnabled(enabled: Boolean) {
            backupPreferencesManager.setFinancialBackupEnabled(enabled)
            debugLogManager.log("ENHANCED_BACKUP", "Local backup ${if (enabled) "enabled" else "disabled"}")
            // Clear cache when backup settings change
            clearCache()
        }

        /**
         * Records that a local backup was performed.
         */
        fun recordLocalBackup() {
            val currentTime = System.currentTimeMillis()
            backupPreferencesManager.setLastLocalBackupTime(currentTime)
            debugLogManager.log("ENHANCED_BACKUP", "Local backup recorded at $currentTime")
        }

        /**
         * Records that a Google Password Manager backup was performed.
         */
        fun recordGpmBackup() {
            val currentTime = System.currentTimeMillis()
            backupPreferencesManager.setLastGpmBackupTime(currentTime)
            debugLogManager.log("ENHANCED_BACKUP", "GPM backup recorded at $currentTime")
            // Clear cache when backup status changes
            clearCache()
        }

        /**
         * Clears the backup status cache.
         * Should be called when backup settings change.
         */
        fun clearCache() {
            cachedBackupStatus = null
            lastCacheTime = 0
            debugLogManager.log("ENHANCED_BACKUP", "Backup status cache cleared")
        }

        /**
         * Checks if local backup is enabled.
         */
        fun isLocalBackupEnabled(): Boolean {
            return backupPreferencesManager.isFinancialBackupEnabled()
        }

        /**
         * Checks if Google Password Manager is available and accessible.
         */
        suspend fun isGooglePasswordManagerAvailable(): Boolean {
            return credentialManagerService.isGooglePasswordManagerAvailable()
        }

        /**
         * Checks if Google account is signed in.
         */
        fun isGoogleAccountSignedIn(): Boolean {
            return try {
                val account = GoogleSignIn.getLastSignedInAccount(context)
                account != null
            } catch (e: Exception) {
                debugLogManager.logError("ENHANCED_BACKUP", "Failed to check Google account status: ${e.message}")
                false
            }
        }

        /**
         * Gets the list of key types stored in Google Password Manager.
         */
        suspend fun getStoredKeyTypes(): List<String> {
            return try {
                credentialManagerService.listStoredKeyTypes()
            } catch (e: Exception) {
                debugLogManager.logError("ENHANCED_BACKUP", "Failed to get stored key types: ${e.message}")
                emptyList()
            }
        }

        /**
         * Checks if a specific key type is stored in Google Password Manager.
         */
        suspend fun isKeyTypeStoredInGoogle(keyType: String): Boolean {
            return try {
                credentialManagerService.isKeyTypeStored(keyType)
            } catch (e: Exception) {
                debugLogManager.logError("ENHANCED_BACKUP", "Failed to check if $keyType is stored: ${e.message}")
                false
            }
        }

        /**
         * Gets backup recommendations for the user.
         */
        suspend fun getBackupRecommendations(): List<String> {
            val recommendations = mutableListOf<String>()
            val status = getBackupStatus()

            if (!status.isGoogleAccountSignedIn && status.isGooglePasswordManagerAvailable) {
                recommendations.add("Sign in to Google account to enable Google Password Manager backup")
            }

            if (!status.isLocalBackupEnabled && !status.isGooglePasswordManagerAvailable) {
                recommendations.add("Enable local backup to protect your API keys")
            }

            if (status.isGooglePasswordManagerAvailable && status.isGoogleAccountSignedIn && !status.isLocalBackupEnabled) {
                recommendations.add("Consider enabling dual backup for maximum protection")
            }

            if (status.storedKeyTypes.isEmpty() && status.isGooglePasswordManagerAvailable) {
                recommendations.add("Save your API keys to Google Password Manager for cross-device access")
            }

            return recommendations
        }

        /**
         * Determines the current backup strategy based on available options.
         */
        private fun determineBackupStrategy(
            isLocalBackupEnabled: Boolean,
            isGooglePasswordManagerAvailable: Boolean,
            isGoogleAccountSignedIn: Boolean,
        ): BackupStrategy {
            return when {
                isLocalBackupEnabled && isGooglePasswordManagerAvailable && isGoogleAccountSignedIn -> {
                    BackupStrategy.DUAL_BACKUP
                }
                isGooglePasswordManagerAvailable && isGoogleAccountSignedIn -> {
                    BackupStrategy.GOOGLE_ONLY
                }
                isLocalBackupEnabled -> {
                    BackupStrategy.LOCAL_ONLY
                }
                else -> {
                    BackupStrategy.NO_BACKUP
                }
            }
        }

        /**
         * Gets a user-friendly description of the backup strategy.
         */
        fun getBackupStrategyDescription(strategy: BackupStrategy): String {
            return when (strategy) {
                BackupStrategy.NO_BACKUP -> "No backup enabled"
                BackupStrategy.LOCAL_ONLY -> "Android Auto Backup only"
                BackupStrategy.GOOGLE_ONLY -> "Google Password Manager only"
                BackupStrategy.DUAL_BACKUP -> "Dual backup (Android Auto Backup + Google Password Manager)"
            }
        }

        /**
         * Gets backup statistics for the user.
         */
        suspend fun getBackupStatistics(): Map<String, Any> {
            val status = getBackupStatus()

            return mapOf(
                "local_backup_enabled" to status.isLocalBackupEnabled,
                "google_password_manager_available" to status.isGooglePasswordManagerAvailable,
                "google_account_signed_in" to status.isGoogleAccountSignedIn,
                "stored_key_types_count" to status.storedKeyTypes.size,
                "stored_key_types" to status.storedKeyTypes,
                "current_strategy" to status.strategy.name,
                "recommendations_count" to getBackupRecommendations().size,
            )
        }
    }
