package com.wealthmanager.data

import android.content.Context
import android.content.SharedPreferences
import com.wealthmanager.BuildConfig
import javax.inject.Inject
import javax.inject.Singleton

/**
 * First Launch Detection Manager
 * Used to detect if this is the first time opening the app and manage related states
 */
@Singleton
class FirstLaunchManager
    @Inject
    constructor(
        private val context: Context,
    ) {
        companion object {
            private const val PREFS_NAME = "wealth_manager_prefs"
            private const val KEY_FIRST_LAUNCH = "first_launch"
            private const val KEY_APP_VERSION = "app_version"
            private const val KEY_ABOUT_DIALOG_SHOWN = "about_dialog_shown"
        }

        private val sharedPreferences: SharedPreferences =
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        fun isFirstLaunch(): Boolean {
            return sharedPreferences.getBoolean(KEY_FIRST_LAUNCH, true)
        }

        fun markFirstLaunchCompleted() {
            sharedPreferences.edit()
                .putBoolean(KEY_FIRST_LAUNCH, false)
                .apply()
        }

        fun shouldShowAboutDialog(): Boolean {
            return isFirstLaunch()
        }

        fun markAboutDialogShown() {
            markFirstLaunchCompleted()
            sharedPreferences.edit()
                .putBoolean(KEY_ABOUT_DIALOG_SHOWN, true)
                .apply()
        }

        fun hasAboutDialogBeenShown(): Boolean {
            return sharedPreferences.getBoolean(KEY_ABOUT_DIALOG_SHOWN, false)
        }

        private fun getCurrentAppVersion(): String {
            return try {
                val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
                packageInfo.versionName ?: BuildConfig.VERSION_NAME
            } catch (e: Exception) {
                BuildConfig.VERSION_NAME
            }
        }

        fun resetFirstLaunch() {
            sharedPreferences.edit()
                .putBoolean(KEY_FIRST_LAUNCH, true)
                .remove(KEY_ABOUT_DIALOG_SHOWN)
                .apply()
        }
    }
