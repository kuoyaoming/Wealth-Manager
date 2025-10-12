package com.wealthmanager.ui.assets

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.wealthmanager.data.entity.CashAsset
import com.wealthmanager.data.entity.StockAsset
import com.wealthmanager.debug.DebugLogManager
import com.wealthmanager.haptic.HapticFeedbackManager
import com.wealthmanager.haptic.rememberHapticFeedbackWithView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssetsScreen(
    onNavigateBack: () -> Unit,
    viewModel: AssetsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var assetToEdit by remember { mutableStateOf<Any?>(null) }
    val debugLogManager = remember { DebugLogManager() }
    val (hapticManager, view) = rememberHapticFeedbackWithView()

    LaunchedEffect(Unit) {
        debugLogManager.logUserAction("Assets Screen Opened")
        viewModel.loadAssets()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                modifier = Modifier.statusBarsPadding(),
                title = { Text(stringResource(R.string.assets_title)) },
                navigationIcon = {
                    IconButton(onClick = {
                        debugLogManager.logUserAction("Back Button Clicked")
                        hapticManager.triggerHaptic(view, HapticFeedbackManager.HapticIntensity.LIGHT)
                        onNavigateBack()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.cd_back_to_dashboard),
                        )
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    debugLogManager.logUserAction("Add Asset FAB Clicked")
                    hapticManager.triggerHaptic(view, HapticFeedbackManager.HapticIntensity.MEDIUM)
                    showAddDialog = true
                },
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.cd_add_asset))
            }
        },
    ) { paddingValues ->
        LazyColumn(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            // Cash Assets Section
            item {
                Text(
                    text = stringResource(R.string.cash_assets),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(vertical = 8.dp),
                )
            }

            if (uiState.cashAssets.isEmpty()) {
                item {
                    Text(
                        text = stringResource(R.string.assets_empty_cash),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            } else {
                items(items = uiState.cashAssets, key = { it.currency }) { asset ->
                    CashAssetItem(
                        asset = asset,
                        onEdit = { assetToEdit = it },
                        onDelete = { viewModel.deleteCashAsset(it) },
                    )
                }
            }

            // Stock Assets Section
            item {
                Text(
                    text = stringResource(R.string.stock_assets),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(vertical = 8.dp),
                )
            }

            if (uiState.stockAssets.isEmpty()) {
                item {
                    Text(
                        text = stringResource(R.string.assets_empty_stock),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            } else {
                items(items = uiState.stockAssets, key = { it.id }) { asset ->
                    StockAssetItem(
                        asset = asset,
                        onEdit = { assetToEdit = it },
                        onDelete = { viewModel.deleteStockAsset(it) },
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        AddAssetDialog(onDismiss = { showAddDialog = false })
    }

    assetToEdit?.let {
        EditAssetDialog(
            assetToEdit = it,
            onDismiss = { assetToEdit = null },
        )
    }
}
