package com.wealthmanager

import android.content.ComponentCallbacks2
import android.os.Bundle
import android.os.Build
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
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.wealthmanager.data.FirstLaunchManager
import com.wealthmanager.data.service.PerformanceMonitor120Hz
import com.wealthmanager.ui.about.AboutDialog
import com.wealthmanager.ui.navigation.WealthManagerNavigation
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.CompositionLocalProvider
import com.wealthmanager.ui.responsive.LocalWindowWidthSizeClass
import com.wealthmanager.ui.theme.WealthManagerTheme
import com.wealthmanager.utils.StandardLogger
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
@androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
class MainActivity : FragmentActivity() {
    
    @Inject
    lateinit var firstLaunchManager: FirstLaunchManager
    
    @Inject
    lateinit var performanceMonitor: PerformanceMonitor120Hz
    
    
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Install Splash Screen API for smooth app launch
        installSplashScreen()
        
        // Complete Edge-to-Edge implementation
        WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge()
        
        // Configure system bars behavior for immersive experience
        val insetsController = WindowCompat.getInsetsController(window, window.decorView)
        insetsController.systemBarsBehavior = 
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        
        performanceMonitor.startMonitoring()
        setupInputEventHandling()
        
        setContent {
            var showAboutDialog by remember { mutableStateOf(false) }
            
            LaunchedEffect(Unit) {
                if (firstLaunchManager.shouldShowAboutDialog() && !firstLaunchManager.hasAboutDialogBeenShown()) {
                    showAboutDialog = true
                }
            }
            
            val windowSizeClass = calculateWindowSizeClass(this)
            WealthManagerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CompositionLocalProvider(
                        LocalWindowWidthSizeClass provides windowSizeClass.widthSizeClass
                    ) {
                        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                            WealthManagerNavigation(
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                    }

                    if (showAboutDialog) {
                        AboutDialog(
                            onDismiss = { showAboutDialog = false },
                            firstLaunchManager = firstLaunchManager
                        )
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Hint system to auto-manage refresh rate by default
        hintFrameRate(0f)
    }

    override fun onPause() {
        super.onPause()
        // Return control to system when leaving the screen
        hintFrameRate(0f)
    }
    
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return try {
            if (event.action == MotionEvent.ACTION_DOWN || 
                event.action == MotionEvent.ACTION_UP || 
                event.action == MotionEvent.ACTION_MOVE) {
                
                if (isValidTouchPosition(event.x, event.y)) {
                    val result = super.onTouchEvent(event)
                    if (!result) {
                        StandardLogger.debug("MainActivity", "Touch event not handled by super: ${event.action}")
                    }
                    return result
                } else {
                    StandardLogger.warn("MainActivity", "Touch event at invalid position: x=${event.x}, y=${event.y}")
                    return false
                }
            }
            false
        } catch (e: Exception) {
            StandardLogger.error("MainActivity", "Error handling touch event", e)
            false
        }
    }
    
    private fun setupInputEventHandling() {
        try {
            StandardLogger.debug("MainActivity", "Input event handling configured")
        } catch (e: Exception) {
            StandardLogger.error("MainActivity", "Error setting up input event handling", e)
        }
    }
    
    private fun isValidTouchPosition(x: Float, y: Float): Boolean {
        return try {
            val window = window
            val decorView = window?.decorView
            if (decorView != null) {
                val width = decorView.width.toFloat()
                val height = decorView.height.toFloat()
                val margin = 10f
                val isValid = x >= margin && x <= (width - margin) && 
                             y >= margin && y <= (height - margin)
                
                if (!isValid) {
                    StandardLogger.debug("MainActivity", "Touch position out of bounds: x=$x, y=$y, width=$width, height=$height")
                }
                isValid
            } else {
                StandardLogger.warn("MainActivity", "Cannot get window decor view, allowing touch")
                true
            }
        } catch (e: Exception) {
            StandardLogger.error("MainActivity", "Error checking touch position", e)
            true
        }
    }

    private fun hintFrameRate(frameRate: Float) {
        try {
            // Provide a non-binding refresh rate preference to the system
            // On API 30+ the system supports dynamic refresh rate; using LayoutParams hint keeps control with the system
            val lp = window.attributes
            lp.preferredRefreshRate = if (frameRate > 0f) frameRate else 0f
            window.attributes = lp
        } catch (e: Exception) {
            StandardLogger.warn("MainActivity", "Failed to hint frame rate: $frameRate", e)
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        StandardLogger.debug("MainActivity", "onDestroy called")
        
        performanceMonitor.stopMonitoring()
    }
    
    override fun onLowMemory() {
        super.onLowMemory()
        StandardLogger.warn("MainActivity", "onLowMemory called - trigger memory optimization")
        System.gc()
        performanceMonitor.getPerformanceStats()
    }
    
    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        StandardLogger.debug("MainActivity", "onTrimMemory called with level: $level")
        
        when (level) {
            ComponentCallbacks2.TRIM_MEMORY_RUNNING_CRITICAL,
            ComponentCallbacks2.TRIM_MEMORY_RUNNING_LOW,
            ComponentCallbacks2.TRIM_MEMORY_RUNNING_MODERATE -> {
                StandardLogger.warn("MainActivity", "Memory pressure detected - level: $level")
                // Trigger garbage collection for memory cleanup
                System.gc()
            }
            ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN -> {
                StandardLogger.debug("MainActivity", "UI hidden - release non-essential resources")
            }
            ComponentCallbacks2.TRIM_MEMORY_BACKGROUND -> {
                StandardLogger.debug("MainActivity", "App moved to background - release resources")
            }
            ComponentCallbacks2.TRIM_MEMORY_MODERATE -> {
                StandardLogger.debug("MainActivity", "Moderate memory pressure")
            }
            ComponentCallbacks2.TRIM_MEMORY_COMPLETE -> {
                StandardLogger.warn("MainActivity", "Complete memory pressure - release all non-essential resources")
                System.gc()
            }
        }
    }
}