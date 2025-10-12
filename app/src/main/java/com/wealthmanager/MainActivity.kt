package com.wealthmanager

import android.content.ComponentCallbacks2
import android.os.Bundle
import android.view.MotionEvent
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.fragment.app.FragmentActivity
import com.wealthmanager.ui.auth.BiometricAuthScreen
import com.wealthmanager.ui.navigation.WealthManagerNavigation
import com.wealthmanager.ui.performance.ContentBasedFrameRateOptimizer
import com.wealthmanager.ui.performance.ModernFrameRateManager
import com.wealthmanager.ui.responsive.LocalWindowWidthSizeClass
import com.wealthmanager.ui.theme.WealthManagerTheme
import com.wealthmanager.utils.StandardLogger
import com.wealthmanager.widget.WidgetManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
@androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
class MainActivity : FragmentActivity() {

    @Inject
    lateinit var frameRateManager: ModernFrameRateManager

    @Inject
    lateinit var frameRateOptimizer: ContentBasedFrameRateOptimizer

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge()

        frameRateManager.initialize(this)
        WidgetManager.initialize(this)

        setContent {
            val uiState by mainViewModel.uiState.collectAsState()
            val windowSizeClass = calculateWindowSizeClass(this)

            WealthManagerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    CompositionLocalProvider(
                        LocalWindowWidthSizeClass provides windowSizeClass.widthSizeClass,
                    ) {
                        when (uiState.isAuthenticated) {
                            true -> {
                                WealthManagerNavigation(modifier = Modifier.fillMaxSize())
                            }
                            false -> {
                                BiometricAuthScreen(
                                    onAuthSuccess = { mainViewModel.onAuthSuccess() },
                                    onSkipAuth = { mainViewModel.onAuthSkipped() }
                                )
                            }
                            null -> {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        frameRateOptimizer.optimizeForDashboard(this)
    }

    override fun onPause() {
        super.onPause()
        frameRateOptimizer.resetToDefault(this)
    }

    // Touch event and memory management overrides remain unchanged...
}
