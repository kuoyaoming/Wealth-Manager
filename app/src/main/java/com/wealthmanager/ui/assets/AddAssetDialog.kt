package com.wealthmanager.ui.assets

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
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
import androidx.compose.ui.unit.dp
import com.wealthmanager.R
import com.wealthmanager.data.service.StockSearchItem
import com.wealthmanager.debug.DebugLogManager
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAssetDialog(
    onDismiss: () -> Unit,
    onAddCash: (String, Double) -> Unit,
    onAddStock: (String, Double, String) -> Unit,
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
    var stockMarket by remember { mutableStateOf("TW") }
    var searchQuery by remember { mutableStateOf("") }
    var showSearchResults by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        debugLogManager.logUserAction("Add Asset Dialog Opened")
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
                                    cashAmount = it 
                                },
                                placeholder = { Text("Enter amount") },
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
                                    if (it.length > 2) {
                                        debugLogManager.logUserAction("Stock Search Triggered")
                                        debugLogManager.log("UI", "Auto-triggering stock search for: $it")
                                        onSearchStocks(it, stockMarket)
                                        showSearchResults = true
                                    } else {
                                        showSearchResults = false
                                    }
                                },
                                placeholder = { Text("e.g., AAPL, TSMC") },
                                trailingIcon = {
                                    if (isSearching) {
                                        CircularProgressIndicator(modifier = Modifier.size(20.dp))
                                    } else {
                                        IconButton(onClick = { 
                                            debugLogManager.logUserAction("Manual Stock Search Clicked")
                                            debugLogManager.log("UI", "User clicked manual search button for: $searchQuery")
                                            if (searchQuery.isNotEmpty()) {
                                                onSearchStocks(searchQuery, stockMarket)
                                                showSearchResults = true
                                            }
                                        }) {
                                            Icon(Icons.Default.Search, contentDescription = "Search")
                                        }
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                            
                            if (showSearchResults && searchResults.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Search Results:",
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
                                placeholder = { Text("Enter number of shares") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Market")
                            Spacer(modifier = Modifier.height(8.dp))
                            Row {
                                Row(
                                    modifier = Modifier
                                        .weight(1f)
                                        .selectable(
                                            selected = stockMarket == "TW",
                                            onClick = { 
                                                debugLogManager.logUserAction("Taiwan Market Selected")
                                                debugLogManager.log("UI", "User selected Taiwan market for stock")
                                                stockMarket = "TW" 
                                            },
                                            role = Role.RadioButton
                                        )
                                ) {
                                    RadioButton(
                                        selected = stockMarket == "TW",
                                        onClick = null
                                    )
                                    Text("Taiwan", modifier = Modifier.padding(start = 8.dp))
                                }
                                Row(
                                    modifier = Modifier
                                        .weight(1f)
                                        .selectable(
                                            selected = stockMarket == "US",
                                            onClick = { 
                                                debugLogManager.logUserAction("US Market Selected")
                                                debugLogManager.log("UI", "User selected US market for stock")
                                                stockMarket = "US" 
                                            },
                                            role = Role.RadioButton
                                        )
                                ) {
                                    RadioButton(
                                        selected = stockMarket == "US",
                                        onClick = null
                                    )
                                    Text("US", modifier = Modifier.padding(start = 8.dp))
                                }
                            }
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
                            debugLogManager.log("UI", "Adding stock asset: $stockSymbol, $stockShares shares, $stockMarket market")
                            val shares = stockShares.toDoubleOrNull()
                            if (stockSymbol.isNotEmpty() && shares != null && shares > 0) {
                                onAddStock(stockSymbol, shares, stockMarket)
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