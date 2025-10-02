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
        }

        private val prefs: SharedPreferences =
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        fun isFinancialBackupEnabled(): Boolean {
            return prefs.getBoolean(KEY_FINANCIAL_BACKUP_ENABLED, false)
        }

        fun setFinancialBackupEnabled(enabled: Boolean) {
            prefs.edit().putBoolean(KEY_FINANCIAL_BACKUP_ENABLED, enabled).apply()
        }
    }
