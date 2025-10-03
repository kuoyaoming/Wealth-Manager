package com.wealthmanager.ui.assets

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.platform.LocalContext
import com.wealthmanager.R

/**
 * 資產表單驗證結果
 */
@Immutable
data class ValidationResult(
    val isValid: Boolean,
    val errorMessage: String? = null,
    val warningMessage: String? = null,
    val suggestion: String? = null,
)

/**
 * 現金金額驗證器
 */
object CashAmountValidator {
    /**
     * 驗證現金金額
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
                    if (currency == "TWD") {
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

        // 檢查金額是否過大（可能是輸入錯誤）
        val maxReasonableAmount = if (currency == "TWD") 1_000_000_000.0 else 100_000_000.0
        if (numericAmount > maxReasonableAmount) {
            return ValidationResult(
                isValid = true,
                warningMessage = context.getString(R.string.validation_amount_too_large),
                suggestion = context.getString(R.string.validation_amount_ignore_if_correct),
            )
        }

        // 檢查小數位數（只有當有小數點時才檢查）
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
 * 股票代碼驗證器
 */
object StockSymbolValidator {
    /**
     * 驗證股票代碼
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

        // 檢查是否包含無效字符
        if (!trimmedSymbol.matches(Regex("^[A-Z0-9.]{1,10}$"))) {
            return ValidationResult(
                isValid = false,
                errorMessage = context.getString(R.string.validation_symbol_invalid_format),
                suggestion = context.getString(R.string.validation_symbol_only_alphanumeric),
            )
        }

        // 檢查是否為台灣股票格式
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

        // 檢查是否為美股格式
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
 * 股票股數驗證器
 */
object StockSharesValidator {
    /**
     * 驗證股票股數
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

        // 檢查股數是否過大
        if (numericShares > 1_000_000) {
            return ValidationResult(
                isValid = true,
                warningMessage = context.getString(R.string.validation_shares_too_large),
                suggestion = context.getString(R.string.validation_shares_ignore_if_correct),
            )
        }

        // 檢查小數位數（只有當有小數點時才檢查）
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
 * 表單整體驗證器
 */
object AssetFormValidator {
    /**
     * 驗證現金表單
     */
    @Composable
    fun validateCashForm(
        amount: String,
        currency: String,
    ): ValidationResult {
        return CashAmountValidator.validate(amount, currency)
    }

    /**
     * 驗證股票表單
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

        // 如果都有警告，合併警告信息
        val warnings = listOfNotNull(symbolResult.warningMessage, sharesResult.warningMessage)
        val suggestions = listOfNotNull(symbolResult.suggestion, sharesResult.suggestion)

        return ValidationResult(
            isValid = true,
            warningMessage = if (warnings.isNotEmpty()) warnings.joinToString("; ") else null,
            suggestion = if (suggestions.isNotEmpty()) suggestions.joinToString("; ") else null,
        )
    }
}
