package com.wealthmanager.ui.assets

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.wealthmanager.R
import com.wealthmanager.data.model.StockSearchItem
import com.wealthmanager.debug.DebugLogManager
import com.wealthmanager.haptic.HapticFeedbackManager
import com.wealthmanager.haptic.rememberHapticFeedbackWithView
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAssetDialog(
    onDismiss: () -> Unit,
    cashCurrency: String,
    cashAmount: String,
    cashButtonLabelRes: Int,
    onCurrencyChange: (String) -> Unit,
    onCashAmountChange: (String) -> Unit,
    onAddCash: (String, Double) -> Unit,
    onAddStock: (String, Double) -> Unit,
    onSearchStocks: (String, String) -> Unit = { _, _ -> },
    onSearchQueryChange: (String) -> Unit = {},
    searchResults: List<StockSearchItem> = emptyList(),
    isSearching: Boolean = false
) {
    val debugLogManager = remember { DebugLogManager() }
    val (hapticManager, view) = rememberHapticFeedbackWithView()
    var selectedTab by remember { mutableStateOf(0) }
    val isUpdating = remember(cashButtonLabelRes) { cashButtonLabelRes == R.string.update }
    var stockSymbol by remember { mutableStateOf("") }
    var stockShares by remember { mutableStateOf("") }
    var showSearchResults by remember { mutableStateOf(false) }
    var searchError by remember { mutableStateOf("") }
    
    LaunchedEffect(cashCurrency) { 
        debugLogManager.log("UI", "Cash currency changed to: $cashCurrency")
        debugLogManager.logUserAction("Cash Currency Changed")
    }
    LaunchedEffect(cashAmount) { 
        debugLogManager.log("UI", "Cash amount changed to: $cashAmount")
        debugLogManager.logUserAction("Cash Amount Changed")
    }

    LaunchedEffect(Unit) {
        debugLogManager.logUserAction("Add Asset Dialog Opened")
    }
    
    // Debounced search is handled in ViewModel; this composable only forwards query changes
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.dialog_add_asset)) },
        text = {
            Column {
                // Tab Selection
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectableGroup()
                ) {
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .selectable(
                                selected = selectedTab == 0,
                                onClick = { 
                                    debugLogManager.logUserAction("Cash Tab Selected")
                                    debugLogManager.log("UI", "User switched to Cash tab in Add Asset dialog")
                                    selectedTab = 0 
                                },
                                role = Role.Tab
                            )
                    ) {
                        RadioButton(
                            selected = selectedTab == 0,
                            onClick = null
                        )
                        Text(stringResource(R.string.assets_tab_cash), modifier = Modifier.padding(start = 8.dp))
                    }
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .selectable(
                                selected = selectedTab == 1,
                                onClick = { 
                                    debugLogManager.logUserAction("Stock Tab Selected")
                                    debugLogManager.log("UI", "User switched to Stock tab in Add Asset dialog")
                                    selectedTab = 1 
                                },
                                role = Role.Tab
                            )
                    ) {
                        RadioButton(
                            selected = selectedTab == 1,
                            onClick = null
                        )
                        Text(stringResource(R.string.assets_tab_stock), modifier = Modifier.padding(start = 8.dp))
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                when (selectedTab) {
                    0 -> { // Cash
                        Column {
                            Text(stringResource(R.string.assets_cash_currency_label))
                            Spacer(modifier = Modifier.height(8.dp))
                            Row {
                                Row(
                                    modifier = Modifier
                                        .weight(1f)
                                        .selectable(
                                            selected = cashCurrency == "TWD",
                                            onClick = { 
                                                debugLogManager.logUserAction("TWD Currency Selected")
                                                debugLogManager.log("UI", "User selected TWD currency for cash asset")
                                                onCurrencyChange("TWD")
                                            },
                                            role = Role.RadioButton
                                        )
                                ) {
                                    RadioButton(
                                        selected = cashCurrency == "TWD",
                                        onClick = null
                                    )
                                    Text(stringResource(R.string.assets_currency_twd), modifier = Modifier.padding(start = 8.dp))
                                }
                                Row(
                                    modifier = Modifier
                                        .weight(1f)
                                        .selectable(
                                            selected = cashCurrency == "USD",
                                            onClick = { 
                                                debugLogManager.logUserAction("USD Currency Selected")
                                                debugLogManager.log("UI", "User selected USD currency for cash asset")
                                                onCurrencyChange("USD")
                                            },
                                            role = Role.RadioButton
                                        )
                                ) {
                                    RadioButton(
                                        selected = cashCurrency == "USD",
                                        onClick = null
                                    )
                                    Text(stringResource(R.string.assets_currency_usd), modifier = Modifier.padding(start = 8.dp))
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            if (isUpdating) {
                                Text(
                                    text = stringResource(R.string.cash_update_existing, cashCurrency.uppercase()),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                            Text(stringResource(R.string.cash_amount))
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = cashAmount,
                                onValueChange = { 
                                    debugLogManager.log("UI", "User typing cash amount: $it")
                                    if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*$"))) {
                                        onCashAmountChange(it)
                                    }
                                },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                placeholder = { Text(stringResource(R.string.assets_amount_placeholder)) },
                                isError = cashAmount.isNotEmpty() && cashAmount.toDoubleOrNull() == null,
                                supportingText = if (cashAmount.isNotEmpty() && cashAmount.toDoubleOrNull() == null) {
                                    { Text(stringResource(R.string.validation_enter_valid_number), color = MaterialTheme.colorScheme.error) }
                                } else null,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                    1 -> { // Stock
                        Column {
                            Text(stringResource(R.string.stock_symbol))
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = stockSymbol,
                                onValueChange = {
                                    debugLogManager.log("UI", "User typing stock symbol: $it")
                                    stockSymbol = it
                                    searchError = ""
                                    onSearchQueryChange(it)
                                    showSearchResults = it.isNotEmpty()
                                },
                                placeholder = { Text(stringResource(R.string.assets_stock_symbol_placeholder)) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Search),
                                keyboardActions = KeyboardActions(
                                    onSearch = {
                                        if (stockSymbol.isNotEmpty()) {
                                            debugLogManager.logUserAction("IME Search Triggered")
                                            debugLogManager.log("UI", "IME Search for: $stockSymbol")
                                            onSearchStocks(stockSymbol, "")
                                            showSearchResults = true
                                        }
                                    }
                                ),
                                trailingIcon = {
                                    if (isSearching) {
                                        CircularProgressIndicator(modifier = Modifier.size(20.dp))
                                    } else {
                                        IconButton(onClick = { 
                                            debugLogManager.logUserAction("Manual Stock Search Clicked")
                                            debugLogManager.log("UI", "User clicked manual search button for: $stockSymbol")
                                            hapticManager.triggerHaptic(view, HapticFeedbackManager.HapticIntensity.LIGHT)
                                            if (stockSymbol.isNotEmpty()) {
                                                onSearchStocks(stockSymbol, "")
                                                showSearchResults = true
                                            }
                                        }) {
                                            Icon(Icons.Default.Search, contentDescription = stringResource(R.string.cd_search))
                                        }
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                            
                            // Show search results when available
                            if (searchResults.isNotEmpty() && showSearchResults) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = stringResource(R.string.assets_search_results_count, searchResults.size),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .heightIn(max = 150.dp)
                                ) {
                                    items(searchResults.take(5)) { result ->
                                        Card(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 2.dp),
                                            onClick = {
                                                debugLogManager.logUserAction("Stock Selected from Search")
                                                debugLogManager.log("UI", "User selected stock: ${result.symbol} - ${result.longName}")
                                                hapticManager.triggerHaptic(view, HapticFeedbackManager.HapticIntensity.MEDIUM)
                                                stockSymbol = result.symbol
                                                showSearchResults = false
                                                searchError = ""
                                            }
                                        ) {
                                            Column(
                                                modifier = Modifier.padding(8.dp)
                                            ) {
                                                Text(
                                                    text = result.symbol,
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    fontWeight = FontWeight.Medium
                                                )
                                                Text(
                                                    text = result.longName,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                    }
                                }
                            } else if (isSearching) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    CircularProgressIndicator(modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = stringResource(R.string.searching),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            } else if (searchError.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = searchError,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.error
                                )
                            } else if (showSearchResults && searchResults.isEmpty() && !isSearching) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = stringResource(R.string.assets_search_no_match),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(stringResource(R.string.stock_shares))
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = stockShares,
                                onValueChange = { 
                                    debugLogManager.log("UI", "User typing stock shares: $it")
                                    stockShares = it 
                                },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                placeholder = { Text(stringResource(R.string.assets_shares_placeholder)) },
                                isError = stockShares.isNotEmpty() && stockShares.toDoubleOrNull() == null,
                                supportingText = if (stockShares.isNotEmpty() && stockShares.toDoubleOrNull() == null) {
                                    { Text(stringResource(R.string.validation_enter_valid_shares), color = MaterialTheme.colorScheme.error) }
                                } else null,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    debugLogManager.logUserAction("Add Asset Button Clicked")
                    hapticManager.triggerHaptic(view, HapticFeedbackManager.HapticIntensity.CONFIRM)
                    when (selectedTab) {
                        0 -> {
                            debugLogManager.log("UI", "Adding cash asset: $cashCurrency $cashAmount")
                            val amount = cashAmount.toDoubleOrNull()
                            if (amount != null && amount > 0) {
                                onAddCash(cashCurrency, amount)
                            } else {
                                debugLogManager.log("UI", "Invalid cash amount: $cashAmount")
                            }
                        }
                        1 -> {
                            debugLogManager.log("UI", "Adding stock asset: $stockSymbol, $stockShares shares")
                            val shares = stockShares.toDoubleOrNull()
                            if (stockSymbol.isNotEmpty() && shares != null && shares > 0) {
                                onAddStock(stockSymbol, shares)
                            } else {
                                debugLogManager.log("UI", "Invalid stock data: symbol=$stockSymbol, shares=$stockShares")
                            }
                        }
                    }
                }
            ) {
                Text(
                    text = if (selectedTab == 0) {
                        if (isUpdating) stringResource(R.string.update) else stringResource(R.string.add_cash)
                    } else {
                        stringResource(R.string.add_stock)
                    }
                )
            }
        },
        dismissButton = {
            TextButton(onClick = { 
                debugLogManager.logUserAction("Cancel Asset Dialog")
                debugLogManager.log("UI", "User cancelled Add Asset dialog")
                hapticManager.triggerHaptic(view, HapticFeedbackManager.HapticIntensity.LIGHT)
                onDismiss() 
            }) {
                Text(stringResource(R.string.dialog_cancel))
            }
        }
    )
}