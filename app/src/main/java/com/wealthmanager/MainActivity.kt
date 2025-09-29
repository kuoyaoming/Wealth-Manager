package com.wealthmanager

import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
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
import androidx.fragment.app.FragmentActivity
// import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.wealthmanager.data.FirstLaunchManager
import com.wealthmanager.data.service.PerformanceMonitor120Hz
import com.wealthmanager.ui.about.AboutDialog
import com.wealthmanager.ui.navigation.WealthManagerNavigation
import com.wealthmanager.ui.theme.WealthManagerTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : FragmentActivity() {
    
    @Inject
    lateinit var firstLaunchManager: FirstLaunchManager
    
    @Inject
    lateinit var performanceMonitor: PerformanceMonitor120Hz
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Install splash screen
        // installSplashScreen()
        
        enableEdgeToEdge()
        
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
     * 處理觸控事件，修復 Pointer Icon 錯誤
     */
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return try {
            // Check if event is valid
            if (event.action == MotionEvent.ACTION_DOWN || 
                event.action == MotionEvent.ACTION_UP || 
                event.action == MotionEvent.ACTION_MOVE) {
                
                // Check if touch position is within valid range
                if (isValidTouchPosition(event.x, event.y)) {
                    // Ensure event is within valid range before processing
                    val result = super.onTouchEvent(event)
                    if (!result) {
                        Log.d("MainActivity", "Touch event not handled by super: ${event.action}")
                    }
                    return result
                } else {
                    // If position is invalid, log warning but don't process event
                    Log.w("MainActivity", "Touch event at invalid position: x=${event.x}, y=${event.y}")
                    return false
                }
            }
            false
        } catch (e: Exception) {
            Log.e("MainActivity", "Error handling touch event", e)
            false
        }
    }
    
    /**
     * 配置輸入事件處理
     */
    private fun setupInputEventHandling() {
        try {
            // Simplified input event handling, not overriding Window.Callback
            Log.d("MainActivity", "Input event handling configured")
        } catch (e: Exception) {
            Log.e("MainActivity", "Error setting up input event handling", e)
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
                    Log.d("MainActivity", "Touch position out of bounds: x=$x, y=$y, width=$width, height=$height")
                }
                isValid
            } else {
                // If unable to get window info, allow touch but log warning
                Log.w("MainActivity", "Cannot get window decor view, allowing touch")
                true
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "Error checking touch position", e)
            true // Allow touch when error occurs
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        Log.d("MainActivity", "onDestroy called")
        
        // Stop performance monitoring
        performanceMonitor.stopMonitoring()
    }
    
    /**
     * 處理低記憶體情況
     */
    override fun onLowMemory() {
        super.onLowMemory()
        Log.w("MainActivity", "onLowMemory called - 觸發記憶體優化")
        
        // Trigger memory cleanup
        System.gc()
        
        // Log performance issues
        performanceMonitor.getPerformanceStats()
    }
    
    /**
     * 處理記憶體修剪
     */
    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        Log.d("MainActivity", "onTrimMemory called with level: $level")
        
        when (level) {
            TRIM_MEMORY_RUNNING_CRITICAL -> {
                Log.w("MainActivity", "嚴重記憶體壓力 - 觸發緊急清理")
                System.gc()
            }
            TRIM_MEMORY_RUNNING_LOW -> {
                Log.w("MainActivity", "低記憶體壓力 - 觸發清理")
                System.gc()
            }
            TRIM_MEMORY_RUNNING_MODERATE -> {
                Log.d("MainActivity", "中等記憶體壓力")
            }
        }
    }
}