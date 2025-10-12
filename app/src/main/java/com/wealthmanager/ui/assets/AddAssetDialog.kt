package com.wealthmanager.ui.assets

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.wealthmanager.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAssetDialog(
    onDismiss: () -> Unit,
    viewModel: AddAssetViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.dialog_add_asset)) },
        text = {
            Column {
                Row(modifier = Modifier.fillMaxWidth().selectableGroup()) {
                    AssetTypeRadioButton(text = stringResource(R.string.assets_tab_cash), selected = uiState.selectedTab == 0, onClick = { viewModel.onTabSelected(0) })
                    Spacer(modifier = Modifier.width(8.dp))
                    AssetTypeRadioButton(text = stringResource(R.string.assets_tab_stock), selected = uiState.selectedTab == 1, onClick = { viewModel.onTabSelected(1) })
                }

                Spacer(modifier = Modifier.height(16.dp))

                when (uiState.selectedTab) {
                    0 -> CashInputFields(viewModel = viewModel)
                    1 -> StockInputFields(viewModel = viewModel)
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (uiState.selectedTab == 0) {
                        viewModel.addCashAsset()
                    } else {
                        viewModel.addStockAsset()
                    }
                    onDismiss()
                }
            ) {
                Text(stringResource(if (uiState.selectedTab == 0) R.string.add_cash else R.string.add_stock))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.dialog_cancel))
            }
        },
    )
}

@Composable
private fun AssetTypeRadioButton(text: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier.selectable(selected = selected, onClick = onClick, role = Role.RadioButton).padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = selected, onClick = null)
        Text(text = text, modifier = Modifier.padding(start = 8.dp))
    }
}

@Composable
private fun CashInputFields(viewModel: AddAssetViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    Column {
        Text(stringResource(R.string.assets_cash_currency_label))
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.selectableGroup()) {
            CurrencyRadioButton(text = "TWD", selected = uiState.cashCurrency == "TWD", onClick = { viewModel.onCashCurrencyChange("TWD") })
            Spacer(modifier = Modifier.width(16.dp))
            CurrencyRadioButton(text = "USD", selected = uiState.cashCurrency == "USD", onClick = { viewModel.onCashCurrencyChange("USD") })
        }
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = uiState.cashAmount,
            onValueChange = viewModel::onCashAmountChange,
            label = { Text(stringResource(id = R.string.assets_cash_amount_label)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
    }
}

@Composable
internal fun CurrencyRadioButton(text: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier.selectable(selected = selected, onClick = onClick, role = Role.RadioButton).padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = selected, onClick = null)
        Text(text = text, modifier = Modifier.padding(start = 8.dp))
    }
}

@Composable
private fun StockInputFields(viewModel: AddAssetViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    Column {
        OutlinedTextField(
            value = uiState.stockSymbol,
            onValueChange = viewModel::onStockSymbolChange,
            label = { Text(stringResource(id = R.string.assets_stock_symbol_label)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            trailingIcon = {
                if (uiState.isSearching) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp))
                } else {
                    Icon(Icons.Default.Search, contentDescription = null)
                }
            }
        )

        if (uiState.searchResults.isNotEmpty()) {
            LazyColumn(modifier = Modifier.heightIn(max = 150.dp)) {
                items(uiState.searchResults) { result ->
                    Text(
                        text = "${result.symbol} - ${result.longName}",
                        modifier = Modifier.fillMaxWidth().padding(8.dp).selectable(selected = false, onClick = { viewModel.onSearchResultSelected(result.symbol) })
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = uiState.stockShares,
            onValueChange = viewModel::onStockSharesChange,
            label = { Text(stringResource(id = R.string.assets_stock_shares_label)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
    }
}
