package com.wealthmanager.widget

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import com.wealthmanager.debug.DebugLogManager

/**
 * Manager class for handling widget operations and integration with the main app.
 * 
 * This class provides:
 * - Widget initialization and setup
 * - Data synchronization between app and widget
 * - Widget update scheduling
 * - Error handling and recovery
 */
object WidgetManager {
    
    /**
     * Initialize widget system and schedule updates.
     */
    fun initialize(context: Context) {
        DebugLogManager.log("WIDGET_MANAGER", "Initializing widget system")
        
        // Schedule periodic updates
        WidgetUpdateScheduler.schedulePeriodicUpdate(context)
        
        // Trigger immediate update if widgets are already installed
        updateAllWidgets(context)
        
        DebugLogManager.log("WIDGET_MANAGER", "Widget system initialized")
    }
    
    /**
     * Update all installed widget instances.
     */
    fun updateAllWidgets(context: Context) {
        try {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val componentName = ComponentName(context, TotalAssetWidgetProvider::class.java)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)
            
            if (appWidgetIds.isNotEmpty()) {
                DebugLogManager.log("WIDGET_MANAGER", "Updating ${appWidgetIds.size} widget instances")
                
                // Trigger widget update
                val intent = android.content.Intent(context, TotalAssetWidgetProvider::class.java).apply {
                    action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                    putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds)
                }
                context.sendBroadcast(intent)
                
                // Schedule data update
                WidgetUpdateScheduler.scheduleUpdate(context)
            } else {
                DebugLogManager.log("WIDGET_MANAGER", "No widget instances found")
            }
        } catch (e: Exception) {
            DebugLogManager.logError("WIDGET_MANAGER: Failed to update widgets: ${e.message}", e)
        }
    }
    
    /**
     * Check if any widgets are currently installed.
     */
    fun hasInstalledWidgets(context: Context): Boolean {
        return try {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val componentName = ComponentName(context, TotalAssetWidgetProvider::class.java)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)
            appWidgetIds.isNotEmpty()
        } catch (e: Exception) {
            DebugLogManager.logError("WIDGET_MANAGER: Failed to check widget status: ${e.message}", e)
            false
        }
    }
    
    /**
     * Get the number of installed widget instances.
     */
    fun getInstalledWidgetCount(context: Context): Int {
        return try {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val componentName = ComponentName(context, TotalAssetWidgetProvider::class.java)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)
            appWidgetIds.size
        } catch (e: Exception) {
            DebugLogManager.logError("WIDGET_MANAGER: Failed to get widget count: ${e.message}", e)
            0
        }
    }
    
    /**
     * Clean up widget resources when app is being destroyed.
     */
    fun cleanup(context: Context) {
        DebugLogManager.log("WIDGET_MANAGER", "Cleaning up widget resources")
        
        // Cancel all widget-related work
        val workManager = androidx.work.WorkManager.getInstance(context)
        workManager.cancelUniqueWork("widget_update")
        workManager.cancelUniqueWork("widget_periodic_update")
        
        DebugLogManager.log("WIDGET_MANAGER", "Widget cleanup completed")
    }
}