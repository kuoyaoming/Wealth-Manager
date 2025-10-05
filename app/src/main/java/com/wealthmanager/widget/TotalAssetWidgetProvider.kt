package com.wealthmanager.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import androidx.core.content.ContextCompat
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.PeriodicWorkRequestBuilder
import com.wealthmanager.MainActivity
import com.wealthmanager.R
import com.wealthmanager.debug.DebugLogManager
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Traditional App Widget Provider for displaying total asset value.
 * 
 * Features:
 * - Shows total asset value in TWD
 * - Adapts to system theme (light/dark)
 * - Clickable to open main app
 * - Auto-updates every 30 minutes
 * - Responsive design with rounded corners
 */
@AndroidEntryPoint
class TotalAssetWidgetProvider : AppWidgetProvider() {
    
    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        DebugLogManager.log("WIDGET", "Total Asset Widget enabled")
        
        // Schedule periodic updates
        WidgetUpdateScheduler.schedulePeriodicUpdate(context)
    }
    
    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        DebugLogManager.log("WIDGET", "Total Asset Widget disabled")
        
        // Cancel periodic updates
        WorkManager.getInstance(context).cancelUniqueWork("widget_periodic_update")
    }
    
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        DebugLogManager.log("WIDGET", "Total Asset Widget updated for ${appWidgetIds.size} widgets")
        
        // Update all widget instances
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }
    
    companion object {
        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val views = RemoteViews(context.packageName, R.layout.widget_total_asset_layout)
            
            // Set click intent to open main app
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            val pendingIntent = PendingIntent.getActivity(
                context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_total_amount, pendingIntent)
            
            // Set initial values (will be updated by WorkManager)
            views.setTextViewText(R.id.widget_total_amount, "NT$ 0")
            views.setTextViewText(R.id.widget_currency_unit, "TWD")
            
            // Update widget
            appWidgetManager.updateAppWidget(appWidgetId, views)
            
            // Trigger data update
            WidgetUpdateScheduler.scheduleUpdate(context)
        }
    }
}

/**
 * WorkManager class for updating widget data.
 */
class WidgetUpdateWorker(
    context: Context,
    params: androidx.work.WorkerParameters
) : androidx.work.CoroutineWorker(context, params) {
    
    override suspend fun doWork(): Result {
        return try {
            DebugLogManager.log("WIDGET_WORKER", "Starting widget update")
            
            // Check error conditions first
            val displayState = WidgetErrorHandler.determineDisplayState(applicationContext)
            DebugLogManager.log("WIDGET_WORKER", "Display state: $displayState")
            
            // Get total assets from repository
            val assetRepository = com.wealthmanager.data.repository.AssetRepository(
                com.wealthmanager.data.database.WealthManagerDatabase.getDatabase(applicationContext).cashAssetDao(),
                com.wealthmanager.data.database.WealthManagerDatabase.getDatabase(applicationContext).stockAssetDao(),
                com.wealthmanager.data.database.WealthManagerDatabase.getDatabase(applicationContext).exchangeRateDao(),
                com.wealthmanager.debug.DebugLogManager
            )
            val cashAssets = assetRepository.getAllCashAssetsSync()
            val stockAssets = assetRepository.getAllStockAssetsSync()
            
            val totalCash = cashAssets.sumOf { it.twdEquivalent }
            val totalStock = stockAssets.sumOf { it.twdEquivalent }
            val totalAssets = totalCash + totalStock
            
            DebugLogManager.log("WIDGET_WORKER", "Total assets: $totalAssets")
            
            // Get appropriate display text based on current state
            val formattedAmount = WidgetErrorHandler.getDisplayText(applicationContext, totalAssets)
            val statusMessage = WidgetErrorHandler.getStatusMessage(applicationContext)
            DebugLogManager.log("WIDGET_WORKER", "Status: $statusMessage")
            
            // Update all widget instances
            val appWidgetManager = AppWidgetManager.getInstance(applicationContext)
            val componentName = android.content.ComponentName(applicationContext, TotalAssetWidgetProvider::class.java)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)
            
            for (appWidgetId in appWidgetIds) {
                val views = RemoteViews(applicationContext.packageName, R.layout.widget_total_asset_layout)
                
                // Update text based on state
                views.setTextViewText(R.id.widget_total_amount, formattedAmount)
                
                // Set currency unit based on state
                val currencyUnit = when (displayState) {
                    WidgetErrorHandler.WidgetDisplayState.NO_DATA -> "Add Assets"
                    WidgetErrorHandler.WidgetDisplayState.NO_NETWORK -> "Offline"
                    WidgetErrorHandler.WidgetDisplayState.NO_API -> "Setup"
                    WidgetErrorHandler.WidgetDisplayState.ERROR -> "Error"
                    WidgetErrorHandler.WidgetDisplayState.PRIVACY_HIDDEN -> "Hidden"
                    else -> "TWD"
                }
                views.setTextViewText(R.id.widget_currency_unit, currencyUnit)
                
                // Set last updated time
                val lastUpdated = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
                views.setTextViewText(R.id.widget_last_updated, "Updated: $lastUpdated")
                views.setViewVisibility(R.id.widget_last_updated, android.view.View.VISIBLE)
                
                // Set click intent
                val intent = Intent(applicationContext, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                }
                val pendingIntent = PendingIntent.getActivity(
                    applicationContext, 0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                views.setOnClickPendingIntent(R.id.widget_total_amount, pendingIntent)
                
                // Update widget
                appWidgetManager.updateAppWidget(appWidgetId, views)
            }
            
            DebugLogManager.log("WIDGET_WORKER", "Widget update completed successfully")
            Result.success()
        } catch (e: Exception) {
            DebugLogManager.logError("WIDGET_WORKER: Failed to update widget: ${e.message}", e)
            
            // Try to show error state on widgets
            try {
                showErrorStateOnWidgets()
            } catch (errorException: Exception) {
                DebugLogManager.logError("WIDGET_WORKER: Failed to show error state: ${errorException.message}", errorException)
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
            
            // Set click intent to open app
            val intent = Intent(applicationContext, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            val pendingIntent = PendingIntent.getActivity(
                applicationContext, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
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
        val workRequest = OneTimeWorkRequestBuilder<WidgetUpdateWorker>()
            .setInitialDelay(java.time.Duration.ofMinutes(1))
            .build()
        
        WorkManager.getInstance(context)
            .enqueueUniqueWork(
                "widget_update",
                ExistingWorkPolicy.REPLACE,
                workRequest
            )
    }
    
    fun schedulePeriodicUpdate(context: Context) {
        val workRequest = androidx.work.PeriodicWorkRequestBuilder<WidgetUpdateWorker>(
            java.time.Duration.ofMinutes(30)
        ).build()
        
        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork(
                "widget_periodic_update",
                ExistingWorkPolicy.KEEP,
                workRequest
            )
    }
}