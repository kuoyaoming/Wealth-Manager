package com.wealthmanager.ui.assets

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.wealthmanager.R
import com.wealthmanager.data.model.StockSearchItem
import com.wealthmanager.debug.DebugLogManager
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAssetDialog(
    onDismiss: () -> Unit,
    onAddCash: (String, Double) -> Unit,
    onAddStock: (String, Double) -> Unit,
    onSearchStocks: (String, String) -> Unit = { _, _ -> },
    searchResults: List<StockSearchItem> = emptyList(),
    isSearching: Boolean = false
) {
    val debugLogManager = remember { DebugLogManager() }
    var selectedTab by remember { mutableStateOf(0) }
    var cashCurrency by remember { mutableStateOf("TWD") }
    var cashAmount by remember { mutableStateOf("") }
        var stockSymbol by remember { mutableStateOf("") }
        var stockShares by remember { mutableStateOf("") }
        var searchQuery by remember { mutableStateOf("") }
        var showSearchResults by remember { mutableStateOf(false) }
        var searchError by remember { mutableStateOf("") }
        var pendingSearchQuery by remember { mutableStateOf("") }
    
    LaunchedEffect(Unit) {
        debugLogManager.logUserAction("Add Asset Dialog Opened")
    }
    
    // Immediate search logic - triggers search without delay
    LaunchedEffect(pendingSearchQuery) {
        if (pendingSearchQuery.isNotEmpty()) {
            debugLogManager.log("UI", "Immediately triggering search for: $pendingSearchQuery")
            debugLogManager.logUserAction("Immediate Stock Search Triggered")
            onSearchStocks(pendingSearchQuery, "")
            showSearchResults = true
        }
    }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Asset") },
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
                        Text("Cash", modifier = Modifier.padding(start = 8.dp))
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
                        Text("Stock", modifier = Modifier.padding(start = 8.dp))
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                when (selectedTab) {
                    0 -> { // Cash
                        Column {
                            Text("Currency")
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
                                                cashCurrency = "TWD" 
                                            },
                                            role = Role.RadioButton
                                        )
                                ) {
                                    RadioButton(
                                        selected = cashCurrency == "TWD",
                                        onClick = null
                                    )
                                    Text("TWD", modifier = Modifier.padding(start = 8.dp))
                                }
                                Row(
                                    modifier = Modifier
                                        .weight(1f)
                                        .selectable(
                                            selected = cashCurrency == "USD",
                                            onClick = { 
                                                debugLogManager.logUserAction("USD Currency Selected")
                                                debugLogManager.log("UI", "User selected USD currency for cash asset")
                                                cashCurrency = "USD" 
                                            },
                                            role = Role.RadioButton
                                        )
                                ) {
                                    RadioButton(
                                        selected = cashCurrency == "USD",
                                        onClick = null
                                    )
                                    Text("USD", modifier = Modifier.padding(start = 8.dp))
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Amount")
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = cashAmount,
                                onValueChange = { 
                                    debugLogManager.log("UI", "User typing cash amount: $it")
                                    // Allow decimal input for cash amounts
                                    if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*$"))) {
                                        cashAmount = it
                                    }
                                },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                placeholder = { Text("Enter amount (e.g., 1000.50)") },
                                isError = cashAmount.isNotEmpty() && cashAmount.toDoubleOrNull() == null,
                                supportingText = if (cashAmount.isNotEmpty() && cashAmount.toDoubleOrNull() == null) {
                                    { Text("Please enter a valid number", color = MaterialTheme.colorScheme.error) }
                                } else null,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                    1 -> { // Stock
                        Column {
                            Text("Stock Symbol")
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = stockSymbol,
                                    onValueChange = {
                                        debugLogManager.log("UI", "User typing stock symbol: $it")
                                        stockSymbol = it
                                        searchQuery = it
                                        searchError = "" // Clear previous errors
                                        
                                        if (it.isNotEmpty()) {
                                            // Update pending search query, trigger immediate search
                                            pendingSearchQuery = it
                                            debugLogManager.log("UI", "Updated pending search query: $it (will search immediately)")
                                        } else {
                                            // Clear search related state
                                            pendingSearchQuery = ""
                                            showSearchResults = false
                                            searchError = ""
                                            debugLogManager.log("UI", "Cleared search query and results")
                                        }
                                    },
                                placeholder = { Text("e.g., VT, AAPL, TSMC") },
                                trailingIcon = {
                                    if (isSearching) {
                                        CircularProgressIndicator(modifier = Modifier.size(20.dp))
                                    } else {
                                        IconButton(onClick = { 
                                            debugLogManager.logUserAction("Manual Stock Search Clicked")
                                            debugLogManager.log("UI", "User clicked manual search button for: $searchQuery")
                                            if (searchQuery.isNotEmpty()) {
                                                onSearchStocks(searchQuery, "")
                                                showSearchResults = true
                                            }
                                        }) {
                                            Icon(Icons.Default.Search, contentDescription = "Search")
                                        }
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                            
                            // Show search results when available
                            if (searchResults.isNotEmpty() && showSearchResults) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Search Results (${searchResults.size}):",
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
                                        text = "Searching...",
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
                                    text = "No matching stocks found, please check the symbol",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Shares")
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = stockShares,
                                onValueChange = { 
                                    debugLogManager.log("UI", "User typing stock shares: $it")
                                    stockShares = it 
                                },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                placeholder = { Text("Enter number of shares") },
                                isError = stockShares.isNotEmpty() && stockShares.toDoubleOrNull() == null,
                                supportingText = if (stockShares.isNotEmpty() && stockShares.toDoubleOrNull() == null) {
                                    { Text("Please enter a valid number of shares", color = MaterialTheme.colorScheme.error) }
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
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = { 
                debugLogManager.logUserAction("Cancel Asset Dialog")
                debugLogManager.log("UI", "User cancelled Add Asset dialog")
                onDismiss() 
            }) {
                Text("Cancel")
            }
        }
    )
}