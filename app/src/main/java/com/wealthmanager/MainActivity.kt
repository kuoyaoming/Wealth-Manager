package com.wealthmanager

import android.os.Bundle
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
import com.wealthmanager.ui.about.AboutDialog
import com.wealthmanager.ui.navigation.WealthManagerNavigation
import com.wealthmanager.ui.theme.WealthManagerTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : FragmentActivity() {
    
    @Inject
    lateinit var firstLaunchManager: FirstLaunchManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
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
}