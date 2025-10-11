package com.wealthmanager.widget

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.wealthmanager.data.entity.CashAsset
import com.wealthmanager.data.entity.StockAsset
import com.wealthmanager.di.DatabaseEntryPoint
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.flow.first

/**
 * Handles error scenarios and edge cases for widget functionality.
 *
 * Scenarios handled:
 * - No network connection
 * - No assets data
 * - No API keys configured
 * - Database errors
 * - Widget update failures
 * - Privacy settings conflicts
 */
object WidgetErrorHandler {
    /**
     * Widget error states
     */
    enum class WidgetErrorState {
        NO_ERROR,
        NO_NETWORK,
        NO_ASSETS,
        NO_API_KEYS,
        DATABASE_ERROR,
        UPDATE_FAILED,
        PRIVACY_CONFLICT,
    }

    /**
     * Widget display states
     */
    enum class WidgetDisplayState {
        NORMAL, // Shows asset amount normally
        NO_DATA, // No assets found
        NO_NETWORK, // Network unavailable
        NO_API, // API keys not configured
        ERROR, // General error
        PRIVACY_HIDDEN, // Privacy mode enabled
    }

    /**
     * Check network connectivity
     */
    fun isNetworkAvailable(context: Context): Boolean {
        return try {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val network = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
        } catch (e: Exception) {
            Log.e("WealthManagerWidget", "Network check failed: ${e.message}", e)
            false
        }
    }

    /**
     * Check if user has any assets
     */
    suspend fun hasAssets(context: Context): Boolean {
        return try {
            val cashAssets = getCashAssetsDirectly(context)
            val stockAssets = getStockAssetsDirectly(context)

            val hasCash = cashAssets.isNotEmpty() && cashAssets.any { it.amount > 0 }
            val hasStock = stockAssets.isNotEmpty() && stockAssets.any { it.shares > 0 }

            hasCash || hasStock
        } catch (e: Exception) {
            Log.e("WealthManagerWidget", "Asset check failed: ${e.message}", e)
            false
        }
    }

    /**
     * Check if API keys are configured.
     * NOTE: This is a simplified check. A full implementation requires Hilt injection
     * in a context that supports it (e.g., a WorkManager worker).
     */
    fun hasApiKeys(context: Context): Boolean {
        // For now, always return false to avoid compilation issues.
        // In a real implementation, this would check the actual API keys.
        return false
    }

    /**
     * Determine widget display state based on current conditions
     */
    suspend fun determineDisplayState(context: Context): WidgetDisplayState {
        return try {
            // Check privacy settings first
            if (WidgetPrivacyManager.isPrivacyEnabled(context)) {
                return WidgetDisplayState.PRIVACY_HIDDEN
            }

            if (!WidgetPrivacyManager.shouldShowAssetAmount(context)) {
                return WidgetDisplayState.PRIVACY_HIDDEN
            }

            // Check for assets
            if (!hasAssets(context)) {
                return WidgetDisplayState.NO_DATA
            }

            // Check network for real-time data
            if (!isNetworkAvailable(context)) {
                return WidgetDisplayState.NO_NETWORK
            }

            // Check API keys for market data
            if (!hasApiKeys(context)) {
                return WidgetDisplayState.NO_API
            }

            WidgetDisplayState.NORMAL
        } catch (e: Exception) {
            Log.e("WealthManagerWidget", "Display state determination failed: ${e.message}", e)
            WidgetDisplayState.ERROR
        }
    }

    /**
     * Get appropriate display text based on widget state
     */
    suspend fun getDisplayText(
        context: Context,
        assetAmount: Double,
    ): String {
        val displayState = determineDisplayState(context)

        return when (displayState) {
            WidgetDisplayState.NORMAL -> {
                WidgetPrivacyManager.getDisplayText(context, assetAmount)
            }
            WidgetDisplayState.NO_DATA -> {
                "No Assets"
            }
            WidgetDisplayState.NO_NETWORK -> {
                "Offline"
            }
            WidgetDisplayState.NO_API -> {
                "Setup Required"
            }
            WidgetDisplayState.ERROR -> {
                "Error"
            }
            WidgetDisplayState.PRIVACY_HIDDEN -> {
                WidgetPrivacyManager.getDisplayText(context, assetAmount)
            }
        }
    }

    /**
     * Get status message for debugging
     */
    suspend fun getStatusMessage(context: Context): String {
        val displayState = determineDisplayState(context)
        val hasAssets = hasAssets(context)
        val hasNetwork = isNetworkAvailable(context)
        val hasApiKeys = hasApiKeys(context)
        val privacyEnabled = WidgetPrivacyManager.isPrivacyEnabled(context)
        val showAmount = WidgetPrivacyManager.shouldShowAssetAmount(context)

        return buildString {
            append("Widget Status: $displayState\n")
            append("Has Assets: $hasAssets\n")
            append("Has Network: $hasNetwork\n")
            append("Has API Keys: $hasApiKeys\n")
            append("Privacy Enabled: $privacyEnabled\n")
            append("Show Amount: $showAmount")
        }
    }

    /**
     * Get user-friendly error message
     */
    fun getErrorMessage(
        context: Context,
        errorState: WidgetErrorState,
    ): String {
        return when (errorState) {
            WidgetErrorState.NO_ERROR -> ""
            WidgetErrorState.NO_NETWORK -> "No internet connection"
            WidgetErrorState.NO_ASSETS -> "No assets found. Add some assets to see your total."
            WidgetErrorState.NO_API_KEYS -> "API keys not configured. Set up API keys in Settings for real-time data."
            WidgetErrorState.DATABASE_ERROR -> "Database error. Please restart the app."
            WidgetErrorState.UPDATE_FAILED -> "Widget update failed. Try refreshing."
            WidgetErrorState.PRIVACY_CONFLICT -> "Privacy settings conflict. Check widget privacy settings."
        }
    }

    /**
     * Get recommendations based on current state
     */
    suspend fun getRecommendations(context: Context): List<String> {
        val recommendations = mutableListOf<String>()
        val displayState = determineDisplayState(context)

        when (displayState) {
            WidgetDisplayState.NO_DATA -> {
                recommendations.add("Add cash or stock assets to see your total")
                recommendations.add("Tap the widget to open the app and add assets")
            }
            WidgetDisplayState.NO_NETWORK -> {
                recommendations.add("Connect to internet for real-time data")
                recommendations.add("Widget will show cached data when offline")
            }
            WidgetDisplayState.NO_API -> {
                recommendations.add("Configure API keys in Settings for market data")
                recommendations.add("Without API keys, only manual asset values are shown")
            }
            WidgetDisplayState.ERROR -> {
                recommendations.add("Restart the app to resolve errors")
                recommendations.add("Check if the app has necessary permissions")
            }
            WidgetDisplayState.PRIVACY_HIDDEN -> {
                recommendations.add("Privacy mode is enabled - asset amounts are hidden")
                recommendations.add("Disable privacy mode in Settings to show amounts")
            }
            WidgetDisplayState.NORMAL -> {
                recommendations.add("Widget is working normally")
                recommendations.add("Data updates every 30 minutes automatically")
            }
        }

        return recommendations
    }

    /**
     * Get cash assets directly from DAO (avoiding AssetRepository dependency injection)
     */
    private suspend fun getCashAssetsDirectly(context: Context): List<CashAsset> {
        val hiltEntryPoint = EntryPointAccessors.fromApplication(context, DatabaseEntryPoint::class.java)
        val database = hiltEntryPoint.wealthManagerDatabase()
        return database.cashAssetDao().getAllCashAssets().first()
    }

    /**
     * Get stock assets directly from DAO (avoiding AssetRepository dependency injection)
     */
    private suspend fun getStockAssetsDirectly(context: Context): List<StockAsset> {
        val hiltEntryPoint = EntryPointAccessors.fromApplication(context, DatabaseEntryPoint::class.java)
        val database = hiltEntryPoint.wealthManagerDatabase()
        return database.stockAssetDao().getAllStockAssets().first()
    }
}
