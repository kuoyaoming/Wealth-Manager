package com.wealthmanager.backup

import android.content.Context
import com.wealthmanager.debug.DebugLogManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages settings for Android's Auto Backup feature.
 */
@Singleton
class EnhancedBackupManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val backupPreferencesManager: BackupPreferencesManager,
    private val debugLogManager: DebugLogManager
) {

    // Cache for backup status to avoid repeated calls
    private var cachedBackupStatus: BackupStatus? = null
    private var lastCacheTime: Long = 0
    private val cacheTimeoutMs = 30000L // 30 seconds cache

    /**
     * Backup strategies available to the user.
     */
    enum class BackupStrategy {
        NO_BACKUP,      // No backup enabled
        LOCAL_ONLY      // Android Auto Backup only
    }

    /**
     * Backup status information.
     */
    data class BackupStatus(
        val strategy: BackupStrategy,
        val isLocalBackupEnabled: Boolean,
        val lastLocalBackupTime: Long
    )

    /**
     * Gets the current backup status.
     * Uses caching to avoid repeated calls.
     */
    fun getBackupStatus(): BackupStatus {
        val currentTime = System.currentTimeMillis()

        // Return cached result if still valid
        if (cachedBackupStatus != null && (currentTime - lastCacheTime) < cacheTimeoutMs) {
            debugLogManager.log("ENHANCED_BACKUP", "Returning cached backup status")
            return cachedBackupStatus!!
        }

        val lastLocalBackupTime = backupPreferencesManager.lastLocalBackupTime

        val strategy = if (isLocalBackupEnabled) BackupStrategy.LOCAL_ONLY else BackupStrategy.NO_BACKUP

        debugLogManager.log(
            "ENHANCED_BACKUP",
            "Backup status: $strategy, Local: $isLocalBackupEnabled"
        )

        val backupStatus = BackupStatus(
            strategy = strategy,
            isLocalBackupEnabled = isLocalBackupEnabled,
            lastLocalBackupTime = lastLocalBackupTime
        )

        // Cache the result
        cachedBackupStatus = backupStatus
        lastCacheTime = currentTime

        return backupStatus
    }

    /**
     * Gets the recommended backup strategy based on current conditions.
     */
    fun getRecommendedBackupStrategy(): BackupStrategy {
        return if (isLocalBackupEnabled) {
            BackupStrategy.LOCAL_ONLY
        } else {
            BackupStrategy.NO_BACKUP
        }
    }

    /**
     * Enables or disables local backup (Android Auto Backup).
     */
    var isLocalBackupEnabled: Boolean
        get() = backupPreferencesManager.isFinancialBackupEnabled
        set(enabled) {
            backupPreferencesManager.isFinancialBackupEnabled = enabled
            debugLogManager.log("ENHANCED_BACKUP", "Local backup ${if (enabled) "enabled" else "disabled"}")
            // Clear cache when backup settings change
            clearCache()
        }

    /**
     * Records that a local backup was performed.
     */
    fun recordLocalBackup() {
        val currentTime = System.currentTimeMillis()
        backupPreferencesManager.lastLocalBackupTime = currentTime
        debugLogManager.log("ENHANCED_BACKUP", "Local backup recorded at $currentTime")
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
     * Gets a user-friendly description of the backup strategy.
     */
    fun getBackupStrategyDescription(strategy: BackupStrategy): String {
        return when (strategy) {
            BackupStrategy.NO_BACKUP -> "No backup enabled"
            BackupStrategy.LOCAL_ONLY -> "Android Auto Backup only"
        }
    }

    /**
     * Gets backup statistics for the user.
     */
    fun getBackupStatistics(): Map<String, Any> {
        val status = getBackupStatus()
        return mapOf(
            "local_backup_enabled" to status.isLocalBackupEnabled,
            "current_strategy" to status.strategy.name
        )
    }
}
