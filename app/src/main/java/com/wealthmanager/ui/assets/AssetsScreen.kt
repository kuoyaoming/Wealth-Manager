package com.wealthmanager.ui.assets

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.wealthmanager.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssetsScreen(
    onNavigateBack: () -> Unit,
    viewModel: AssetsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        viewModel.loadAssets()
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.assets_title)) },
                navigationIcon = {
                    TextButton(onClick = onNavigateBack) {
                        Text(stringResource(R.string.cancel))
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true }
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
                        onEdit = { /* TODO: Edit cash asset */ },
                        onDelete = { /* TODO: Delete cash asset */ }
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
                        onEdit = { /* TODO: Edit stock asset */ },
                        onDelete = { /* TODO: Delete stock asset */ }
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
            }
        )
    }
}