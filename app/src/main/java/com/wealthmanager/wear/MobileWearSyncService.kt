package com.wealthmanager.wear

import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.WearableListenerService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MobileWearSyncService : WearableListenerService() {
    @Inject
    lateinit var wearSyncManager: WearSyncManager

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        super.onDataChanged(dataEvents)
        dataEvents.use { buffer ->
            for (event in buffer) {
                if (event.type != DataEvent.TYPE_CHANGED) continue

                val dataItem = event.dataItem
                if (dataItem.uri.path != WearSyncManager.PATH_TILE_DATA) continue

                val dataMap = DataMapItem.fromDataItem(dataItem).dataMap
                val requestSync = dataMap.getBoolean(WearSyncManager.KEY_REQUEST_SYNC, false)

                if (requestSync) {
                    serviceScope.launch {
                        wearSyncManager.respondToSyncRequest()
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        serviceScope.cancel()
        super.onDestroy()
    }
}
