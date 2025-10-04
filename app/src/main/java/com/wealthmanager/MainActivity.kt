package com.wealthmanager

import android.content.ComponentCallbacks2
import android.os.Bundle
import android.view.MotionEvent
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.*
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.FragmentActivity
import com.wealthmanager.data.FirstLaunchManager
import com.wealthmanager.data.service.PerformanceMonitor120Hz
import com.wealthmanager.ui.about.AboutDialog
import com.wealthmanager.ui.navigation.WealthManagerNavigation
import com.wealthmanager.ui.onboarding.OnboardingFlow
import com.wealthmanager.ui.responsive.LocalWindowWidthSizeClass
import com.wealthmanager.ui.theme.WealthManagerTheme
import com.wealthmanager.utils.StandardLogger
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Main activity for the Wealth Manager application.
 *
 * This activity serves as the entry point for the app and handles:
 * - Initial setup and configuration
 * - Performance monitoring
 * - Navigation and UI state management
 * - Memory management and optimization
 *
 * @property firstLaunchManager Manages first-time app launch logic
 * @property performanceMonitor Monitors app performance metrics
 */
@AndroidEntryPoint
@androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
class MainActivity : FragmentActivity() {
    @Inject
    lateinit var firstLaunchManager: FirstLaunchManager

    @Inject
    lateinit var performanceMonitor: PerformanceMonitor120Hz

    /**
     * Initializes the activity and sets up the UI.
     *
     * This method handles:
     * - Splash screen installation
     * - Edge-to-edge display configuration
     * - Performance monitoring initialization
     * - Input event handling setup
     * - Navigation and theme setup
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Install splash screen for smooth app startup
        installSplashScreen()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge()

        // Configure system bars behavior for immersive experience
        val insetsController = WindowCompat.getInsetsController(window, window.decorView)
        insetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        // Start performance monitoring
        performanceMonitor.startMonitoring()
        setupInputEventHandling()
        
        // Initialize widget system
        com.wealthmanager.widget.WidgetManager.initialize(this)

        setContent {
            var showAboutDialog by remember { mutableStateOf(false) }
            var showOnboarding by remember { mutableStateOf(false) }
            var navigateToSettings by remember { mutableStateOf(false) }

            LaunchedEffect(Unit) {
                if (firstLaunchManager.shouldShowGooglePasswordManagerOnboarding()) {
                    showOnboarding = true
                } else if (firstLaunchManager.shouldShowAboutDialog() && !firstLaunchManager.hasAboutDialogBeenShown()) {
                    showAboutDialog = true
                }
            }

            val windowSizeClass = calculateWindowSizeClass(this)
            WealthManagerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    CompositionLocalProvider(
                        LocalWindowWidthSizeClass provides windowSizeClass.widthSizeClass,
                    ) {
                        WealthManagerNavigation(
                            modifier = Modifier.fillMaxSize(),
                        )
                    }

                    if (showOnboarding) {
                        OnboardingFlow(
                            firstLaunchManager = firstLaunchManager,
                            onComplete = {
                                showOnboarding = false
                            },
                            onNavigateToSettings = {
                                showOnboarding = false
                                navigateToSettings = true
                            },
                        )
                    }

                    if (showAboutDialog) {
                        AboutDialog(
                            onDismiss = { showAboutDialog = false },
                            firstLaunchManager = firstLaunchManager,
                        )
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        hintFrameRate(0f)
    }

    override fun onPause() {
        super.onPause()
        hintFrameRate(0f)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return try {
            if (event.action == MotionEvent.ACTION_DOWN ||
                event.action == MotionEvent.ACTION_UP ||
                event.action == MotionEvent.ACTION_MOVE
            ) {
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

    /**
     * Sets up input event handling for the activity.
     *
     * This method configures touch event processing and error handling
     * for user interactions throughout the app.
     */
    private fun setupInputEventHandling() {
        try {
            StandardLogger.debug("MainActivity", "Input event handling configured")
        } catch (e: Exception) {
            StandardLogger.error("MainActivity", "Error setting up input event handling", e)
        }
    }

    /**
     * Validates if a touch position is within valid bounds.
     *
     * @param x The x-coordinate of the touch event
     * @param y The y-coordinate of the touch event
     * @return true if the touch position is valid, false otherwise
     */
    private fun isValidTouchPosition(
        x: Float,
        y: Float,
    ): Boolean {
        return try {
            val window = window
            val decorView = window?.decorView
            if (decorView != null) {
                val width = decorView.width.toFloat()
                val height = decorView.height.toFloat()
                val margin = 10f
                val isValid =
                    x >= margin && x <= (width - margin) &&
                        y >= margin && y <= (height - margin)

                if (!isValid) {
                    StandardLogger.debug(
                        "MainActivity",
                        "Touch position out of bounds: x=$x, y=$y, width=$width, height=$height",
                    )
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

    /**
     * Hints the system about the preferred frame rate for smooth rendering.
     *
     * @param frameRate The desired frame rate (0 to disable hinting)
     */
    private fun hintFrameRate(frameRate: Float) {
        try {
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
            // Note: These constants are deprecated but still functional
            // TODO: Consider using newer memory management APIs in future updates
            ComponentCallbacks2.TRIM_MEMORY_RUNNING_CRITICAL,
            ComponentCallbacks2.TRIM_MEMORY_RUNNING_LOW,
            ComponentCallbacks2.TRIM_MEMORY_RUNNING_MODERATE,
            -> {
                StandardLogger.warn("MainActivity", "Memory pressure detected - level: $level")
                System.gc()
            }
            ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN -> {
                StandardLogger.debug("MainActivity", "UI hidden - release non-essential resources")
            }
            ComponentCallbacks2.TRIM_MEMORY_BACKGROUND -> {
                StandardLogger.debug("MainActivity", "App moved to background - release resources")
            }
            // Note: These constants are deprecated but still functional
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
