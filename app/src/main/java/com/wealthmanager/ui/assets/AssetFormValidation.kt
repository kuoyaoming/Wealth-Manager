package com.wealthmanager.ui.assets

import android.content.Context
import androidx.compose.runtime.Immutable
import com.wealthmanager.R

/**
 * Represents the outcome of a validation check, including potential errors, warnings, or suggestions.
 */
@Immutable
data class ValidationResult(
    val isValid: Boolean,
    val errorMessage: String? = null,
    val warningMessage: String? = null,
    val suggestion: String? = null,
)

/**
 * Validator for cash asset amounts.
 */
object CashAmountValidator {
    fun validate(context: Context, amount: String, currency: String): ValidationResult {
        if (amount.isEmpty()) {
            return ValidationResult(
                isValid = false,
                errorMessage = context.getString(R.string.validation_amount_required),
                suggestion = if (currency == "TWD") context.getString(R.string.validation_amount_example_twd) else context.getString(R.string.validation_amount_example_usd),
            )
        }

        val numericAmount = amount.toDoubleOrNull()
        if (numericAmount == null) {
            return ValidationResult(
                isValid = false,
                errorMessage = context.getString(R.string.validation_amount_invalid_number),
                suggestion = context.getString(R.string.validation_amount_only_numbers),
            )
        }

        if (numericAmount <= 0) {
            return ValidationResult(
                isValid = false,
                errorMessage = context.getString(R.string.validation_amount_must_positive),
                suggestion = context.getString(R.string.validation_amount_enter_positive),
            )
        }

        val maxReasonableAmount = if (currency == "TWD") 1_000_000_000.0 else 100_000_000.0
        if (numericAmount > maxReasonableAmount) {
            return ValidationResult(
                isValid = true,
                warningMessage = context.getString(R.string.validation_amount_too_large),
                suggestion = context.getString(R.string.validation_amount_ignore_if_correct),
            )
        }

        val decimalPlaces = amount.substringAfter('.', "").length
        if (decimalPlaces > 2) {
            return ValidationResult(
                isValid = true,
                warningMessage = context.getString(R.string.validation_amount_decimal_places),
                suggestion = context.getString(R.string.validation_amount_auto_adjusted),
            )
        }

        return ValidationResult(isValid = true)
    }
}

/**
 * Validator for stock asset symbols.
 */
object StockSymbolValidator {
    fun validate(context: Context, symbol: String): ValidationResult {
        if (symbol.isBlank()) {
            return ValidationResult(
                isValid = false,
                errorMessage = context.getString(R.string.validation_symbol_required),
                suggestion = context.getString(R.string.validation_symbol_example),
            )
        }

        val trimmedSymbol = symbol.trim().uppercase()
        if (!trimmedSymbol.matches(Regex("^[A-Z0-9.]{1,10}$"))) {
            return ValidationResult(
                isValid = false,
                errorMessage = context.getString(R.string.validation_symbol_invalid_format),
                suggestion = context.getString(R.string.validation_symbol_only_alphanumeric),
            )
        }

        return ValidationResult(isValid = true)
    }
}

/**
 * Validator for stock asset shares.
 */
object StockSharesValidator {
    fun validate(context: Context, shares: String): ValidationResult {
        if (shares.isEmpty()) {
            return ValidationResult(
                isValid = false,
                errorMessage = context.getString(R.string.validation_shares_required),
                suggestion = context.getString(R.string.validation_shares_example),
            )
        }

        val numericShares = shares.toDoubleOrNull()
        if (numericShares == null) {
            return ValidationResult(
                isValid = false,
                errorMessage = context.getString(R.string.validation_shares_invalid_number),
                suggestion = context.getString(R.string.validation_shares_only_numbers),
            )
        }

        if (numericShares <= 0) {
            return ValidationResult(
                isValid = false,
                errorMessage = context.getString(R.string.validation_shares_must_positive),
                suggestion = context.getString(R.string.validation_shares_enter_positive),
            )
        }

        if (numericShares > 1_000_000) {
            return ValidationResult(
                isValid = true,
                warningMessage = context.getString(R.string.validation_shares_too_large),
                suggestion = context.getString(R.string.validation_shares_ignore_if_correct),
            )
        }

        val decimalPlaces = shares.substringAfter('.', "").length
        if (decimalPlaces > 4) {
            return ValidationResult(
                isValid = true,
                warningMessage = context.getString(R.string.validation_shares_decimal_places),
                suggestion = context.getString(R.string.validation_shares_auto_adjusted),
            )
        }

        return ValidationResult(isValid = true)
    }
}

/**
 * Facade for validating different parts of an asset form.
 */
object AssetFormValidator {
    fun validateCashForm(context: Context, amount: String, currency: String): ValidationResult {
        return CashAmountValidator.validate(context, amount, currency)
    }

    fun validateStockForm(context: Context, symbol: String, shares: String): ValidationResult {
        val symbolResult = StockSymbolValidator.validate(context, symbol)
        if (!symbolResult.isValid) {
            return symbolResult
        }

        val sharesResult = StockSharesValidator.validate(context, shares)
        if (!sharesResult.isValid) {
            return sharesResult
        }

        val warnings = listOfNotNull(symbolResult.warningMessage, sharesResult.warningMessage)
        val suggestions = listOfNotNull(symbolResult.suggestion, sharesResult.suggestion)

        return ValidationResult(
            isValid = true,
            warningMessage = if (warnings.isNotEmpty()) warnings.joinToString("; ") else null,
            suggestion = if (suggestions.isNotEmpty()) suggestions.joinToString("; ") else null,
        )
    }
}
