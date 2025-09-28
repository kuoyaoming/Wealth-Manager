package com.wealthmanager.ui.navigation

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.wealthmanager.ui.auth.BiometricAuthScreen
import com.wealthmanager.ui.dashboard.DashboardScreen
import com.wealthmanager.ui.assets.AssetsScreen

@Composable
fun WealthManagerNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    var isAuthenticated by remember { mutableStateOf(false) }
    
    NavHost(
        navController = navController,
        startDestination = if (isAuthenticated) "dashboard" else "auth",
        modifier = modifier
    ) {
        composable("auth") {
            BiometricAuthScreen(
                onAuthSuccess = {
                    isAuthenticated = true
                    navController.navigate("dashboard") {
                        popUpTo("auth") { inclusive = true }
                    }
                }
            )
        }
        
        composable("dashboard") {
            DashboardScreen(
                onNavigateToAssets = {
                    navController.navigate("assets")
                }
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