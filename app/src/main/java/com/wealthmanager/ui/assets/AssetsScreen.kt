package com.wealthmanager.ui.assets

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.wealthmanager.R
import com.wealthmanager.debug.DebugLogManager
import com.wealthmanager.data.entity.CashAsset
import com.wealthmanager.data.entity.StockAsset

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssetsScreen(
    onNavigateBack: () -> Unit,
    viewModel: AssetsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var showEditCashDialog by remember { mutableStateOf<CashAsset?>(null) }
    var showEditStockDialog by remember { mutableStateOf<StockAsset?>(null) }
    val debugLogManager = remember { DebugLogManager() }
    
    LaunchedEffect(Unit) {
        debugLogManager.logUserAction("Assets Screen Opened")
        debugLogManager.log("UI", "Assets screen loaded, starting to load assets")
        viewModel.loadAssets()
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.assets_title)) },
                navigationIcon = {
                    IconButton(onClick = { 
                        debugLogManager.logUserAction("Back Button Clicked")
                        debugLogManager.log("UI", "User clicked back button to return to dashboard")
                        onNavigateBack() 
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to Dashboard"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { 
                    debugLogManager.logUserAction("Add Asset FAB Clicked")
                    debugLogManager.log("UI", "User clicked FAB to open Add Asset dialog")
                    showAddDialog = true 
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
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Cash Assets Section
            item {
                Text(
                    text = stringResource(R.string.cash_assets),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            
            if (uiState.cashAssets.isEmpty()) {
                item {
                    Text(
                        text = "No cash assets found",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                items(uiState.cashAssets.size) { index ->
                    CashAssetItem(
                        asset = uiState.cashAssets[index],
                        onEdit = { asset ->
                            debugLogManager.logUserAction("Edit Cash Asset")
                            debugLogManager.log("UI", "User wants to edit cash asset: ${asset.currency} ${asset.amount}")
                            showEditCashDialog = asset
                        },
                        onDelete = { asset ->
                            debugLogManager.logUserAction("Delete Cash Asset")
                            debugLogManager.log("UI", "User wants to delete cash asset: ${asset.currency} ${asset.amount}")
                            viewModel.deleteCashAsset(asset)
                        }
                    )
                }
            }
            
            // Stock Assets Section
            item {
                Text(
                    text = stringResource(R.string.stock_assets),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            
            if (uiState.stockAssets.isEmpty()) {
                item {
                    Text(
                        text = "No stock assets found",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                items(uiState.stockAssets.size) { index ->
                    StockAssetItem(
                        asset = uiState.stockAssets[index],
                        onEdit = { asset ->
                            debugLogManager.logUserAction("Edit Stock Asset")
                            debugLogManager.log("UI", "User wants to edit stock asset: ${asset.symbol}")
                            showEditStockDialog = asset
                        },
                        onDelete = { asset ->
                            debugLogManager.logUserAction("Delete Stock Asset")
                            debugLogManager.log("UI", "User wants to delete stock asset: ${asset.symbol}")
                            viewModel.deleteStockAsset(asset)
                        }
                    )
                }
            }
        }
    }
    
        // Add Asset Dialog
        if (showAddDialog) {
            AddAssetDialog(
                onDismiss = { showAddDialog = false },
                onAddCash = { currency, amount ->
                    viewModel.addCashAsset(currency, amount)
                    showAddDialog = false
                },
                onAddStock = { symbol, shares, market ->
                    viewModel.addStockAsset(symbol, shares, market)
                    showAddDialog = false
                },
                onSearchStocks = { query, market ->
                    viewModel.searchStocks(query, market)
                },
                searchResults = uiState.searchResults,
                isSearching = uiState.isSearching
            )
        }
        
        // Edit Cash Asset Dialog
        showEditCashDialog?.let { asset ->
            EditCashAssetDialog(
                asset = asset,
                onDismiss = { showEditCashDialog = null },
                onSave = { updatedAsset ->
                    viewModel.updateCashAsset(updatedAsset)
                    showEditCashDialog = null
                }
            )
        }
        
        // Edit Stock Asset Dialog
        showEditStockDialog?.let { asset ->
            EditStockAssetDialog(
                asset = asset,
                onDismiss = { showEditStockDialog = null },
                onSave = { updatedAsset ->
                    viewModel.updateStockAsset(updatedAsset)
                    showEditStockDialog = null
                }
            )
        }
}