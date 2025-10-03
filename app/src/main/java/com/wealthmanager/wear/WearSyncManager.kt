package com.wealthmanager.wear

import android.content.Context
import androidx.core.content.edit
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import com.wealthmanager.data.repository.AssetRepository
import com.wealthmanager.debug.DebugLogManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.abs
import kotlin.math.max

@Singleton
class WearSyncManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val assetRepository: AssetRepository,
    private val debugLogManager: DebugLogManager,
) {
    private val dataClient by lazy { Wearable.getDataClient(context) }
    private val capabilityClient by lazy { Wearable.getCapabilityClient(context) }
    private val preferences = context.getSharedPreferences("wear_sync_state", Context.MODE_PRIVATE)

    private var lastSyncedTotal: Double? = null
    private var lastSyncedTimestamp: Long = 0L
    private var lastSyncResultWasError: Boolean = false
    private var lastSyncAttemptTime: Long = 0L
    private val MIN_SYNC_INTERVAL_MS = 30 * 1000L // 30 seconds debounce interval
    private val MAX_RETRY_ATTEMPTS = 3
    private val RETRY_DELAY_BASE_MS = 1000L // Base delay for exponential backoff

    suspend fun syncTotalsFromDashboard(
        totalAssets: Double,
        lastUpdated: Long,
        hasError: Boolean,
    ) {
        debugLogManager.log(
            "WEAR_SYNC",
            "syncTotalsFromDashboard called - total: $totalAssets, lastUpdated: $lastUpdated, hasError: $hasError",
        )
        
        // 檢查基本條件
        if (!validateSyncData(totalAssets, lastUpdated)) {
            debugLogManager.log("WEAR_SYNC", "Sync skipped - invalid data")
            return
        }
        
        if (!shouldSync(totalAssets, lastUpdated, hasError)) {
            debugLogManager.log("WEAR_SYNC", "Sync skipped - shouldSync returned false")
            return
        }
        
        // 檢查連接狀態
        if (!ensureWearableConnection()) {
            debugLogManager.log("WEAR_SYNC", "Sync skipped - no wearable connection")
            return
        }
        
        debugLogManager.log("WEAR_SYNC", "Proceeding with sync")
        pushTotalsWithRetry(totalAssets, lastUpdated, hasError)
    }

    suspend fun respondToSyncRequest() {
        withContext(Dispatchers.IO) {
            try {
                // 檢查連接狀態
                if (!ensureWearableConnection()) {
                    debugLogManager.log("WEAR_SYNC", "Sync request ignored - no wearable connection")
                    return@withContext
                }
                
                val cashAssets = assetRepository.getAllCashAssets().first()
                val stockAssets = assetRepository.getAllStockAssets().first()
                val totalCash = cashAssets.sumOf { it.twdEquivalent }
                val totalStock = stockAssets.sumOf { it.twdEquivalent }
                val totalAssets = totalCash + totalStock
                val lastUpdatedCash = cashAssets.maxOfOrNull { it.lastUpdated } ?: 0L
                val lastUpdatedStock = stockAssets.maxOfOrNull { it.lastUpdated } ?: 0L
                val lastUpdated = max(lastUpdatedCash, lastUpdatedStock)
                
                val finalLastUpdated = if (lastUpdated == 0L) System.currentTimeMillis() else lastUpdated
                
                if (validateSyncData(totalAssets, finalLastUpdated)) {
                    pushTotalsWithRetry(totalAssets, finalLastUpdated, hasError = false)
                } else {
                    debugLogManager.log("WEAR_SYNC", "Sync request ignored - invalid calculated data")
                }
            } catch (exception: Exception) {
                debugLogManager.logError(
                    "WEAR_SYNC: Failed to respond to sync request: ${exception.message}",
                    exception,
                )
                val fallbackTotal = lastSyncedTotal ?: 0.0
                val fallbackTimestamp = System.currentTimeMillis()
                if (validateSyncData(fallbackTotal, fallbackTimestamp)) {
                    pushTotalsWithRetry(fallbackTotal, fallbackTimestamp, hasError = true)
                }
            }
        }
    }

    suspend fun manualSync(
        totalAssets: Double,
        lastUpdated: Long,
        hasError: Boolean = false,
    ): ManualSyncResult {
        debugLogManager.log("WEAR_SYNC", "manualSync requested - total: $totalAssets, lastUpdated: $lastUpdated")
        return withContext(Dispatchers.IO) {
            try {
                // 檢查數據有效性
                if (!validateSyncData(totalAssets, lastUpdated)) {
                    debugLogManager.log("WEAR_SYNC", "Manual sync failed - invalid data")
                    return@withContext ManualSyncResult.Failure("Invalid sync data")
                }
                
                // 檢查連接狀態
                if (!ensureWearableConnection()) {
                    debugLogManager.log("WEAR_SYNC", "Manual sync failed - no wearable connection")
                    return@withContext ManualSyncResult.WearAppNotInstalled
                }
                
                val success = pushTotalsWithRetry(totalAssets, lastUpdated, hasError)
                if (success) {
                    ManualSyncResult.Success
                } else {
                    ManualSyncResult.Failure("Sync operation failed")
                }
            } catch (exception: Exception) {
                debugLogManager.logError("WEAR_SYNC: manual sync failed: ${exception.message}", exception)
                ManualSyncResult.Failure(exception.message)
            }
        }
    }

    private suspend fun pushTotalsWithRetry(
        totalAssets: Double,
        lastUpdated: Long,
        hasError: Boolean,
    ): Boolean {
        debugLogManager.log(
            "WEAR_SYNC",
            "pushTotalsWithRetry called - total: $totalAssets, lastUpdated: $lastUpdated, hasError: $hasError",
        )
        
        repeat(MAX_RETRY_ATTEMPTS) { attempt ->
            try {
                val success = pushTotals(totalAssets, lastUpdated, hasError)
                if (success) {
                    saveSyncState(totalAssets, lastUpdated, hasError)
                    return true
                }
            } catch (exception: Exception) {
                debugLogManager.logError("WEAR_SYNC: Attempt ${attempt + 1} failed: ${exception.message}", exception)
            }
            
            if (attempt < MAX_RETRY_ATTEMPTS - 1) {
                val delayMs = RETRY_DELAY_BASE_MS * (1L shl attempt) // Exponential backoff
                debugLogManager.log("WEAR_SYNC", "Retrying in ${delayMs}ms...")
                delay(delayMs)
            }
        }
        
        debugLogManager.log("WEAR_SYNC", "All retry attempts failed")
        return false
    }

    private suspend fun pushTotals(
        totalAssets: Double,
        lastUpdated: Long,
        hasError: Boolean,
    ): Boolean {
        debugLogManager.log(
            "WEAR_SYNC",
            "pushTotals called - total: $totalAssets, lastUpdated: $lastUpdated, hasError: $hasError",
        )
        return withContext(Dispatchers.IO) {
            try {
                debugLogManager.log("WEAR_SYNC", "Creating data map request")
                val request =
                    PutDataMapRequest.create(PATH_TILE_DATA).apply {
                        dataMap.putDouble(KEY_TOTAL_ASSETS, totalAssets)
                        dataMap.putLong(KEY_LAST_UPDATED, lastUpdated)
                        dataMap.putBoolean(KEY_HAS_ERROR, hasError)
                        dataMap.putBoolean(KEY_REQUEST_SYNC, false)
                        dataMap.putLong(KEY_TIMESTAMP, System.currentTimeMillis())
                    }.asPutDataRequest().setUrgent()

                debugLogManager.log("WEAR_SYNC", "Sending data to wear device")
                dataClient.putDataItem(request).await()
                lastSyncedTotal = totalAssets
                lastSyncedTimestamp = lastUpdated
                lastSyncResultWasError = hasError
                debugLogManager.log(
                    "WEAR_SYNC",
                    "Wear data synced successfully: total=$totalAssets, lastUpdated=$lastUpdated, error=$hasError",
                )
                true
            } catch (exception: Exception) {
                debugLogManager.logError("WEAR_SYNC: Failed to sync wear data: ${exception.message}", exception)
                false
            }
        }
    }

    private fun shouldSync(
        totalAssets: Double,
        lastUpdated: Long,
        hasError: Boolean,
    ): Boolean {
        val currentTime = System.currentTimeMillis()

        // Debounce check: skip if too soon since last sync attempt
        if (currentTime - lastSyncAttemptTime < MIN_SYNC_INTERVAL_MS) {
            debugLogManager.log("WEAR_SYNC", "Sync throttled - too soon since last attempt")
            return false
        }

        lastSyncAttemptTime = currentTime

        if (hasError) {
            // Always propagate error state
            return !lastSyncResultWasError
        }

        val previousTotal = lastSyncedTotal ?: return true

        if (abs(previousTotal - totalAssets) >= VALUE_DELTA_THRESHOLD) {
            return true
        }

        if (lastUpdated - lastSyncedTimestamp >= TIME_DELTA_THRESHOLD_MS) {
            return true
        }

        if (lastSyncResultWasError) {
            return true
        }

        return false
    }

    private suspend fun ensureWearableConnection(): Boolean {
        return try {
            val capabilityInfo = capabilityClient
                .getCapability(CAPABILITY_WEAR_APP, CapabilityClient.FILTER_REACHABLE)
                .await()
            val hasConnection = capabilityInfo.nodes.isNotEmpty()
            debugLogManager.log("WEAR_SYNC", "Wearable connection check: $hasConnection")
            hasConnection
        } catch (e: Exception) {
            debugLogManager.logError("WEAR_SYNC: Connection check failed", e)
            false
        }
    }

    private fun validateSyncData(totalAssets: Double, lastUpdated: Long): Boolean {
        val isValid = when {
            totalAssets.isNaN() || totalAssets.isInfinite() -> {
                debugLogManager.log("WEAR_SYNC", "Invalid totalAssets: $totalAssets")
                false
            }
            totalAssets < 0 -> {
                debugLogManager.log("WEAR_SYNC", "Negative totalAssets: $totalAssets")
                false
            }
            lastUpdated <= 0 -> {
                debugLogManager.log("WEAR_SYNC", "Invalid lastUpdated: $lastUpdated")
                false
            }
            lastUpdated > System.currentTimeMillis() + 60000 -> { // More than 1 minute in future
                debugLogManager.log("WEAR_SYNC", "Future timestamp: $lastUpdated")
                false
            }
            else -> true
        }
        
        debugLogManager.log("WEAR_SYNC", "Data validation result: $isValid")
        return isValid
    }

    private fun saveSyncState(totalAssets: Double, lastUpdated: Long, hasError: Boolean) {
        preferences.edit {
            putString("last_sync_total", totalAssets.toString())
            putLong("last_sync_timestamp", lastUpdated)
            putBoolean("last_sync_error", hasError)
            putLong("last_sync_time", System.currentTimeMillis())
            putInt("sync_success_count", getSyncSuccessCount() + 1)
        }
    }

    private fun getSyncSuccessCount(): Int {
        return preferences.getInt("sync_success_count", 0)
    }

    // TODO: Implement network availability check for future sync optimizations
    // private fun isNetworkAvailable(): Boolean { ... }
    
    // TODO: Implement battery optimization check for future sync optimizations  
    // private fun isBatteryOptimizationDisabled(): Boolean { ... }

    sealed class ManualSyncResult {
        object Success : ManualSyncResult()

        object WearAppNotInstalled : ManualSyncResult()

        data class Failure(val reason: String? = null) : ManualSyncResult()
    }

    companion object {
        const val PATH_TILE_DATA = "/wealth/tile"
        const val KEY_TOTAL_ASSETS = "total_assets"
        const val KEY_LAST_UPDATED = "last_updated"
        const val KEY_HAS_ERROR = "has_error"
        const val KEY_REQUEST_SYNC = "request_sync"
        const val KEY_TIMESTAMP = "timestamp"
        private const val CAPABILITY_WEAR_APP = "wealth_manager_wear_app"

        private const val VALUE_DELTA_THRESHOLD = 100.0 // Increased threshold to reduce sync frequency
        private const val TIME_DELTA_THRESHOLD_MS = 10 * 60 * 1000L // 10 minutes, reduce sync frequency
    }
}