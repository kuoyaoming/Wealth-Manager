package com.wealthmanager.ui.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.wealthmanager.R
import com.wealthmanager.debug.DebugLogManager
import com.wealthmanager.haptic.HapticFeedbackManager
import com.wealthmanager.haptic.rememberHapticFeedbackWithView
import com.wealthmanager.ui.charts.TreemapChartComponent
import com.wealthmanager.ui.responsive.rememberResponsiveLayout
import com.wealthmanager.ui.sync.SyncFeedbackHandler
import com.wealthmanager.ui.sync.SyncStatusIndicator
import com.wealthmanager.ui.sync.SyncType
import com.wealthmanager.utils.PerformanceTracker

/**
 * Main dashboard screen displaying portfolio overview and asset information.
 *
 * This screen provides:
 * - Total assets summary
 * - Cash and stock asset breakdowns
 * - Interactive treemap chart
 * - Manual sync functionality with Wear OS
 * - Performance monitoring and optimization
 *
 * @param onNavigateToAssets Callback to navigate to assets management screen
 * @param onNavigateToSettings Callback to navigate to settings screen
 * @param navController Navigation controller for screen transitions
 * @param viewModel ViewModel managing dashboard state and data
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToAssets: () -> Unit,
    onNavigateToSettings: () -> Unit,
    navController: NavHostController,
    viewModel: DashboardViewModel = hiltViewModel(),
) {
    PerformanceTracker(
        componentName = "DashboardScreen",
        trackMemory = true,
        trackRecomposition = true,
    ) {
        val uiState by viewModel.uiState.collectAsState()
        val apiStatus by viewModel.apiStatus.collectAsState()
        val manualSyncStatus by viewModel.manualSyncStatus.collectAsState()
        val (hapticManager, view) = rememberHapticFeedbackWithView()
        val responsiveLayout = rememberResponsiveLayout()
        val snackbarHostState = remember { SnackbarHostState() }
        val debugLogManager = remember { DebugLogManager() }
        val wearSyncSuccessMessage = stringResource(R.string.wear_sync_success)
        val wearSyncMissingAppMessage = stringResource(R.string.wear_sync_missing_app)
        val wearSyncFailedMessage = stringResource(R.string.wear_sync_failed)

        // Auto-refresh when entering or returning to dashboard
        LaunchedEffect(Unit) {
            debugLogManager.log("DASHBOARD", "Dashboard screen launched/resumed - loading portfolio data")
            viewModel.loadPortfolioData()
        }

        LaunchedEffect(manualSyncStatus) {
            when (val status = manualSyncStatus) {
                ManualSyncStatus.Success -> snackbarHostState.showSnackbar(wearSyncSuccessMessage)
                ManualSyncStatus.WearAppMissing -> snackbarHostState.showSnackbar(wearSyncMissingAppMessage)
                is ManualSyncStatus.Failure -> snackbarHostState.showSnackbar(status.reason ?: wearSyncFailedMessage)
                else -> { /* no-op */ }
            }
            if (manualSyncStatus != null && manualSyncStatus !is ManualSyncStatus.InProgress) {
                viewModel.clearManualSyncStatus()
            }
        }

        SyncFeedbackHandler(
            syncFeedbackManager = viewModel.syncFeedbackManager,
            snackbarHostState = snackbarHostState,
            onRetry = { syncType ->
                when (syncType) {
                    SyncType.MANUAL_REFRESH -> viewModel.refreshData()
                    SyncType.WEAR_SYNC -> viewModel.manualSyncToWear()
                    SyncType.MARKET_DATA -> viewModel.refreshData()
                    else -> { /* no-op */ }
                }
            },
            onUndo = { syncType, undoData ->
                debugLogManager.log("SYNC_FEEDBACK", "Undo requested for $syncType")
            },
        )

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            topBar = {
                TopAppBar(
                    modifier = Modifier.statusBarsPadding(),
                    title = { Text(stringResource(R.string.dashboard_title)) },
                    actions = {
                        IconButton(
                            onClick = {
                                hapticManager.triggerHaptic(view, HapticFeedbackManager.HapticIntensity.MEDIUM)
                                viewModel.toggleCurrency()
                            },
                        ) {
                            Icon(Icons.Default.Wallet, contentDescription = stringResource(R.string.change_currency))
                        }
                        IconButton(
                            onClick = {
                                hapticManager.triggerHaptic(view, HapticFeedbackManager.HapticIntensity.MEDIUM)
                                viewModel.refreshData()
                            },
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = stringResource(R.string.refresh_data))
                        }
                        val isManualSyncInProgress = manualSyncStatus is ManualSyncStatus.InProgress
                        IconButton(
                            onClick = {
                                hapticManager.triggerHaptic(view, HapticFeedbackManager.HapticIntensity.LIGHT)
                                viewModel.manualSyncToWear()
                            },
                            enabled = !uiState.isLoading && !isManualSyncInProgress,
                        ) {
                            if (isManualSyncInProgress) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                            } else {
                                Icon(Icons.Default.Sync, contentDescription = stringResource(R.string.sync_wear))
                            }
                        }
                        IconButton(
                            onClick = {
                                hapticManager.triggerHaptic(view, HapticFeedbackManager.HapticIntensity.MEDIUM)
                                onNavigateToSettings()
                            },
                        ) {
                            Icon(Icons.Default.Settings, contentDescription = stringResource(R.string.cd_open_settings))
                        }
                    },
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        hapticManager.triggerHaptic(view, HapticFeedbackManager.HapticIntensity.MEDIUM)
                        onNavigateToAssets()
                    },
                ) {
                    Icon(Icons.Default.Add, contentDescription = stringResource(R.string.cd_add_asset))
                }
            },
            snackbarHost = { SnackbarHost(snackbarHostState) },
        ) { paddingValues ->
            LazyColumn(
                contentPadding = PaddingValues(responsiveLayout.paddingMedium),
                verticalArrangement = Arrangement.spacedBy(responsiveLayout.paddingMedium),
                modifier = Modifier.padding(paddingValues),
            ) {
                item {
                    SyncStatusIndicator(
                        syncFeedbackManager = viewModel.syncFeedbackManager,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                // Total Assets Card
                item {
                    TotalAssetsCardOptimized(
                        totalValue = uiState.totalAssets,
                        isLoading = uiState.isLoading,
                        currency = uiState.displayCurrency,
                        exchangeRate = uiState.exchangeRate,
                    )
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(responsiveLayout.paddingMedium),
                    ) {
                        // Cash Assets Card
                        Box(modifier = Modifier.weight(1f)) {
                            CashAssetsCardOptimized(
                                cashValue = uiState.cashAssets,
                                totalAssets = uiState.totalAssets,
                                isLoading = uiState.isLoading,
                                currency = uiState.displayCurrency,
                                exchangeRate = uiState.exchangeRate,
                            )
                        }

                        // Stock Assets Card
                        Box(modifier = Modifier.weight(1f)) {
                            StockAssetsCardOptimized(
                                stockValue = uiState.stockAssets,
                                totalAssets = uiState.totalAssets,
                                isLoading = uiState.isLoading,
                                currency = uiState.displayCurrency,
                                exchangeRate = uiState.exchangeRate,
                            )
                        }
                    }
                }

                // Treemap Chart
                if (uiState.totalAssets > 0) {
                    item {
                        TreemapChartComponent(
                            assets = uiState.assets,
                            isLoading = uiState.isLoading,
                            onAssetClick = { asset ->
                                // TODO: Navigate to asset details or show details dialog
                                // For now, just log the click
                            },
                        )
                    }
                }
            }

            // Show API error banner if needed
            if (apiStatus.hasError) {
                ApiErrorBanner(
                    errorMessage = apiStatus.errorMessage,
                    isRetrying = false,
                    isDataStale = false,
                    onRetry = {
                        hapticManager.triggerHaptic(view, HapticFeedbackManager.HapticIntensity.MEDIUM)
                        viewModel.refreshData()
                    },
                    onDismiss = {
                        hapticManager.triggerHaptic(view, HapticFeedbackManager.HapticIntensity.LIGHT)
                        // Dismiss error logic would go here
                    },
                )
            }
        }
    }
}
