package com.wealthmanager.ui.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.wealthmanager.R
import com.wealthmanager.haptic.HapticFeedbackManager
import com.wealthmanager.haptic.rememberHapticFeedbackWithView
import com.wealthmanager.ui.responsive.rememberResponsiveLayout
import com.wealthmanager.ui.dashboard.TotalAssetsCardOptimized
import com.wealthmanager.ui.dashboard.CashAssetsCardOptimized
import com.wealthmanager.ui.dashboard.StockAssetsCardOptimized
import com.wealthmanager.ui.charts.PieChartComponentFixed
import com.wealthmanager.ui.dashboard.ApiErrorBanner
import com.wealthmanager.ui.dashboard.ManualSyncStatus
import kotlinx.coroutines.flow.collectLatest
import androidx.compose.ui.draw.alpha

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToAssets: () -> Unit,
    onNavigateToSettings: () -> Unit,
    navController: NavHostController,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val apiStatus by viewModel.apiStatus.collectAsState()
    val manualSyncStatus by viewModel.manualSyncStatus.collectAsState()
    val (hapticManager, view) = rememberHapticFeedbackWithView()
    val responsiveLayout = rememberResponsiveLayout()
    val snackbarHostState = remember { SnackbarHostState() }
    val wearSyncSuccessMessage = stringResource(R.string.wear_sync_success)
    var showMissingKeysDialog = androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }
    val wearSyncMissingAppMessage = stringResource(R.string.wear_sync_missing_app)
    val wearSyncFailedMessage = stringResource(R.string.wear_sync_failed)

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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.dashboard_title)) },
                actions = {
                    IconButton(
                        onClick = {
                            hapticManager.triggerHaptic(view, HapticFeedbackManager.HapticIntensity.MEDIUM)
                            if (viewModel.hasRequiredKeys()) {
                                viewModel.refreshData()
                            } else {
                                showMissingKeysDialog.value = true
                            }
                        }
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = stringResource(R.string.refresh_data))
                    }
                    val isManualSyncInProgress = manualSyncStatus is ManualSyncStatus.InProgress
                    IconButton(
                        onClick = {
                            hapticManager.triggerHaptic(view, HapticFeedbackManager.HapticIntensity.LIGHT)
                            viewModel.manualSyncToWear()
                        },
                        enabled = !uiState.isLoading && !isManualSyncInProgress
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
                        }
                    ) {
                        Icon(Icons.Default.Settings, contentDescription = stringResource(R.string.cd_open_settings))
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    hapticManager.triggerHaptic(view, HapticFeedbackManager.HapticIntensity.MEDIUM)
                    onNavigateToAssets()
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.cd_add_asset))
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
			val columns = responsiveLayout.columns
			LazyVerticalGrid(
				columns = GridCells.Fixed(columns),
            contentPadding = PaddingValues(responsiveLayout.paddingMedium),
            horizontalArrangement = Arrangement.spacedBy(responsiveLayout.paddingMedium),
            verticalArrangement = Arrangement.spacedBy(responsiveLayout.paddingMedium),
            modifier = Modifier.padding(paddingValues)
        ) {
            // Total Assets Card
				item(span = { GridItemSpan(columns) }) {
                TotalAssetsCardOptimized(
                    totalValue = uiState.totalAssets,
                    isLoading = uiState.isLoading
                )
            }
            
            // Cash Assets Card
            item {
                CashAssetsCardOptimized(
                    cashValue = uiState.cashAssets,
                    isLoading = uiState.isLoading
                )
            }
            
            // Stock Assets Card
            item {
                StockAssetsCardOptimized(
                    stockValue = uiState.stockAssets,
                    isLoading = uiState.isLoading
                )
            }
            
            // Pie Chart
            if (uiState.totalAssets > 0) {
					item(span = { GridItemSpan(columns) }) {
                    PieChartComponentFixed(
                        assets = uiState.assets,
                        isLoading = uiState.isLoading
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
                }
            )
        }

        if (showMissingKeysDialog.value) {
            AlertDialog(
                onDismissRequest = { showMissingKeysDialog.value = false },
                title = { Text(stringResource(R.string.missing_api_keys_title)) },
                text = { Text(stringResource(R.string.missing_api_keys_message)) },
                confirmButton = {
                    TextButton(onClick = {
                        showMissingKeysDialog.value = false
                        onNavigateToSettings()
                    }) { Text(stringResource(R.string.go_to_settings)) }
                },
                dismissButton = {
                    TextButton(onClick = { showMissingKeysDialog.value = false }) { Text(stringResource(R.string.cancel)) }
                }
            )
        }
    }
}