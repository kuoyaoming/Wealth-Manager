package com.wealthmanager.data

import android.content.Context
import android.content.SharedPreferences
import com.wealthmanager.BuildConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

private const val PREFS_NAME = "wealth_manager_prefs"
private const val KEY_FIRST_LAUNCH = "first_launch"
private const val KEY_APP_VERSION = "app_version"
private const val KEY_ABOUT_DIALOG_SHOWN = "about_dialog_shown"
private const val KEY_GOOGLE_PASSWORD_MANAGER_ONBOARDING_SHOWN = "google_password_manager_onboarding_shown"

/**
 * Manages first-launch and onboarding-related flags using modern Kotlin properties.
 */
@Singleton
class FirstLaunchManager @Inject constructor(@ApplicationContext private val context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    /**
     * Indicates if this is the first time the app is launched.
     * Setting it to false marks the first launch as completed.
     */
    var isFirstLaunch: Boolean
        get() = sharedPreferences.getBoolean(KEY_FIRST_LAUNCH, true)
        private set(value) = sharedPreferences.edit().putBoolean(KEY_FIRST_LAUNCH, value).apply()

    /**
     * Indicates if the 'About' dialog has been shown to the user.
     */
    var hasAboutDialogBeenShown: Boolean
        get() = sharedPreferences.getBoolean(KEY_ABOUT_DIALOG_SHOWN, false)
        private set(value) = sharedPreferences.edit().putBoolean(KEY_ABOUT_DIALOG_SHOWN, value).apply()

    /**
     * Indicates if the Google Password Manager onboarding has been shown.
     */
    var hasGooglePasswordManagerOnboardingBeenShown: Boolean
        get() = sharedPreferences.getBoolean(KEY_GOOGLE_PASSWORD_MANAGER_ONBOARDING_SHOWN, false)
        set(value) = sharedPreferences.edit().putBoolean(KEY_GOOGLE_PASSWORD_MANAGER_ONBOARDING_SHOWN, value).apply()

    /**
     * Determines if the 'About' dialog should be displayed.
     * This is typically true only on the very first launch.
     */
    fun shouldShowAboutDialog(): Boolean {
        return isFirstLaunch
    }

    /**
     * Marks the 'About' dialog as shown, which also completes the first launch sequence.
     */
    fun markAboutDialogShown() {
        if (isFirstLaunch) {
            isFirstLaunch = false
        }
        hasAboutDialogBeenShown = true
    }

    /**
     * Determines if the Google Password Manager onboarding should be shown.
     */
    fun shouldShowGooglePasswordManagerOnboarding(): Boolean {
        return isFirstLaunch && !hasGooglePasswordManagerOnboardingBeenShown
    }

    /**
     * Resets the first launch flag for debugging or testing purposes.
     */
    fun resetFirstLaunch() {
        sharedPreferences.edit()
            .putBoolean(KEY_FIRST_LAUNCH, true)
            .remove(KEY_ABOUT_DIALOG_SHOWN)
            .apply()
    }
}
