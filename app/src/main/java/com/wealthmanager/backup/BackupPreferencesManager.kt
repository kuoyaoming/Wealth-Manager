package com.wealthmanager.backup

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages backup-related preferences.
 * Currently tracks whether financial data should participate in Android Auto Backup.
 */
@Singleton
class BackupPreferencesManager
    @Inject
    constructor(
        @ApplicationContext context: Context,
    ) {
        companion object {
            private const val PREFS_NAME = "backup_settings"
            private const val KEY_FINANCIAL_BACKUP_ENABLED = "financial_backup_enabled"
            private const val KEY_LAST_LOCAL_BACKUP_TIME = "last_local_backup_time"
            private const val KEY_LAST_GPM_BACKUP_TIME = "last_gpm_backup_time"
        }

        private val prefs: SharedPreferences =
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        fun isFinancialBackupEnabled(): Boolean {
            return prefs.getBoolean(KEY_FINANCIAL_BACKUP_ENABLED, false)
        }

        fun setFinancialBackupEnabled(enabled: Boolean) {
            prefs.edit().putBoolean(KEY_FINANCIAL_BACKUP_ENABLED, enabled).apply()
        }

        fun getLastLocalBackupTime(): Long {
            return prefs.getLong(KEY_LAST_LOCAL_BACKUP_TIME, 0L)
        }

        fun setLastLocalBackupTime(timestamp: Long) {
            prefs.edit().putLong(KEY_LAST_LOCAL_BACKUP_TIME, timestamp).apply()
        }

        fun getLastGpmBackupTime(): Long {
            return prefs.getLong(KEY_LAST_GPM_BACKUP_TIME, 0L)
        }

        fun setLastGpmBackupTime(timestamp: Long) {
            prefs.edit().putLong(KEY_LAST_GPM_BACKUP_TIME, timestamp).apply()
        }
    }
