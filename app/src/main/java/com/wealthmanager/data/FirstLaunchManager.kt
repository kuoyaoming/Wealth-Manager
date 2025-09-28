package com.wealthmanager.data

import android.content.Context
import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 首次開啟APP檢測管理器
 * 用於檢測是否為首次開啟APP，並管理相關狀態
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
     * 檢查是否為首次開啟APP
     */
    fun isFirstLaunch(): Boolean {
        return sharedPreferences.getBoolean(KEY_FIRST_LAUNCH, true)
    }
    
    /**
     * 標記首次開啟已完成
     */
    fun markFirstLaunchCompleted() {
        sharedPreferences.edit()
            .putBoolean(KEY_FIRST_LAUNCH, false)
            .apply()
    }
    
    /**
     * 檢查是否需要顯示關於視窗
     */
    fun shouldShowAboutDialog(): Boolean {
        val currentVersion = getCurrentAppVersion()
        val lastShownVersion = sharedPreferences.getString(KEY_APP_VERSION, "")
        
        return isFirstLaunch() || currentVersion != lastShownVersion
    }
    
    /**
     * 標記關於視窗已顯示
     */
    fun markAboutDialogShown() {
        val currentVersion = getCurrentAppVersion()
        sharedPreferences.edit()
            .putString(KEY_APP_VERSION, currentVersion)
            .putBoolean(KEY_ABOUT_DIALOG_SHOWN, true)
            .apply()
    }
    
    /**
     * 獲取當前APP版本
     */
    private fun getCurrentAppVersion(): String {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.versionName ?: "1.0.0"
        } catch (e: Exception) {
            "1.0.0"
        }
    }
    
    /**
     * 重置首次開啟狀態（用於測試）
     */
    fun resetFirstLaunch() {
        sharedPreferences.edit()
            .putBoolean(KEY_FIRST_LAUNCH, true)
            .remove(KEY_ABOUT_DIALOG_SHOWN)
            .apply()
    }
}