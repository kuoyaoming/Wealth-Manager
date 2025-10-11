package com.wealthmanager.backup

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

private const val PREFS_NAME = "backup_settings"
private const val KEY_FINANCIAL_BACKUP_ENABLED = "financial_backup_enabled"
private const val KEY_LAST_LOCAL_BACKUP_TIME = "last_local_backup_time"
private const val KEY_LAST_GPM_BACKUP_TIME = "last_gpm_backup_time"

/**
 * Manages backup-related preferences using modern Kotlin properties.
 * This class tracks settings like financial data backup participation and backup timestamps.
 */
@Singleton
class BackupPreferencesManager @Inject constructor(@ApplicationContext context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    /**
     * Determines if financial data is included in Android Auto Backup.
     * Defaults to false.
     */
    var isFinancialBackupEnabled: Boolean
        get() = prefs.getBoolean(KEY_FINANCIAL_BACKUP_ENABLED, false)
        set(value) = prefs.edit().putBoolean(KEY_FINANCIAL_BACKUP_ENABLED, value).apply()

    /**
     * Stores the timestamp of the last successful local backup.
     * Defaults to 0 if no backup has been made.
     */
    var lastLocalBackupTime: Long
        get() = prefs.getLong(KEY_LAST_LOCAL_BACKUP_TIME, 0L)
        set(value) = prefs.edit().putLong(KEY_LAST_LOCAL_BACKUP_TIME, value).apply()

    /**
     * Stores the timestamp of the last successful backup to Google Password Manager.
     * Defaults to 0 if no backup has been made.
     */
    var lastGpmBackupTime: Long
        get() = prefs.getLong(KEY_LAST_GPM_BACKUP_TIME, 0L)
        set(value) = prefs.edit().putLong(KEY_LAST_GPM_BACKUP_TIME, value).apply()
}
