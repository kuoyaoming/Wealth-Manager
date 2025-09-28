package com.wealthmanager.ui.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
// import com.wealthmanager.BuildConfig
import com.wealthmanager.R
import com.wealthmanager.debug.DebugLogManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToAssets: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val debugLogManager = remember { DebugLogManager() }
    
    // Check if this is a debug build
    val isDebugBuild = remember {
        try {
            Class.forName("com.wealthmanager.BuildConfig").getField("DEBUG").getBoolean(null)
        } catch (e: Exception) {
            true // Assume debug if BuildConfig is not available
        }
    }
    
    // Observe API status
    val apiStatus by viewModel.apiStatus.collectAsState()
    
    LaunchedEffect(Unit) {
        debugLogManager.logUserAction("Dashboard Screen Opened")
        viewModel.loadPortfolioData()
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.dashboard_title)) },
                    actions = {
                        IconButton(onClick = { 
                            debugLogManager.logUserAction("Refresh Data Button Clicked")
                            debugLogManager.log("UI", "User clicked refresh button to update market data")
                            viewModel.refreshData() 
                        }) {
                            Icon(Icons.Default.Refresh, contentDescription = stringResource(R.string.refresh_data))
                        }
                        
                        // Only show debug button in debug builds
                        if (isDebugBuild) {
                            IconButton(onClick = { 
                                debugLogManager.logUserAction("Debug Log Button Clicked")
                                debugLogManager.log("UI", "User clicked debug log button to copy logs to clipboard")
                                debugLogManager.copyLogsToClipboard(context)
                            }) {
                                Icon(Icons.Default.BugReport, contentDescription = "Copy Debug Logs")
                            }
                        }
                    }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { 
                    debugLogManager.logUserAction("Navigate to Assets Button Clicked")
                    debugLogManager.log("UI", "User clicked FAB to navigate to Assets screen")
                    onNavigateToAssets() 
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_cash))
            }
        }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // API Error Banner
                if (apiStatus.hasError) {
                    item {
                        ApiErrorBanner(
                            errorMessage = apiStatus.errorMessage,
                            isRetrying = apiStatus.isRetrying,
                            isDataStale = apiStatus.isDataStale,
                            onRetry = { viewModel.retryApiCall() },
                            onDismiss = { viewModel.dismissApiError() }
                        )
                    }
                }
            item {
                TotalAssetsCard(
                    totalValue = uiState.totalAssets,
                    isLoading = uiState.isLoading
                )
            }
            
            item {
                CashAssetsCard(
                    cashValue = uiState.cashAssets,
                    isLoading = uiState.isLoading
                )
            }
            
            item {
                StockAssetsCard(
                    stockValue = uiState.stockAssets,
                    isLoading = uiState.isLoading
                )
            }
            
            if (uiState.assets.isNotEmpty()) {
                item {
                    PieChartCard(
                        assets = uiState.assets,
                        isLoading = uiState.isLoading
                    )
                }
            }
        }
    }
}