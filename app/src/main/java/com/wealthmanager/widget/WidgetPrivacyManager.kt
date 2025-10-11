package com.wealthmanager.widget

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

/**
 * Manages widget privacy settings and controls what information is displayed.
 *
 * Features:
 * - Privacy toggle for asset amount display
 * - Secure storage of privacy preferences
 * - Default privacy settings
 * - Privacy-aware widget updates
 */
object WidgetPrivacyManager {
    private const val PREFS_NAME = "widget_privacy_prefs"
    private const val KEY_SHOW_ASSET_AMOUNT = "show_asset_amount"
    private const val KEY_PRIVACY_ENABLED = "privacy_enabled"

    /**
     * Check if asset amount should be displayed on widgets.
     */
    fun shouldShowAssetAmount(context: Context): Boolean {
        val prefs = getSharedPreferences(context)
        return prefs.getBoolean(KEY_SHOW_ASSET_AMOUNT, true) // Default to showing
    }

    /**
     * Set whether asset amount should be displayed on widgets.
     */
    fun setShowAssetAmount(
        context: Context,
        show: Boolean,
    ) {
        val prefs = getSharedPreferences(context)
        prefs.edit()
            .putBoolean(KEY_SHOW_ASSET_AMOUNT, show)
            .apply()

        Log.d("WealthManagerWidget", "Asset amount display set to: $show")

        // Update all widgets when privacy setting changes
        WidgetManager.updateAllWidgets(context)
    }

    /**
     * Check if privacy mode is enabled.
     */
    fun isPrivacyEnabled(context: Context): Boolean {
        val prefs = getSharedPreferences(context)
        return prefs.getBoolean(KEY_PRIVACY_ENABLED, false) // Default to disabled
    }

    /**
     * Enable or disable privacy mode.
     */
    fun setPrivacyEnabled(
        context: Context,
        enabled: Boolean,
    ) {
        val prefs = getSharedPreferences(context)
        prefs.edit()
            .putBoolean(KEY_PRIVACY_ENABLED, enabled)
            .apply()

        Log.d("WealthManagerWidget", "Privacy mode set to: $enabled")

        // Update all widgets when privacy setting changes
        WidgetManager.updateAllWidgets(context)
    }

    /**
     * Get the display text for widgets based on privacy settings.
     */
    fun getDisplayText(
        context: Context,
        assetAmount: Double,
    ): String {
        return when {
            isPrivacyEnabled(context) -> "***"
            !shouldShowAssetAmount(context) -> "Hidden"
            else -> {
                // Format the amount normally
                com.wealthmanager.utils.MoneyFormatter.format(
                    amount = java.math.BigDecimal.valueOf(assetAmount),
                    currencyCode = "TWD",
                    locale = java.util.Locale.getDefault(),
                    style = com.wealthmanager.utils.MoneyFormatter.Style.CurrencySymbol,
                    context = com.wealthmanager.utils.MoneyFormatter.MoneyContext.Total,
                )
            }
        }
    }

    /**
     * Get privacy status description for UI.
     */
    fun getPrivacyStatusDescription(context: Context): String {
        return when {
            isPrivacyEnabled(context) -> "Privacy mode enabled - Asset amounts hidden"
            !shouldShowAssetAmount(context) -> "Asset amounts hidden on widgets"
            else -> "Asset amounts visible on widgets"
        }
    }

    /**
     * Get privacy recommendations based on current settings.
     */
    fun getPrivacyRecommendations(context: Context): List<String> {
        val recommendations = mutableListOf<String>()

        if (!isPrivacyEnabled(context) && shouldShowAssetAmount(context)) {
            recommendations.add("Consider enabling privacy mode for sensitive financial data")
            recommendations.add("Widgets display your total asset value on the home screen")
            recommendations.add("Privacy mode hides asset amounts while keeping other functionality")
        }

        if (isPrivacyEnabled(context)) {
            recommendations.add("Privacy mode is active - asset amounts are hidden")
            recommendations.add("You can still tap widgets to open the app")
            recommendations.add("Disable privacy mode to show asset amounts again")
        }

        return recommendations
    }

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }
}
