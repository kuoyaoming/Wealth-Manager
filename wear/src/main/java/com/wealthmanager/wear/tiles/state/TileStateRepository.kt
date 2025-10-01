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
        val tileAdded: Boolean
    )

    fun loadState(): TileState {
        val total = preferences.getString(KEY_TOTAL_ASSETS, "--") ?: "--"
        val lastUpdated = preferences.getString(KEY_LAST_UPDATED, "") ?: ""
        val hasError = preferences.getBoolean(KEY_HAS_ERROR, false)
        val tileAdded = preferences.getBoolean(KEY_TILE_ADDED, false)

        return TileState(
            totalAssets = total.toDoubleOrNull() ?: 0.0,
            formattedTotalAssets = if (total == "--") "--" else formatCurrency(total.toDouble()),
            lastUpdated = lastUpdated,
            hasError = hasError,
            tileAdded = tileAdded
        )
    }

    suspend fun requestSync(manual: Boolean = false) {
        withContext(Dispatchers.IO) {
            val dataMapRequest = PutDataMapRequest.create(PATH_TILE_DATA)
            dataMapRequest.dataMap.putBoolean(KEY_REQUEST_SYNC, true)
            dataMapRequest.dataMap.putBoolean(KEY_MANUAL_SYNC_REQUEST, manual)
            dataMapRequest.dataMap.putLong(KEY_TIMESTAMP, System.currentTimeMillis())
            dataClient.putDataItem(dataMapRequest.asPutDataRequest().setUrgent()).await()
        }
    }

    suspend fun updateStateFromDataItem(dataItem: DataItem) {
        withContext(Dispatchers.IO) {
            val dataMap = DataMapItem.fromDataItem(dataItem).dataMap
            val totalAssets = dataMap.getDouble(KEY_TOTAL_ASSETS, 0.0)
            val lastUpdated = dataMap.getLong(KEY_LAST_UPDATED, 0L)
            val hasError = dataMap.getBoolean(KEY_HAS_ERROR, false)

            val formattedDate = if (lastUpdated > 0) {
                formatDate(lastUpdated)
            } else {
                ""
            }

            preferences.edit {
                putString(KEY_TOTAL_ASSETS, totalAssets.toString())
                putString(KEY_LAST_UPDATED, formattedDate)
                putBoolean(KEY_HAS_ERROR, hasError)
                putBoolean(KEY_TILE_ADDED, true)
            }
        }
    }

    fun markTileRemoved() {
        preferences.edit { putBoolean(KEY_TILE_ADDED, false) }
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

    companion object {
        const val PATH_TILE_DATA = "/wealth/tile"
        const val KEY_TOTAL_ASSETS = "total_assets"
        const val KEY_LAST_UPDATED = "last_updated"
        const val KEY_HAS_ERROR = "has_error"
        const val KEY_REQUEST_SYNC = "request_sync"
        const val KEY_MANUAL_SYNC_REQUEST = "manual_sync"
        const val KEY_TIMESTAMP = "timestamp"
        private const val KEY_TILE_ADDED = "tile_added"
    }
}

