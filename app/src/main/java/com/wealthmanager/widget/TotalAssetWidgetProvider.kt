package com.wealthmanager.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.wealthmanager.MainActivity
import com.wealthmanager.R
import com.wealthmanager.di.DatabaseEntryPoint
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * App Widget Provider for displaying total asset value.
 */
@AndroidEntryPoint
class TotalAssetWidgetProvider : AppWidgetProvider() {
    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        Log.d("WealthManagerWidget", "Total Asset Widget enabled")

        WidgetUpdateScheduler.schedulePeriodicUpdate(context)
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        Log.d("WealthManagerWidget", "Total Asset Widget disabled")

        WorkManager.getInstance(context).cancelUniqueWork("widget_periodic_update")
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        Log.d("WealthManagerWidget", "Total Asset Widget updated for ${appWidgetIds.size} widgets")

        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    companion object {
        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int,
        ) {
            val views = RemoteViews(context.packageName, R.layout.widget_total_asset_layout)

            val intent =
                Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                }
            val pendingIntent =
                PendingIntent.getActivity(
                    context,
                    0,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
                )
            views.setOnClickPendingIntent(R.id.widget_total_amount, pendingIntent)

            views.setTextViewText(R.id.widget_total_amount, "NT$ 0")
            views.setTextViewText(R.id.widget_currency_unit, "TWD")

            appWidgetManager.updateAppWidget(appWidgetId, views)

            WidgetUpdateScheduler.scheduleUpdate(context)
        }
    }
}

/**
 * WorkManager class for updating widget data.
 */
class WidgetUpdateWorker(
    context: Context,
    params: androidx.work.WorkerParameters,
) : androidx.work.CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        return try {
            Log.d("WealthManagerWidget", "Starting widget update")

            val hiltEntryPoint = EntryPointAccessors.fromApplication(applicationContext, DatabaseEntryPoint::class.java)
            val database = hiltEntryPoint.wealthManagerDatabase()

            val displayState = WidgetErrorHandler.determineDisplayState(applicationContext)
            Log.d("WealthManagerWidget", "Display state: $displayState")

            val cashAssets = runBlocking { database.cashAssetDao().getAllCashAssets().first() }
            val stockAssets = runBlocking { database.stockAssetDao().getAllStockAssets().first() }

            val totalCash = cashAssets.sumOf { it.twdEquivalent }
            val totalStock = stockAssets.sumOf { it.twdEquivalent }
            val totalAssets = totalCash + totalStock

            Log.d("WealthManagerWidget", "Total assets: $totalAssets")

            val formattedAmount = WidgetErrorHandler.getDisplayText(applicationContext, totalAssets)
            val statusMessage = WidgetErrorHandler.getStatusMessage(applicationContext)
            Log.d("WealthManagerWidget", "Status: $statusMessage")

            val appWidgetManager = AppWidgetManager.getInstance(applicationContext)
            val componentName = android.content.ComponentName(applicationContext, TotalAssetWidgetProvider::class.java)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)

            for (appWidgetId in appWidgetIds) {
                val views = RemoteViews(applicationContext.packageName, R.layout.widget_total_asset_layout)

                views.setTextViewText(R.id.widget_total_amount, formattedAmount)

                val currencyUnit =
                    when (displayState) {
                        WidgetErrorHandler.WidgetDisplayState.NO_DATA -> "Add Assets"
                        WidgetErrorHandler.WidgetDisplayState.NO_NETWORK -> "Offline"
                        WidgetErrorHandler.WidgetDisplayState.NO_API -> "Setup"
                        WidgetErrorHandler.WidgetDisplayState.ERROR -> "Error"
                        WidgetErrorHandler.WidgetDisplayState.PRIVACY_HIDDEN -> "Hidden"
                        else -> "TWD"
                    }
                views.setTextViewText(R.id.widget_currency_unit, currencyUnit)

                val lastUpdated = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
                views.setTextViewText(R.id.widget_last_updated, "Updated: $lastUpdated")
                views.setViewVisibility(R.id.widget_last_updated, android.view.View.VISIBLE)

                val intent =
                    Intent(applicationContext, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    }
                val pendingIntent =
                    PendingIntent.getActivity(
                        applicationContext,
                        0,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
                    )
                views.setOnClickPendingIntent(R.id.widget_total_amount, pendingIntent)

                appWidgetManager.updateAppWidget(appWidgetId, views)
            }

            Log.d("WealthManagerWidget", "Widget update completed successfully")
            Result.success()
        } catch (e: Exception) {
            Log.e("WealthManagerWidget", "Failed to update widget: ${e.message}", e)

            try {
                showErrorStateOnWidgets()
            } catch (errorException: Exception) {
                Log.e("WealthManagerWidget", "Failed to show error state: ${errorException.message}", errorException)
            }

            Result.failure()
        }
    }

    /**
     * Show error state on all widgets when update fails
     */
    private fun showErrorStateOnWidgets() {
        val appWidgetManager = AppWidgetManager.getInstance(applicationContext)
        val componentName = android.content.ComponentName(applicationContext, TotalAssetWidgetProvider::class.java)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)

        for (appWidgetId in appWidgetIds) {
            val views = RemoteViews(applicationContext.packageName, R.layout.widget_total_asset_layout)

            views.setTextViewText(R.id.widget_total_amount, "Error")
            views.setTextViewText(R.id.widget_currency_unit, "Tap to retry")

            val intent =
                Intent(applicationContext, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                }
            val pendingIntent =
                PendingIntent.getActivity(
                    applicationContext,
                    0,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
                )
            views.setOnClickPendingIntent(R.id.widget_total_amount, pendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}

/**
 * Widget update scheduler.
 */
object WidgetUpdateScheduler {
    fun scheduleUpdate(context: Context) {
        val workRequest =
            OneTimeWorkRequestBuilder<WidgetUpdateWorker>()
                .setInitialDelay(java.time.Duration.ofMinutes(1))
                .build()

        WorkManager.getInstance(context)
            .enqueueUniqueWork(
                "widget_update",
                ExistingWorkPolicy.REPLACE,
                workRequest,
            )
    }

    fun schedulePeriodicUpdate(context: Context) {
        val workRequest =
            androidx.work.PeriodicWorkRequestBuilder<WidgetUpdateWorker>(
                java.time.Duration.ofMinutes(30),
            ).build()

        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork(
                "widget_periodic_update",
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest,
            )
    }
}
