package com.wealthmanager.utils

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.wealthmanager.util.LanguageManager

/**
 * Centralized money formatting for UI display only.
 * Does not affect calculations or API models.
 */
object MoneyFormatter {

    enum class Style { CurrencySymbol, CurrencyCode, NumberOnly }
    enum class MoneyContext { CashAmount, StockPrice, Total, ChartLabel }

    private val defaultFractionDigits: Map<String, Int> = mapOf(
        "TWD" to 0,
        "USD" to 2
    )

    private val contextOverrides: Map<MoneyContext, Map<String, Int>> = mapOf(
        MoneyContext.CashAmount to mapOf(
            "TWD" to 0,
            "USD" to 2
        ),
        MoneyContext.StockPrice to mapOf(
            "TWD" to 2,
            "USD" to 2
        ),
        MoneyContext.Total to mapOf(
            "TWD" to 0
        ),
        MoneyContext.ChartLabel to mapOf(
            "TWD" to 0
        )
    )

    fun format(
        amount: BigDecimal,
        currencyCode: String,
        locale: Locale,
        style: Style = Style.CurrencySymbol,
        context: MoneyContext? = null,
        minFractionDigits: Int? = null,
        maxFractionDigits: Int? = null,
        roundingMode: RoundingMode = RoundingMode.HALF_UP,
        trimTrailingZeros: Boolean = true
    ): String {
        val fallback = defaultFractionDigits[currencyCode] ?: 2
        val policy = context?.let { contextOverrides[it]?.get(currencyCode) } ?: fallback
        val min = minFractionDigits ?: policy
        val max = maxFractionDigits ?: policy

        val rounded = amount.setScale(max, roundingMode)

        return when (style) {
            Style.CurrencySymbol -> {
                NumberFormat.getCurrencyInstance(locale).apply {
                    currency = Currency.getInstance(currencyCode)
                    minimumFractionDigits = min
                    maximumFractionDigits = max
                }.format(rounded)
            }
            Style.CurrencyCode -> {
                val numberOnly = NumberFormat.getNumberInstance(locale).apply {
                    minimumFractionDigits = min
                    maximumFractionDigits = max
                }.format(rounded)
                "${Currency.getInstance(currencyCode).currencyCode} $numberOnly"
            }
            Style.NumberOnly -> {
                val text = NumberFormat.getNumberInstance(locale).apply {
                    minimumFractionDigits = min
                    maximumFractionDigits = max
                }.format(rounded)
                if (trimTrailingZeros && max > 0) trimZeros(text, locale) else text
            }
        }
    }

    private fun trimZeros(text: String, locale: Locale): String {
        val dfs = (NumberFormat.getInstance(locale) as java.text.DecimalFormat).decimalFormatSymbols
        val sep = dfs.decimalSeparator
        // Remove trailing zeros after decimal separator, also remove separator if no decimals remain
        return if (text.contains(sep)) {
            text.replace(Regex("""\${sep}0+""") ) { match ->
                val withoutZeros = match.value.trimEnd('0')
                if (withoutZeros == "$sep") {
                    ""
                } else {
                    withoutZeros
                }
            }
        } else {
            text
        }
    }
}

@Composable
fun rememberMoneyText(
    amount: Double,
    currencyCode: String,
    style: MoneyFormatter.Style = MoneyFormatter.Style.CurrencySymbol,
    moneyContext: MoneyFormatter.MoneyContext? = null,
    minFractionDigits: Int? = null,
    maxFractionDigits: Int? = null
): String {
    val androidContext = LocalContext.current
    val locale = LanguageManager.getCurrentLocale(androidContext)
    return MoneyFormatter.format(
        amount = BigDecimal.valueOf(amount),
        currencyCode = currencyCode,
        locale = locale,
        style = style,
        context = moneyContext,
        minFractionDigits = minFractionDigits,
        maxFractionDigits = maxFractionDigits
    )
}


