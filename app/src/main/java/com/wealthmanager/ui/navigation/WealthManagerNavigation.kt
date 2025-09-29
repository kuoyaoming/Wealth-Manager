package com.wealthmanager.ui.navigation

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.wealthmanager.auth.AuthStateManager
import com.wealthmanager.ui.auth.BiometricAuthScreen
import com.wealthmanager.ui.dashboard.DashboardScreen
import com.wealthmanager.ui.assets.AssetsScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.platform.LocalContext

@Composable
fun WealthManagerNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val context = LocalContext.current
    val authStateManager = remember { AuthStateManager(context) }
    var isAuthenticated by remember { mutableStateOf(authStateManager.isAuthenticated()) }
    
    // Check authentication status on app start
    LaunchedEffect(Unit) {
        if (authStateManager.isAuthenticated()) {
            navController.navigate("dashboard") {
                popUpTo("auth") { inclusive = true }
            }
        }
    }
    
    NavHost(
        navController = navController,
        startDestination = if (authStateManager.isAuthenticated()) "dashboard" else "auth",
        modifier = modifier
    ) {
        composable("auth") {
            BiometricAuthScreen(
                onAuthSuccess = {
                    authStateManager.setAuthenticated(true)
                    isAuthenticated = true
                    navController.navigate("dashboard") {
                        popUpTo("auth") { inclusive = true }
                    }
                },
                onSkipAuth = {
                    // Don't allow skipping if already authenticated
                    if (!authStateManager.isAuthenticated()) {
                        authStateManager.setAuthenticated(false) // Mark as not authenticated
                        isAuthenticated = false
                        navController.navigate("dashboard") {
                            popUpTo("auth") { inclusive = true }
                        }
                    }
                }
            )
        }
        
        composable("dashboard") {
            DashboardScreen(
                onNavigateToAssets = {
                    navController.navigate("assets")
                },
                navController = navController
            )
        }
        
        composable("assets") {
            AssetsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}