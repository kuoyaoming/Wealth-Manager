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
    var selectedTab by remember { mutableStateOf(0) }
    var cashCurrency by remember { mutableStateOf("TWD") }
    var cashAmount by remember { mutableStateOf("") }
    var stockSymbol by remember { mutableStateOf("") }
    var stockShares by remember { mutableStateOf("") }
    var stockMarket by remember { mutableStateOf("TW") }
    var searchQuery by remember { mutableStateOf("") }
    var showSearchResults by remember { mutableStateOf(false) }
    
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
                                onClick = { selectedTab = 0 },
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
                                onClick = { selectedTab = 1 },
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
                                            onClick = { cashCurrency = "TWD" },
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
                                            onClick = { cashCurrency = "USD" },
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
                                onValueChange = { cashAmount = it },
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
                                    stockSymbol = it
                                    searchQuery = it
                                    if (it.length > 2) {
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
                                onValueChange = { stockShares = it },
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
                                            onClick = { stockMarket = "TW" },
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
                                            onClick = { stockMarket = "US" },
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
                    when (selectedTab) {
                        0 -> {
                            val amount = cashAmount.toDoubleOrNull()
                            if (amount != null && amount > 0) {
                                onAddCash(cashCurrency, amount)
                            }
                        }
                        1 -> {
                            val shares = stockShares.toDoubleOrNull()
                            if (stockSymbol.isNotEmpty() && shares != null && shares > 0) {
                                onAddStock(stockSymbol, shares, stockMarket)
                            }
                        }
                    }
                }
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}