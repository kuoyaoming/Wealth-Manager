package com.wealthmanager.data

import android.content.Context
import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton

/**
 * First Launch Detection Manager
 * Used to detect if this is the first time opening the app and manage related states
 */
@Singleton
class FirstLaunchManager @Inject constructor(
    private val context: Context
) {
    
    companion object {
        private const val PREFS_NAME = "wealth_manager_prefs"
        private const val KEY_FIRST_LAUNCH = "first_launch"
        private const val KEY_APP_VERSION = "app_version"
        private const val KEY_ABOUT_DIALOG_SHOWN = "about_dialog_shown"
    }
    
    private val sharedPreferences: SharedPreferences = 
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    
    /**
     * Check if this is the first time launching the app
     */
    fun isFirstLaunch(): Boolean {
        return sharedPreferences.getBoolean(KEY_FIRST_LAUNCH, true)
    }
    
    /**
     * Mark first launch as completed
     */
    fun markFirstLaunchCompleted() {
        sharedPreferences.edit()
            .putBoolean(KEY_FIRST_LAUNCH, false)
            .apply()
    }
    
    /**
     * Check if the about dialog should be shown
     * Only show on first launch, not on version updates
     */
    fun shouldShowAboutDialog(): Boolean {
        // Only show on first launch, not afterwards
        return isFirstLaunch()
    }
    
    /**
     * Mark the about dialog as shown
     */
    fun markAboutDialogShown() {
        // Mark first launch as completed, no longer show About dialog
        markFirstLaunchCompleted()
        sharedPreferences.edit()
            .putBoolean(KEY_ABOUT_DIALOG_SHOWN, true)
            .apply()
    }
    
    /**
     * Check if the about dialog has been shown before
     */
    fun hasAboutDialogBeenShown(): Boolean {
        return sharedPreferences.getBoolean(KEY_ABOUT_DIALOG_SHOWN, false)
    }
    
    /**
     * Get current app version
     */
    private fun getCurrentAppVersion(): String {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.versionName ?: "1.2.0"
        } catch (e: Exception) {
            "1.2.0"
        }
    }
    
    /**
     * Reset first launch state (for testing)
     */
    fun resetFirstLaunch() {
        sharedPreferences.edit()
            .putBoolean(KEY_FIRST_LAUNCH, true)
            .remove(KEY_ABOUT_DIALOG_SHOWN)
            .apply()
    }
}