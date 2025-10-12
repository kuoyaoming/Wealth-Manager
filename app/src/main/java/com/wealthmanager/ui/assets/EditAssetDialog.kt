package com.wealthmanager.ui.assets

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.wealthmanager.R
import com.wealthmanager.data.entity.CashAsset
import com.wealthmanager.data.entity.StockAsset

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditAssetDialog(
    assetToEdit: Any,
    onDismiss: () -> Unit,
    viewModel: EditAssetViewModel = hiltViewModel(),
) {
    LaunchedEffect(assetToEdit) {
        viewModel.loadAsset(assetToEdit)
    }

    val uiState by viewModel.uiState.collectAsState()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            val titleRes = when (assetToEdit) {
                is CashAsset -> R.string.dialog_edit_cash_asset
                is StockAsset -> R.string.dialog_edit_stock_asset
                else -> R.string.edit
            }
            Text(stringResource(titleRes))
        },
        text = {
            when (assetToEdit) {
                is CashAsset -> EditCashAssetFields(viewModel)
                is StockAsset -> EditStockAssetFields(viewModel)
            }
        },
        confirmButton = {
            TextButton(onClick = { viewModel.onSave(onSuccess = onDismiss) }) {
                Text(stringResource(R.string.dialog_save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.dialog_cancel))
            }
        }
    )
}

@Composable
private fun EditCashAssetFields(viewModel: EditAssetViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    Column {
        Text(stringResource(R.string.assets_cash_currency_label))
        Spacer(modifier = Modifier.height(8.dp))
        Row {
            CurrencyRadioButton(text = "TWD", selected = uiState.currency == "TWD", onClick = { viewModel.onCurrencyChange("TWD") })
            Spacer(modifier = Modifier.width(16.dp))
            CurrencyRadioButton(text = "USD", selected = uiState.currency == "USD", onClick = { viewModel.onCurrencyChange("USD") })
        }
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = uiState.amount,
            onValueChange = viewModel::onAmountChange,
            label = { Text(stringResource(R.string.asset_form_amount_label)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun EditStockAssetFields(viewModel: EditAssetViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val asset = uiState.asset as? StockAsset ?: return

    Column {
        Text(stringResource(R.string.assets_stock_symbol_market, asset.symbol, asset.market))
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = uiState.shares,
            onValueChange = viewModel::onSharesChange,
            label = { Text(stringResource(R.string.asset_form_shares_label)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
