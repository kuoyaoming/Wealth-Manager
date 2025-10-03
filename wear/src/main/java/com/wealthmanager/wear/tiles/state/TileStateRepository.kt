package com.wealthmanager.wear.tiles.state

import android.content.Context
import androidx.core.content.edit
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataItem
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TileStateRepository(private val context: Context) {

    private val preferences = context.getSharedPreferences("wealth_tile_state", Context.MODE_PRIVATE)
    private val dataClient: DataClient = Wearable.getDataClient(context)

    data class TileState(
        val totalAssets: Double,
        val formattedTotalAssets: String,
        val lastUpdated: String,
        val hasError: Boolean,
        val tileAdded: Boolean,
        val isLoading: Boolean = false
    )

    fun loadState(): TileState {
        val total = preferences.getString(KEY_TOTAL_ASSETS, "--") ?: "--"
        val lastUpdated = preferences.getString(KEY_LAST_UPDATED, "") ?: ""
        val hasError = preferences.getBoolean(KEY_HAS_ERROR, false)
        val tileAdded = preferences.getBoolean(KEY_TILE_ADDED, false)
        val isLoading = preferences.getBoolean(KEY_IS_LOADING, false)

        return TileState(
            totalAssets = total.toDoubleOrNull() ?: 0.0,
            formattedTotalAssets = if (total == "--") "--" else formatCurrency(total.toDouble()),
            lastUpdated = lastUpdated,
            hasError = hasError,
            tileAdded = tileAdded,
            isLoading = isLoading
        )
    }

    suspend fun requestSync(manual: Boolean = false) {
        withContext(Dispatchers.IO) {
            // 設置載入狀態
            preferences.edit { putBoolean(KEY_IS_LOADING, true) }
            
            val dataMapRequest = PutDataMapRequest.create(PATH_TILE_DATA)
            dataMapRequest.dataMap.putBoolean(KEY_REQUEST_SYNC, true)
            dataMapRequest.dataMap.putBoolean(KEY_MANUAL_SYNC_REQUEST, manual)
            dataMapRequest.dataMap.putLong(KEY_TIMESTAMP, System.currentTimeMillis())
            dataClient.putDataItem(dataMapRequest.asPutDataRequest().setUrgent()).await()
        }
    }

    suspend fun updateStateFromDataItem(dataItem: DataItem) {
        withContext(Dispatchers.IO) {
            try {
                val dataMap = DataMapItem.fromDataItem(dataItem).dataMap
                val totalAssets = dataMap.getDouble(KEY_TOTAL_ASSETS, 0.0)
                val lastUpdated = dataMap.getLong(KEY_LAST_UPDATED, 0L)
                val hasError = dataMap.getBoolean(KEY_HAS_ERROR, false)

                // 驗證接收到的數據
                if (!validateReceivedData(totalAssets, lastUpdated)) {
                    preferences.edit {
                        putBoolean(KEY_HAS_ERROR, true)
                        putBoolean(KEY_IS_LOADING, false)
                        putLong(KEY_CACHE_TIMESTAMP, System.currentTimeMillis())
                    }
                    return@withContext
                }

                val formattedDate = if (lastUpdated > 0) {
                    formatDate(lastUpdated)
                } else {
                    ""
                }

                // 只有當數據真正改變時才更新緩存
                val currentTotal = preferences.getString(KEY_TOTAL_ASSETS, "--") ?: "--"
                val currentLastUpdated = preferences.getString(KEY_LAST_UPDATED, "") ?: ""
                val currentHasError = preferences.getBoolean(KEY_HAS_ERROR, false)

                if (currentTotal != totalAssets.toString() || 
                    currentLastUpdated != formattedDate || 
                    currentHasError != hasError) {
                    
                    preferences.edit {
                        putString(KEY_TOTAL_ASSETS, totalAssets.toString())
                        putString(KEY_LAST_UPDATED, formattedDate)
                        putBoolean(KEY_HAS_ERROR, hasError)
                        putBoolean(KEY_TILE_ADDED, true)
                        putBoolean(KEY_IS_LOADING, false) // 清除載入狀態
                        putLong(KEY_CACHE_TIMESTAMP, System.currentTimeMillis())
                        putLong(KEY_LAST_SUCCESSFUL_SYNC, System.currentTimeMillis())
                    }
                }
            } catch (e: Exception) {
                // 處理數據解析錯誤
                preferences.edit {
                    putBoolean(KEY_HAS_ERROR, true)
                    putBoolean(KEY_IS_LOADING, false)
                    putLong(KEY_CACHE_TIMESTAMP, System.currentTimeMillis())
                }
            }
        }
    }

    fun markTileRemoved() {
        preferences.edit { putBoolean(KEY_TILE_ADDED, false) }
    }

    fun markErrorState() {
        preferences.edit {
            putBoolean(KEY_HAS_ERROR, true)
            putBoolean(KEY_IS_LOADING, false)
            putLong(KEY_CACHE_TIMESTAMP, System.currentTimeMillis())
        }
    }

    private fun formatCurrency(value: Double): String {
        val locale = context.resources.configuration.locales.get(0)
        return String.format(locale, "%,.0f", value)
    }

    private fun formatDate(timestamp: Long): String {
        val locale = context.resources.configuration.locales.get(0)
        val sdf = SimpleDateFormat("MM/dd HH:mm", locale)
        return sdf.format(Date(timestamp))
    }

    private fun validateReceivedData(totalAssets: Double, lastUpdated: Long): Boolean {
        return when {
            totalAssets.isNaN() || totalAssets.isInfinite() -> false
            totalAssets < 0 -> false
            lastUpdated <= 0 -> false
            lastUpdated > System.currentTimeMillis() + 60000 -> false // More than 1 minute in future
            else -> true
        }
    }

    companion object {
        const val PATH_TILE_DATA = "/wealth/tile"
        const val KEY_TOTAL_ASSETS = "total_assets"
        const val KEY_LAST_UPDATED = "last_updated"
        const val KEY_HAS_ERROR = "has_error"
        const val KEY_REQUEST_SYNC = "request_sync"
        const val KEY_MANUAL_SYNC_REQUEST = "manual_sync"
        const val KEY_TIMESTAMP = "timestamp"
        private const val KEY_TILE_ADDED = "tile_added"
        private const val KEY_IS_LOADING = "is_loading"
        private const val KEY_CACHE_TIMESTAMP = "cache_timestamp"
        private const val KEY_LAST_SUCCESSFUL_SYNC = "last_successful_sync"
    }
}

