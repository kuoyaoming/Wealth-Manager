package com.wealthmanager

import android.os.Bundle
import android.util.Log
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
import com.wealthmanager.data.FirstLaunchManager
import com.wealthmanager.ui.about.AboutDialog
import com.wealthmanager.ui.navigation.WealthManagerNavigation
import com.wealthmanager.ui.theme.WealthManagerTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Optimized MainActivity with improved lifecycle management
 */
@AndroidEntryPoint
class MainActivityOptimized : FragmentActivity() {
    
    @Inject
    lateinit var firstLaunchManager: FirstLaunchManager
    
    private var isAppInBackground = false
    private var lastActiveTime = System.currentTimeMillis()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        Log.d("MainActivity", "onCreate called")
        
        // Install splash screen
        // installSplashScreen()
        
        enableEdgeToEdge()
        
        setContent {
            var showAboutDialog by remember { mutableStateOf(false) }
            
            // Check if we should show the about dialog
            LaunchedEffect(Unit) {
                if (firstLaunchManager.shouldShowAboutDialog()) {
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
    
    override fun onStart() {
        super.onStart()
        Log.d("MainActivity", "onStart called")
        isAppInBackground = false
        lastActiveTime = System.currentTimeMillis()
    }
    
    override fun onResume() {
        super.onResume()
        Log.d("MainActivity", "onResume called")
        isAppInBackground = false
        lastActiveTime = System.currentTimeMillis()
    }
    
    override fun onPause() {
        super.onPause()
        Log.d("MainActivity", "onPause called")
        isAppInBackground = true
    }
    
    override fun onStop() {
        super.onStop()
        Log.d("MainActivity", "onStop called")
        isAppInBackground = true
    }
    
    override fun onDestroy() {
        super.onDestroy()
        Log.d("MainActivity", "onDestroy called")
    }
    
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.d("MainActivity", "onSaveInstanceState called")
        
        // Save important state
        outState.putLong("lastActiveTime", lastActiveTime)
        outState.putBoolean("isAppInBackground", isAppInBackground)
    }
    
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        Log.d("MainActivity", "onRestoreInstanceState called")
        
        // Restore state
        lastActiveTime = savedInstanceState.getLong("lastActiveTime", System.currentTimeMillis())
        isAppInBackground = savedInstanceState.getBoolean("isAppInBackground", false)
    }
    
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        Log.d("MainActivity", "onWindowFocusChanged: $hasFocus")
        
        if (hasFocus) {
            isAppInBackground = false
            lastActiveTime = System.currentTimeMillis()
            // Trigger data refresh if needed
            refreshDataIfNeeded()
        } else {
            isAppInBackground = true
        }
    }
    
    /**
     * Refresh data if the app has been in background for too long
     */
    private fun refreshDataIfNeeded() {
        val timeSinceLastActive = System.currentTimeMillis() - lastActiveTime
        val refreshThreshold = 5 * 60 * 1000L // 5 minutes
        
        if (timeSinceLastActive > refreshThreshold) {
            Log.d("MainActivity", "Refreshing data after background time: ${timeSinceLastActive}ms")
            // Trigger data refresh
            // This would be handled by the ViewModel
        }
    }
    
    /**
     * Handle low memory situations
     */
    override fun onLowMemory() {
        super.onLowMemory()
        Log.w("MainActivity", "onLowMemory called - clearing caches")
        
        // Clear caches and free up memory
        System.gc()
    }
    
    /**
     * Handle configuration changes
     */
    override fun onConfigurationChanged(newConfig: android.content.res.Configuration) {
        super.onConfigurationChanged(newConfig)
        Log.d("MainActivity", "onConfigurationChanged called")
        
        // Handle configuration changes gracefully
        // This helps prevent app crashes during orientation changes
    }
}
