package com.wealthmanager.ui.assets

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.wealthmanager.R

@Composable
fun AddAssetDialog(
    onDismiss: () -> Unit,
    onAddCash: (String, Double) -> Unit,
    onAddStock: (String, Double, String) -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    var cashCurrency by remember { mutableStateOf("TWD") }
    var cashAmount by remember { mutableStateOf("") }
    var stockSymbol by remember { mutableStateOf("") }
    var stockShares by remember { mutableStateOf("") }
    var stockMarket by remember { mutableStateOf("TW") }
    
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
                                onValueChange = { stockSymbol = it },
                                placeholder = { Text("e.g., AAPL, TSMC") },
                                modifier = Modifier.fillMaxWidth()
                            )
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