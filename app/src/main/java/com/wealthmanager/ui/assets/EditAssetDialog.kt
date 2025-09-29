package com.wealthmanager.ui.assets

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.wealthmanager.data.entity.CashAsset
import com.wealthmanager.data.entity.StockAsset
import com.wealthmanager.debug.DebugLogManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditCashAssetDialog(
    asset: CashAsset,
    onDismiss: () -> Unit,
    onSave: (CashAsset) -> Unit
) {
    val debugLogManager = remember { DebugLogManager() }
    var currency by remember { mutableStateOf(asset.currency) }
    var amount by remember { mutableStateOf(asset.amount.toString()) }
    
    LaunchedEffect(Unit) {
        debugLogManager.logUserAction("Edit Cash Asset Dialog Opened")
        debugLogManager.log("UI", "Editing cash asset: ${asset.currency} ${asset.amount}")
    }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Cash Asset") },
        text = {
            Column {
                Text("Currency")
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth().selectableGroup(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    listOf("TWD", "USD").forEach { text ->
                        Row(
                            Modifier
                                .selectable(
                                    selected = (text == currency),
                                    onClick = {
                                        debugLogManager.logUserAction("Currency Changed to $text")
                                        debugLogManager.log("UI", "User changed currency to $text")
                                        currency = text
                                    },
                                    role = Role.RadioButton
                                )
                                .padding(horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (text == currency),
                                onClick = null
                            )
                            Text(
                                text = text,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text("Amount")
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = amount,
                    onValueChange = {
                        debugLogManager.log("UI", "User typing amount: $it")
                        // Allow decimal input for cash amounts
                        if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*$"))) {
                            amount = it
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    placeholder = { Text("Enter amount (e.g., 1000.50)") },
                    isError = amount.isNotEmpty() && amount.toDoubleOrNull() == null,
                    supportingText = if (amount.isNotEmpty() && amount.toDoubleOrNull() == null) {
                        { Text("Please enter a valid number", color = MaterialTheme.colorScheme.error) }
                    } else null,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    debugLogManager.logUserAction("Save Cash Asset Changes")
                    val newAmount = amount.toDoubleOrNull()
                    if (newAmount != null && newAmount > 0) {
                        val twdEquivalent = if (currency == "TWD") newAmount else (newAmount * 30.0)
                        val updatedAsset = asset.copy(
                            currency = currency,
                            amount = newAmount,
                            twdEquivalent = twdEquivalent
                        )
                        debugLogManager.log("UI", "Saving cash asset changes: $currency $newAmount")
                        onSave(updatedAsset)
                    } else {
                        debugLogManager.log("UI", "Invalid amount: $amount")
                    }
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = {
                debugLogManager.logUserAction("Cancel Cash Asset Edit")
                debugLogManager.log("UI", "User cancelled cash asset edit")
                onDismiss()
            }) {
                Text("Cancel")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditStockAssetDialog(
    asset: StockAsset,
    onDismiss: () -> Unit,
    onSave: (StockAsset) -> Unit
) {
    val debugLogManager = remember { DebugLogManager() }
    var shares by remember { mutableStateOf(asset.shares.toString()) }
    
    LaunchedEffect(Unit) {
        debugLogManager.logUserAction("Edit Stock Asset Dialog Opened")
        debugLogManager.log("UI", "Editing stock asset: ${asset.symbol}")
    }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Stock Asset") },
        text = {
            Column {
                Text("Stock: ${asset.symbol}")
                Text("Company: ${asset.companyName}")
                Spacer(modifier = Modifier.height(16.dp))
                Text("Shares")
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = shares,
                    onValueChange = {
                        debugLogManager.log("UI", "User typing shares: $it")
                        shares = it
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    placeholder = { Text("Enter shares") },
                    isError = shares.isNotEmpty() && shares.toDoubleOrNull() == null,
                    supportingText = if (shares.isNotEmpty() && shares.toDoubleOrNull() == null) {
                        { Text("Please enter a valid number of shares", color = MaterialTheme.colorScheme.error) }
                    } else null,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    debugLogManager.logUserAction("Save Stock Asset Changes")
                    val newShares = shares.toDoubleOrNull()
                    if (newShares != null && newShares > 0) {
                        val updatedAsset = asset.copy(
                            shares = newShares,
                            market = "GLOBAL"  // Fixed to GLOBAL market
                        )
                        debugLogManager.log("UI", "Saving stock asset changes: $newShares shares")
                        onSave(updatedAsset)
                    } else {
                        debugLogManager.log("UI", "Invalid shares: $shares")
                    }
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = {
                debugLogManager.logUserAction("Cancel Stock Asset Edit")
                debugLogManager.log("UI", "User cancelled stock asset edit")
                onDismiss()
            }) {
                Text("Cancel")
            }
        }
    )
}