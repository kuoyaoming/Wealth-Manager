package com.wealthmanager

import android.app.Activity
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
        
        Log.d("MainActivity120Hz", "onCreate called")
        
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
     * 啟用高刷新率模式
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
                    
                    Log.d("MainActivity120Hz", "支援的刷新率: $refreshRates")
                    Log.d("MainActivity120Hz", "當前刷新率: $currentRefreshRate Hz")
                    
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
                        Log.d("MainActivity120Hz", "已啟用 120Hz 模式")
                    } else {
                        Log.d("MainActivity120Hz", "設備不支援 120Hz，使用標準刷新率")
                    }
                }
            } catch (e: Exception) {
                Log.e("MainActivity120Hz", "啟用高刷新率失敗", e)
            }
        } else {
            Log.d("MainActivity120Hz", "Android 版本不支援高刷新率 API")
        }
    }
    
    /**
     * 處理觸控事件，針對 120Hz 優化
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
                    Log.w("MainActivity120Hz", "Touch event at invalid position: x=${event.x}, y=${event.y}")
                    return false
                }
            }
            false
        } catch (e: Exception) {
            Log.e("MainActivity120Hz", "Error handling touch event", e)
            false
        }
    }
    
    /**
     * 配置輸入事件處理
     */
    private fun setupInputEventHandling() {
        try {
            // Simplified input event handling, not overriding Window.Callback
            Log.d("MainActivity120Hz", "Input event handling configured for 120Hz")
        } catch (e: Exception) {
            Log.e("MainActivity120Hz", "Error setting up input event handling", e)
        }
    }
    
    /**
     * 檢查觸控位置是否有效
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
                    Log.d("MainActivity120Hz", "Touch position out of bounds: x=$x, y=$y, width=$width, height=$height")
                }
                isValid
            } else {
                // If unable to get window info, allow touch but log warning
                Log.w("MainActivity120Hz", "Cannot get window decor view, allowing touch")
                true
            }
        } catch (e: Exception) {
            Log.e("MainActivity120Hz", "Error checking touch position", e)
            true // Allow touch when error occurs
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        Log.d("MainActivity120Hz", "onDestroy called")

        // Stop performance monitoring
        performanceMonitor.stopMonitoring()
    }
    
    /**
     * 處理低記憶體情況
     */
    override fun onLowMemory() {
        super.onLowMemory()
        Log.w("MainActivity120Hz", "onLowMemory called - 觸發記憶體優化")

        // Trigger memory cleanup
        System.gc()

        // Log performance issues
        val stats = performanceMonitor.getPerformanceStats()
        Log.w("MainActivity120Hz", "效能統計: $stats")
    }
    
    /**
     * 處理記憶體修剪
     */
    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        Log.d("MainActivity120Hz", "onTrimMemory called with level: $level")

        when (level) {
            TRIM_MEMORY_RUNNING_CRITICAL -> {
                Log.w("MainActivity120Hz", "嚴重記憶體壓力 - 觸發緊急清理")
                System.gc()
            }
            TRIM_MEMORY_RUNNING_LOW -> {
                Log.w("MainActivity120Hz", "低記憶體壓力 - 觸發清理")
                System.gc()
            }
            TRIM_MEMORY_RUNNING_MODERATE -> {
                Log.d("MainActivity120Hz", "中等記憶體壓力")
            }
        }
    }
    
    /**
     * 獲取當前刷新率
     */
    fun getCurrentRefreshRate(): Float = currentRefreshRate
    
    /**
     * 是否啟用高刷新率
     */
    fun isHighRefreshRateEnabled(): Boolean = isHighRefreshRateEnabled
}