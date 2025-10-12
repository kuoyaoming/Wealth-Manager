package com.wealthmanager.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.wealthmanager.ui.assets.AssetsScreen
import com.wealthmanager.ui.dashboard.DashboardScreen
import com.wealthmanager.ui.settings.SettingsScreen

/**
 * Defines the navigation graph for the main part of the application,
 * accessible after the user is authenticated.
 */
@Composable
fun WealthManagerNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = "dashboard", // Authentication is handled by MainActivity now
        modifier = modifier,
    ) {
        composable("dashboard") {
            DashboardScreen(
                onNavigateToAssets = { navController.navigate("assets") },
                onNavigateToSettings = { navController.navigate("settings") },
                navController = navController,
            )
        }

        composable("assets") {
            AssetsScreen(
                onNavigateBack = { navController.popBackStack() },
            )
        }

        composable("settings") {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() },
            )
        }
    }
}
