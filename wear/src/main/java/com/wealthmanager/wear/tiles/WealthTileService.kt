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
                .addIdToImageMapping(
                    "ic_launcher_foreground",
                    ResourceBuilders.ImageResource.Builder()
                        .setAndroidResourceByResId(
                            ResourceBuilders.AndroidImageResourceByResId.Builder()
                                .setResourceId(R.drawable.ic_launcher_foreground)
                                .build()
                        )
                        .build()
                )
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

        // 使用 PrimaryLayout 符合 Wear OS 設計指南
        val primaryLayoutBuilder = PrimaryLayout.Builder()

        // 設置主要標籤（標題）
        primaryLayoutBuilder.setPrimaryLabelContent(
            LayoutElementBuilders.Text.Builder()
                .setText(title)
                .setFontStyle(
                    LayoutElementBuilders.FontStyle.Builder()
                        .setSize(DimensionBuilders.SpProp.Builder().setValue(14f).build())
                        .build()
                )
                .build()
        )

        // 設置次要標籤（總資產或載入狀態）
        if (state.isLoading) {
            primaryLayoutBuilder.setSecondaryLabelContent(
                LayoutElementBuilders.Text.Builder()
                    .setText(context.getString(R.string.tile_loading))
                    .setFontStyle(
                        LayoutElementBuilders.FontStyle.Builder()
                            .setSize(DimensionBuilders.SpProp.Builder().setValue(12f).build())
                            .build()
                    )
                    .build()
            )
        } else {
            primaryLayoutBuilder.setSecondaryLabelContent(
                LayoutElementBuilders.Text.Builder()
                    .setText(totalAssets)
                    .setFontStyle(
                        LayoutElementBuilders.FontStyle.Builder()
                            .setSize(DimensionBuilders.SpProp.Builder().setValue(12f).build())
                            .build()
                    )
                    .build()
            )
        }

        // 如果有更新時間，設置內容區域
        if (lastUpdated.isNotEmpty()) {
            primaryLayoutBuilder.setContent(
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

        // 如果有錯誤，顯示錯誤信息和重試按鈕
        if (state.hasError) {
            primaryLayoutBuilder.setContent(
                LayoutElementBuilders.Column.Builder()
                    .addContent(
                        LayoutElementBuilders.Text.Builder()
                            .setText(context.getString(R.string.tile_error))
                            .setFontStyle(
                                LayoutElementBuilders.FontStyle.Builder()
                                    .setSize(DimensionBuilders.SpProp.Builder().setValue(10f).build())
                                    .build()
                            )
                            .build()
                    )
                    .addContent(
                        LayoutElementBuilders.Text.Builder()
                            .setText(context.getString(R.string.tile_tap_to_open))
                            .setFontStyle(
                                LayoutElementBuilders.FontStyle.Builder()
                                    .setSize(DimensionBuilders.SpProp.Builder().setValue(8f).build())
                                    .build()
                            )
                            .build()
                    )
                    .build()
            )
        }

        // 添加點擊動作：打開主應用
        primaryLayoutBuilder.setClickable(
            ActionBuilders.Clickable.Builder()
                .setOnClick(
                    ActionBuilders.LaunchAction.Builder()
                        .setAndroidActivity(
                            ActionBuilders.AndroidActivity.Builder()
                                .setClassName("com.wealthmanager.wear.ui.MainWearActivity")
                                .setPackageName("com.wealthmanager.wear")
                                .build()
                        )
                        .build()
                )
                .build()
        )

        return LayoutElementBuilders.Layout.Builder()
            .setRoot(primaryLayoutBuilder.build())
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