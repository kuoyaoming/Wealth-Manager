package com.wealthmanager.ui.assets

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.wealthmanager.R
import com.wealthmanager.data.entity.CashAsset
import com.wealthmanager.data.entity.StockAsset
import com.wealthmanager.debug.DebugLogManager
import com.wealthmanager.haptic.HapticFeedbackManager
import com.wealthmanager.haptic.rememberHapticFeedbackWithView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditCashAssetDialog(
    asset: CashAsset,
    onDismiss: () -> Unit,
    onSave: (CashAsset) -> Unit,
) {
    val debugLogManager = remember { DebugLogManager() }
    val (hapticManager, view) = rememberHapticFeedbackWithView()
    var currency by remember { mutableStateOf(asset.currency) }
    var amount by remember { mutableStateOf(asset.amount.toString()) }

    LaunchedEffect(Unit) {
        debugLogManager.logUserAction("Edit Cash Asset Dialog Opened")
        debugLogManager.log("UI", "Editing cash asset: ${asset.currency} ${asset.amount}")
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.dialog_edit_cash_asset)) },
        text = {
            Column {
                Text(stringResource(R.string.assets_cash_currency_label))
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth().selectableGroup(),
                    horizontalArrangement = Arrangement.SpaceAround,
                ) {
                    listOf("TWD", "USD").forEach { text ->
                        Row(
                            Modifier
                                .selectable(
                                    selected = text == currency,
                                    onClick = {
                                        debugLogManager.logUserAction("Currency Changed to $text")
                                        debugLogManager.log("UI", "User changed currency to $text")
                                        currency = text
                                    },
                                    role = Role.RadioButton,
                                )
                                .padding(horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            RadioButton(
                                selected = text == currency,
                                onClick = null,
                            )
                            Text(
                                text =
                                    stringResource(
                                        if (text == "TWD") R.string.assets_currency_twd else R.string.assets_currency_usd,
                                    ),
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(start = 8.dp),
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(stringResource(R.string.cash_amount))
                Spacer(modifier = Modifier.height(8.dp))
                val amountExample = if (currency == "TWD") "1000" else "1000.50"
                OutlinedTextField(
                    value = amount,
                    onValueChange = {
                        debugLogManager.log("UI", "User typing amount: $it")
                        // Allow decimal input for cash amounts
                        if (it.isEmpty() || it.matches(Regex("""^\d*\.?\d*$"""))) {
                            amount = it
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    placeholder = { Text(stringResource(R.string.assets_amount_placeholder, amountExample)) },
                    isError = amount.isNotEmpty() && amount.toDoubleOrNull() == null,
                    supportingText =
                        if (amount.isNotEmpty() && amount.toDoubleOrNull() == null) {
                            {
                                Text(
                                    stringResource(R.string.validation_enter_valid_number),
                                    color = MaterialTheme.colorScheme.error,
                                )
                            }
                        } else {
                            null
                        },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    debugLogManager.logUserAction("Save Cash Asset Changes")
                    hapticManager.triggerHaptic(view, HapticFeedbackManager.HapticIntensity.CONFIRM)
                    val newAmount = amount.toDoubleOrNull()
                    if (newAmount != null && newAmount > 0) {
                        val twdEquivalent = if (currency == "TWD") newAmount else newAmount * 30.0
                        val updatedAsset =
                            asset.copy(
                                currency = currency,
                                amount = newAmount,
                                twdEquivalent = twdEquivalent,
                            )
                        debugLogManager.log("UI", "Saving cash asset changes: $currency $newAmount")
                        onSave(updatedAsset)
                    } else {
                        debugLogManager.log("UI", "Invalid amount: $amount")
                    }
                },
            ) {
                Text(stringResource(R.string.dialog_save))
            }
        },
        dismissButton = {
            TextButton(onClick = {
                debugLogManager.logUserAction("Cancel Cash Asset Edit")
                debugLogManager.log("UI", "User cancelled cash asset edit")
                hapticManager.triggerHaptic(view, HapticFeedbackManager.HapticIntensity.LIGHT)
                onDismiss()
            }) {
                Text(stringResource(R.string.dialog_cancel))
            }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditStockAssetDialog(
    asset: StockAsset,
    onDismiss: () -> Unit,
    onSave: (StockAsset) -> Unit,
) {
    val debugLogManager = remember { DebugLogManager() }
    val (hapticManager, view) = rememberHapticFeedbackWithView()
    var shares by remember { mutableStateOf(asset.shares.toString()) }

    LaunchedEffect(Unit) {
        debugLogManager.logUserAction("Edit Stock Asset Dialog Opened")
        debugLogManager.log("UI", "Editing stock asset: ${asset.symbol}")
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.dialog_edit_stock_asset)) },
        text = {
            Column {
                Text(stringResource(R.string.assets_stock_symbol_market, asset.symbol, asset.market))
                Text(stringResource(R.string.assets_stock_company_symbol, asset.companyName, asset.symbol))
                Spacer(modifier = Modifier.height(16.dp))
                Text(stringResource(R.string.stock_shares))
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = shares,
                    onValueChange = {
                        debugLogManager.log("UI", "User typing shares: $it")
                        shares = it
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    placeholder = { Text(stringResource(R.string.assets_shares_placeholder)) },
                    isError = shares.isNotEmpty() && shares.toDoubleOrNull() == null,
                    supportingText =
                        if (shares.isNotEmpty() && shares.toDoubleOrNull() == null) {
                            {
                                Text(
                                    stringResource(R.string.validation_enter_valid_shares),
                                    color = MaterialTheme.colorScheme.error,
                                )
                            }
                        } else {
                            null
                        },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    debugLogManager.logUserAction("Save Stock Asset Changes")
                    hapticManager.triggerHaptic(view, HapticFeedbackManager.HapticIntensity.CONFIRM)
                    val newShares = shares.toDoubleOrNull()
                    if (newShares != null && newShares > 0) {
                        val updatedAsset =
                            asset.copy(
                                shares = newShares,
                                market = "GLOBAL", // Fixed to GLOBAL market
                            )
                        debugLogManager.log("UI", "Saving stock asset changes: $newShares shares")
                        onSave(updatedAsset)
                    } else {
                        debugLogManager.log("UI", "Invalid shares: $shares")
                    }
                },
            ) {
                Text(stringResource(R.string.dialog_save))
            }
        },
        dismissButton = {
            TextButton(onClick = {
                debugLogManager.logUserAction("Cancel Stock Asset Edit")
                debugLogManager.log("UI", "User cancelled stock asset edit")
                hapticManager.triggerHaptic(view, HapticFeedbackManager.HapticIntensity.LIGHT)
                onDismiss()
            }) {
                Text(stringResource(R.string.dialog_cancel))
            }
        },
    )
}
