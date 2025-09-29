package com.wealthmanager

import android.app.Activity
import android.content.ComponentCallbacks2
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Display
import android.view.MotionEvent
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.fragment.app.FragmentActivity
import com.wealthmanager.data.FirstLaunchManager
import com.wealthmanager.data.service.PerformanceMonitor120Hz
import com.wealthmanager.ui.about.AboutDialog
import com.wealthmanager.ui.navigation.WealthManagerNavigation
import com.wealthmanager.ui.theme.WealthManagerTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity120Hz : FragmentActivity() {
    
    @Inject
    lateinit var firstLaunchManager: FirstLaunchManager
    
    @Inject
    lateinit var performanceMonitor: PerformanceMonitor120Hz
    
    private var currentRefreshRate = 60f
    private var isHighRefreshRateEnabled = false
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Remove screen refresh log
        
        enableEdgeToEdge()
        
        // Enable high refresh rate mode
        enableHighRefreshRate()
        
        // Start performance monitoring
        performanceMonitor.startMonitoring()
        
        // Configure input event handling
        setupInputEventHandling()
        
        setContent {
            var showAboutDialog by remember { mutableStateOf(false) }
            
            // Check if we should show the about dialog (only on first launch)
            LaunchedEffect(Unit) {
                if (firstLaunchManager.shouldShowAboutDialog() && !firstLaunchManager.hasAboutDialogBeenShown()) {
                    showAboutDialog = true
                }
            }
            
            WealthManagerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                        WealthManagerNavigation(
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
            
            // Show about dialog if needed
            if (showAboutDialog) {
                AboutDialog(
                    onDismiss = { showAboutDialog = false },
                    firstLaunchManager = firstLaunchManager
                )
            }
        }
    }
    
    /**
     * Enable high refresh rate mode
     */
    private fun enableHighRefreshRate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                val display = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    display
                } else {
                    @Suppress("DEPRECATION")
                    windowManager.defaultDisplay
                }
                
                display?.let { d ->
                    val refreshRates = d.supportedModes.map { it.refreshRate }
                    val maxRefreshRate = refreshRates.maxOrNull() ?: 60f
                    currentRefreshRate = maxRefreshRate
                    
                    // Remove screen refresh log
                    
                    // Set performance monitor refresh rate
                    performanceMonitor.setCurrentRefreshRate(currentRefreshRate)
                    
                    if (maxRefreshRate >= 120f) {
                        // Set high refresh rate mode
                        window.attributes = window.attributes.apply {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                preferredDisplayModeId = d.supportedModes
                                    .find { it.refreshRate >= 120f }?.modeId ?: 0
                            }
                        }
                        isHighRefreshRateEnabled = true
                        // Remove screen refresh log
                    } else {
                        // Remove screen refresh log
                    }
                }
            } catch (e: Exception) {
                // Remove screen refresh log
            }
        } else {
            // Remove screen refresh log
        }
    }
    
    /**
     * Handle touch events, optimized for 120Hz
     */
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return try {
            // 120Hz touch event handling
            if (event.action == MotionEvent.ACTION_DOWN ||
                event.action == MotionEvent.ACTION_UP ||
                event.action == MotionEvent.ACTION_MOVE) {
                
                if (isValidTouchPosition(event.x, event.y)) {
                    val result = super.onTouchEvent(event)
                    return result
                } else {
                    // Remove screen refresh log
                    return false
                }
            }
            false
        } catch (e: Exception) {
            // Remove screen refresh log
            false
        }
    }
    
    /**
     * Configure input event handling
     */
    private fun setupInputEventHandling() {
        try {
            // Simplified input event handling, not overriding Window.Callback
            // Remove screen refresh log
        } catch (e: Exception) {
            // Remove screen refresh log
        }
    }
    
    /**
     * Check if touch position is valid
     */
    private fun isValidTouchPosition(x: Float, y: Float): Boolean {
        return try {
            val window = window
            val decorView = window?.decorView
            if (decorView != null) {
                val width = decorView.width.toFloat()
                val height = decorView.height.toFloat()
                // Add boundary buffer to avoid boundary issues
                val margin = 10f
                val isValid = x >= margin && x <= (width - margin) &&
                             y >= margin && y <= (height - margin)

                if (!isValid) {
                    // Remove screen refresh log
                }
                isValid
            } else {
                // If unable to get window info, allow touch but log warning
                // Remove screen refresh log
                true
            }
        } catch (e: Exception) {
            // Remove screen refresh log
            true // Allow touch when error occurs
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        // Remove screen refresh log

        // Stop performance monitoring
        performanceMonitor.stopMonitoring()
    }
    
    /**
     * Handle low memory situation
     */
    override fun onLowMemory() {
        super.onLowMemory()
        // Remove screen refresh log

        // Trigger memory cleanup
        System.gc()

        // Log performance issues
        // val stats = performanceMonitor.getPerformanceStats()
        // Remove screen refresh log
    }
    
    /**
     * Handle memory trimming
     */
    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        // Remove screen refresh log

        when (level) {
            ComponentCallbacks2.TRIM_MEMORY_RUNNING_CRITICAL,
            ComponentCallbacks2.TRIM_MEMORY_RUNNING_LOW,
            ComponentCallbacks2.TRIM_MEMORY_RUNNING_MODERATE -> {
                // Remove screen refresh log
                System.gc()
            }
            ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN -> {
                // UI hidden - release non-essential resources
            }
            ComponentCallbacks2.TRIM_MEMORY_BACKGROUND -> {
                // App moved to background - release resources
            }
            ComponentCallbacks2.TRIM_MEMORY_MODERATE -> {
                // Moderate memory pressure
            }
            ComponentCallbacks2.TRIM_MEMORY_COMPLETE -> {
                // Complete memory pressure - release all non-essential resources
                System.gc()
            }
        }
    }
    
    /**
     * Get current refresh rate
     */
    fun getCurrentRefreshRate(): Float = currentRefreshRate
    
    /**
     * Whether high refresh rate is enabled
     */
    fun isHighRefreshRateEnabled(): Boolean = isHighRefreshRateEnabled
}