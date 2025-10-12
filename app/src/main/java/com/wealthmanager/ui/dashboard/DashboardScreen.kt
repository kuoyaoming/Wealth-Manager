package com.wealthmanager.ui.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
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
import com.wealthmanager.utils.MoneyFormatter
import com.wealthmanager.utils.PerformanceTracker
import com.wealthmanager.utils.rememberMoneyText
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToAssets: () -> Unit,
    onNavigateToSettings: () -> Unit,
    navController: NavHostController,
    viewModel: DashboardViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val apiStatus by viewModel.apiStatus.collectAsState()
    val manualSyncStatus by viewModel.manualSyncStatus.collectAsState()
    val (hapticManager, view) = rememberHapticFeedbackWithView()
    val responsiveLayout = rememberResponsiveLayout()
    val snackbarHostState = remember { SnackbarHostState() }
    val debugLogManager = remember { DebugLogManager() }

    LaunchedEffect(Unit) {
        viewModel.loadPortfolioData()
    }

    SyncFeedbackHandler(
        syncFeedbackManager = viewModel.syncFeedbackManager,
        snackbarHostState = snackbarHostState,
        onRetry = { syncType -> if (syncType == SyncType.MANUAL_REFRESH) viewModel.refreshData() },
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                modifier = Modifier.statusBarsPadding(),
                title = { Text(stringResource(R.string.dashboard_title)) },
                actions = {
                    // TopAppBar actions remain the same
                    IconButton(onClick = { viewModel.toggleCurrency() }) {
                        Icon(Icons.Default.Wallet, contentDescription = stringResource(R.string.change_currency))
                    }
                    IconButton(onClick = { viewModel.refreshData() }) {
                        Icon(Icons.Default.Refresh, contentDescription = stringResource(R.string.refresh_data))
                    }
                    IconButton(onClick = { onNavigateToSettings() }) {
                        Icon(Icons.Default.Settings, contentDescription = stringResource(R.string.cd_open_settings))
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToAssets) {
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

            // Treemap Chart
            if (uiState.totalAssets > 0) {
                item {
                    TreemapChartComponent(
                        assets = uiState.assets,
                        isLoading = uiState.isLoading,
                    )
                }
            }

            // Total Assets Card
            item {
                TotalAssetsCard(
                    totalValue = uiState.totalAssets,
                    isLoading = uiState.isLoading,
                    currency = uiState.displayCurrency,
                    exchangeRate = uiState.exchangeRate,
                )
            }

            // Sorted Asset List
            item {
                Text(
                    text = stringResource(R.string.assets_title),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )
            }

            items(uiState.assets.sortedByDescending { it.value }) { asset ->
                AssetListItem(asset = asset, currency = uiState.displayCurrency)
            }
        }

        if (apiStatus.hasError) {
            ApiErrorBanner(
                errorMessage = apiStatus.errorMessage,
                isRetrying = false,
                isDataStale = false,
                onRetry = { viewModel.refreshData() },
                onDismiss = { /* ... */ },
            )
        }
    }
}

@Composable
private fun AssetListItem(asset: AssetItem, currency: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = asset.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = rememberMoneyText(
                        amount = asset.value,
                        currencyCode = currency,
                        style = MoneyFormatter.Style.CurrencySymbol
                    ),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )

                if (asset.dayChangePercentage != 0.0) {
                    val (color, sign) = if (asset.dayChangePercentage > 0) {
                        colorResource(id = R.color.deep_green) to "+"
                    } else {
                        colorResource(id = R.color.deep_red) to ""
                    }
                    Text(
                        text = String.format(Locale.US, "%s%.2f%%", sign, asset.dayChangePercentage),
                        color = color,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }
    }
}
