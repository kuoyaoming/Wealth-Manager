package com.wealthmanager.wear.tiles

import android.content.ComponentName
import android.content.Intent
import androidx.wear.tiles.TileService
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataItem
import com.google.android.gms.wearable.WearableListenerService
import com.wealthmanager.wear.tiles.state.TileStateRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.cancel

class WealthDataListenerService : WearableListenerService() {

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private lateinit var tileStateRepository: TileStateRepository

    override fun onCreate() {
        super.onCreate()
        tileStateRepository = TileStateRepository(applicationContext)
    }

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        super.onDataChanged(dataEvents)
        dataEvents.use { events ->
            for (event in events) {
                if (event.type == DataEvent.TYPE_CHANGED) {
                    handleDataItem(event.dataItem)
                }
            }
        }
    }

    private fun handleDataItem(dataItem: DataItem) {
        if (dataItem.uri.path == TileStateRepository.PATH_TILE_DATA) {
            serviceScope.launch {
                tileStateRepository.updateStateFromDataItem(dataItem)
                // Notify tiles to update
                val intent = Intent("androidx.wear.tiles.action.REQUEST_UPDATE").apply {
                    component = ComponentName(applicationContext, WealthTileService::class.java)
                }
                applicationContext.sendBroadcast(intent)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.coroutineContext.cancel()
    }
}

