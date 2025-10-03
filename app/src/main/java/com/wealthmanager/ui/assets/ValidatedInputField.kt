package com.wealthmanager.ui.assets

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.wealthmanager.R

/**
 * 帶有即時驗證的輸入欄位
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ValidatedInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Done,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    validationResult: ValidationResult,
    modifier: Modifier = Modifier,
    trailingIcon: @Composable (() -> Unit)? = null,
    enabled: Boolean = true,
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            placeholder = { Text(placeholder) },
            keyboardOptions =
                KeyboardOptions(
                    keyboardType = keyboardType,
                    imeAction = imeAction,
                ),
            keyboardActions = keyboardActions,
            isError = !validationResult.isValid,
            supportingText = {
                ValidationSupportingText(validationResult)
            },
            trailingIcon = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    // 驗證狀態圖標
                    ValidationStatusIcon(validationResult)

                    // 自定義尾隨圖標
                    trailingIcon?.invoke()
                }
            },
            enabled = enabled,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

/**
 * 驗證狀態圖標
 */
@Composable
private fun ValidationStatusIcon(validationResult: ValidationResult) {
    val (icon, color) =
        when {
            !validationResult.isValid -> Icons.Default.Error to MaterialTheme.colorScheme.error
            validationResult.warningMessage != null -> Icons.Default.Warning to MaterialTheme.colorScheme.primary
            validationResult.isValid -> Icons.Default.CheckCircle to Color(0xFF4CAF50)
            else -> null to Color.Unspecified
        }

    if (icon != null) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(20.dp),
        )
    }
}

/**
 * 驗證支持文本
 */
@Composable
private fun ValidationSupportingText(validationResult: ValidationResult) {
    Column {
        // 錯誤訊息
        if (!validationResult.isValid && validationResult.errorMessage != null) {
            Text(
                text = validationResult.errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
            )
        }

        // 警告訊息
        if (validationResult.isValid && validationResult.warningMessage != null) {
            Text(
                text = validationResult.warningMessage,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodySmall,
            )
        }

        // 建議訊息
        if (validationResult.suggestion != null) {
            Text(
                text = stringResource(R.string.validation_suggestion_prefix, validationResult.suggestion),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Normal,
            )
        }
    }
}

/**
 * 現金金額輸入欄位
 */
@Composable
fun CashAmountInputField(
    value: String,
    onValueChange: (String) -> Unit,
    currency: String,
    modifier: Modifier = Modifier,
) {
    val validationResult = CashAmountValidator.validate(value, currency)

    ValidatedInputField(
        value = value,
        onValueChange = onValueChange,
        label = stringResource(R.string.asset_form_amount_label),
        placeholder =
            if (currency == "TWD") {
                stringResource(R.string.validation_amount_example_twd)
            } else {
                stringResource(R.string.validation_amount_example_usd)
            },
        keyboardType = KeyboardType.Decimal,
        validationResult = validationResult,
        modifier = modifier,
    )
}

/**
 * 股票代碼輸入欄位
 */
@Composable
fun StockSymbolInputField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    trailingIcon: @Composable (() -> Unit)? = null,
) {
    val validationResult = StockSymbolValidator.validate(value)

    ValidatedInputField(
        value = value,
        onValueChange = onValueChange,
        label = stringResource(R.string.asset_form_symbol_label),
        placeholder = stringResource(R.string.validation_symbol_example),
        keyboardType = KeyboardType.Text,
        imeAction = ImeAction.Search,
        validationResult = validationResult,
        modifier = modifier,
        trailingIcon = trailingIcon,
    )
}

/**
 * 股票股數輸入欄位
 */
@Composable
fun StockSharesInputField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val validationResult = StockSharesValidator.validate(value)

    ValidatedInputField(
        value = value,
        onValueChange = onValueChange,
        label = stringResource(R.string.asset_form_shares_label),
        placeholder = stringResource(R.string.validation_shares_example),
        keyboardType = KeyboardType.Decimal,
        validationResult = validationResult,
        modifier = modifier,
    )
}
