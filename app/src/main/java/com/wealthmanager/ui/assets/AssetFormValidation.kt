package com.wealthmanager.ui.assets

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.platform.LocalContext
import com.wealthmanager.R

/**
 * Asset form validation result.
 */
@Immutable
data class ValidationResult(
    val isValid: Boolean,
    val errorMessage: String? = null,
    val warningMessage: String? = null,
    val suggestion: String? = null,
)

/**
 * Cash amount validator.
 */
object CashAmountValidator {
    /**
     * Validate cash amount.
     */
    @Composable
    fun validate(
        amount: String,
        currency: String,
    ): ValidationResult {
        val context = LocalContext.current

        if (amount.isEmpty()) {
            return ValidationResult(
                isValid = false,
                errorMessage = context.getString(R.string.validation_amount_required),
                suggestion =
                    if (currency == context.getString(R.string.assets_currency_twd)) {
                        context.getString(R.string.validation_amount_example_twd)
                    } else {
                        context.getString(R.string.validation_amount_example_usd)
                    },
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

        val maxReasonableAmount = if (currency == context.getString(R.string.assets_currency_twd)) 1_000_000_000.0 else 100_000_000.0
        if (numericAmount > maxReasonableAmount) {
            return ValidationResult(
                isValid = true,
                warningMessage = context.getString(R.string.validation_amount_too_large),
                suggestion = context.getString(R.string.validation_amount_ignore_if_correct),
            )
        }

        if (amount.contains(".")) {
            val decimalPlaces = amount.substringAfter(".").length
            if (decimalPlaces > 2) {
                return ValidationResult(
                    isValid = true,
                    warningMessage = context.getString(R.string.validation_amount_decimal_places),
                    suggestion = context.getString(R.string.validation_amount_auto_adjusted),
                )
            }
        }

        return ValidationResult(isValid = true)
    }
}

/**
 * Stock symbol validator.
 */
object StockSymbolValidator {
    /**
     * Validate stock symbol.
     */
    @Composable
    fun validate(symbol: String): ValidationResult {
        val context = LocalContext.current

        if (symbol.isEmpty()) {
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

        val isTaiwanStock =
            trimmedSymbol.matches(Regex("^\\d{4}$")) ||
                trimmedSymbol.endsWith(".TW") ||
                trimmedSymbol.endsWith(".T")

        if (isTaiwanStock) {
            return ValidationResult(
                isValid = true,
                suggestion = context.getString(R.string.validation_symbol_taiwan_stock),
            )
        }

        if (trimmedSymbol.matches(Regex("^[A-Z]{1,5}$"))) {
            return ValidationResult(
                isValid = true,
                suggestion = context.getString(R.string.validation_symbol_us_stock),
            )
        }

        return ValidationResult(
            isValid = true,
            warningMessage = context.getString(R.string.validation_symbol_confirm_correct),
            suggestion = context.getString(R.string.validation_symbol_search_confirm),
        )
    }
}

/**
 * Stock shares validator.
 */
object StockSharesValidator {
    /**
     * Validate stock shares.
     */
    @Composable
    fun validate(shares: String): ValidationResult {
        val context = LocalContext.current

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

        if (shares.contains(".")) {
            val decimalPlaces = shares.substringAfter(".").length
            if (decimalPlaces > 4) {
                return ValidationResult(
                    isValid = true,
                    warningMessage = context.getString(R.string.validation_shares_decimal_places),
                    suggestion = context.getString(R.string.validation_shares_auto_adjusted),
                )
            }
        }

        return ValidationResult(isValid = true)
    }
}

/**
 * Form validation utility.
 */
object AssetFormValidator {
    /**
     * Validate cash form.
     */
    @Composable
    fun validateCashForm(
        amount: String,
        currency: String,
    ): ValidationResult {
        return CashAmountValidator.validate(amount, currency)
    }

    /**
     * Validate stock form.
     */
    @Composable
    fun validateStockForm(
        symbol: String,
        shares: String,
    ): ValidationResult {
        val symbolResult = StockSymbolValidator.validate(symbol)
        if (!symbolResult.isValid) {
            return symbolResult
        }

        val sharesResult = StockSharesValidator.validate(shares)
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
