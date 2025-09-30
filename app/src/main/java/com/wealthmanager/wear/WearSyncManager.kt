package com.wealthmanager.wear

import android.content.Context
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import com.google.android.gms.wearable.CapabilityClient
import com.wealthmanager.data.repository.AssetRepository
import com.wealthmanager.debug.DebugLogManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
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
    private val debugLogManager: DebugLogManager
) {

    private val dataClient by lazy { Wearable.getDataClient(context) }
    private val capabilityClient by lazy { Wearable.getCapabilityClient(context) }

    private var lastSyncedTotal: Double? = null
    private var lastSyncedTimestamp: Long = 0L
    private var lastSyncResultWasError: Boolean = false

    suspend fun syncTotalsFromDashboard(totalAssets: Double, lastUpdated: Long, hasError: Boolean) {
        debugLogManager.log("WEAR_SYNC", "syncTotalsFromDashboard called - total: $totalAssets, lastUpdated: $lastUpdated, hasError: $hasError")
        if (!shouldSync(totalAssets, lastUpdated, hasError)) {
            debugLogManager.log("WEAR_SYNC", "Sync skipped - shouldSync returned false")
            return
        }
        debugLogManager.log("WEAR_SYNC", "Proceeding with sync")
        pushTotals(totalAssets, lastUpdated, hasError)
    }

    suspend fun respondToSyncRequest() {
        withContext(Dispatchers.IO) {
            try {
                val cashAssets = assetRepository.getAllCashAssets().first()
                val stockAssets = assetRepository.getAllStockAssets().first()
                val totalCash = cashAssets.sumOf { it.twdEquivalent }
                val totalStock = stockAssets.sumOf { it.twdEquivalent }
                val totalAssets = totalCash + totalStock
                val lastUpdatedCash = cashAssets.maxOfOrNull { it.lastUpdated } ?: 0L
                val lastUpdatedStock = stockAssets.maxOfOrNull { it.lastUpdated } ?: 0L
                val lastUpdated = max(lastUpdatedCash, lastUpdatedStock)
                pushTotals(totalAssets, if (lastUpdated == 0L) System.currentTimeMillis() else lastUpdated, hasError = false)
            } catch (exception: Exception) {
                debugLogManager.logError("WEAR_SYNC: Failed to respond to sync request: ${exception.message}", exception)
                pushTotals(lastSyncedTotal ?: 0.0, System.currentTimeMillis(), hasError = true)
            }
        }
    }

    suspend fun manualSync(totalAssets: Double, lastUpdated: Long, hasError: Boolean = false): ManualSyncResult {
        debugLogManager.log("WEAR_SYNC", "manualSync requested - total: $totalAssets, lastUpdated: $lastUpdated")
        return withContext(Dispatchers.IO) {
            try {
                val capabilityInfo = capabilityClient
                    .getCapability(CAPABILITY_WEAR_APP, CapabilityClient.FILTER_REACHABLE)
                    .await()
                if (capabilityInfo.nodes.isEmpty()) {
                    debugLogManager.log("WEAR_SYNC", "No reachable wear nodes with app capability")
                    ManualSyncResult.WearAppNotInstalled
                } else {
                    val success = pushTotals(totalAssets, lastUpdated, hasError)
                    if (success) ManualSyncResult.Success else ManualSyncResult.Failure()
                }
            } catch (exception: Exception) {
                debugLogManager.logError("WEAR_SYNC: manual sync failed: ${exception.message}", exception)
                ManualSyncResult.Failure(exception.message)
            }
        }
    }

    private suspend fun pushTotals(totalAssets: Double, lastUpdated: Long, hasError: Boolean): Boolean {
        debugLogManager.log("WEAR_SYNC", "pushTotals called - total: $totalAssets, lastUpdated: $lastUpdated, hasError: $hasError")
        return withContext(Dispatchers.IO) {
            try {
                debugLogManager.log("WEAR_SYNC", "Creating data map request")
                val request = PutDataMapRequest.create(PATH_TILE_DATA).apply {
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
                debugLogManager.log("WEAR_SYNC", "Wear data synced successfully: total=$totalAssets, lastUpdated=$lastUpdated, error=$hasError")
                true
            } catch (exception: Exception) {
                debugLogManager.logError("WEAR_SYNC: Failed to sync wear data: ${exception.message}", exception)
                false
            }
        }
    }

    private fun shouldSync(totalAssets: Double, lastUpdated: Long, hasError: Boolean): Boolean {
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

        private const val VALUE_DELTA_THRESHOLD = 10.0
        private const val TIME_DELTA_THRESHOLD_MS = 5 * 60 * 1000L // 5 minutes
    }
}

