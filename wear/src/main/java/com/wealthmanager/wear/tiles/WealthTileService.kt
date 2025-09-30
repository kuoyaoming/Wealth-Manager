package com.wealthmanager.wear.tiles

import android.content.ComponentName
import android.content.Context
import androidx.wear.tiles.ActionBuilders
import androidx.wear.tiles.DimensionBuilders
import androidx.wear.tiles.EventBuilders
import androidx.wear.tiles.LayoutElementBuilders
import androidx.wear.tiles.RequestBuilders
import androidx.wear.tiles.ResourceBuilders
import androidx.wear.tiles.TileBuilders
import androidx.wear.tiles.TileService
import androidx.wear.tiles.TimelineBuilders
import androidx.wear.tiles.material.layouts.PrimaryLayout
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import com.wealthmanager.wear.R
import com.wealthmanager.wear.tiles.state.TileStateRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.concurrent.Executors

class WealthTileService : TileService() {

    private val serviceScope = CoroutineScope(Dispatchers.Main + Job())
    private val tileStateRepository by lazy { TileStateRepository(this) }
    private val executor = Executors.newSingleThreadExecutor()

    override fun onTileRequest(requestParams: RequestBuilders.TileRequest): ListenableFuture<TileBuilders.Tile> {
        return Futures.immediateFuture(
            TileBuilders.Tile.Builder()
                .setResourcesVersion(RESOURCES_VERSION)
                .setTimeline(
                    TimelineBuilders.Timeline.Builder()
                        .addTimelineEntry(
                            TimelineBuilders.TimelineEntry.Builder()
                                .setLayout(createTileLayout(this, tileStateRepository.loadState()))
                                .build()
                        )
                        .build()
                )
                .build()
        )
    }

    override fun onResourcesRequest(requestParams: RequestBuilders.ResourcesRequest): ListenableFuture<ResourceBuilders.Resources> {
        return Futures.immediateFuture(
            ResourceBuilders.Resources.Builder()
                .setVersion(RESOURCES_VERSION)
                .build()
        )
    }

    override fun onTileAddEvent(requestParams: EventBuilders.TileAddEvent) {
        super.onTileAddEvent(requestParams)
        triggerSync()
    }

    override fun onTileRemoveEvent(requestParams: EventBuilders.TileRemoveEvent) {
        super.onTileRemoveEvent(requestParams)
        tileStateRepository.markTileRemoved()
    }

    private fun createTileLayout(context: Context, state: TileStateRepository.TileState): LayoutElementBuilders.Layout {
        val title = context.getString(R.string.tile_title)
        val lastUpdatedLabel = context.getString(R.string.tile_last_updated)

        val totalAssets = state.formattedTotalAssets
        val lastUpdated = state.lastUpdated

        val column = LayoutElementBuilders.Column.Builder()
            .addContent(
                LayoutElementBuilders.Text.Builder()
                    .setText(title)
                    .build()
            )
            .addContent(
                LayoutElementBuilders.Text.Builder()
                    .setText(totalAssets)
                    .setFontStyle(
                        LayoutElementBuilders.FontStyle.Builder()
                            .setSize(DimensionBuilders.SpProp.Builder().setValue(18f).build())
                            .build()
                    )
                    .build()
            )

        if (lastUpdated.isNotEmpty()) {
            column.addContent(
                LayoutElementBuilders.Text.Builder()
                    .setText(context.getString(R.string.tile_last_updated_format, lastUpdatedLabel, lastUpdated))
                    .setFontStyle(
                        LayoutElementBuilders.FontStyle.Builder()
                            .setSize(DimensionBuilders.SpProp.Builder().setValue(10f).build())
                            .build()
                    )
                    .build()
            )
        }

        val body = LayoutElementBuilders.Column.Builder()
            .setHorizontalAlignment(LayoutElementBuilders.HORIZONTAL_ALIGN_CENTER)
            .addContent(column.build())

        if (state.hasError) {
            body.addContent(
                LayoutElementBuilders.Text.Builder()
                    .setText(context.getString(R.string.tile_error))
                    .setFontStyle(
                        LayoutElementBuilders.FontStyle.Builder()
                            .setSize(DimensionBuilders.SpProp.Builder().setValue(10f).build())
                            .build()
                    )
                    .build()
            )
        }

        return LayoutElementBuilders.Layout.Builder()
            .setRoot(body.build())
            .build()
    }

    private fun triggerSync() {
        val context = this
        serviceScope.launch {
            tileStateRepository.requestSync()
            // Use broadcast to notify tiles
            val intent = android.content.Intent("androidx.wear.tiles.action.REQUEST_UPDATE").apply {
                component = android.content.ComponentName(context, WealthTileService::class.java)
            }
            context.sendBroadcast(intent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        executor.shutdown()
    }

    companion object {
        private const val RESOURCES_VERSION = "1"
    }
}