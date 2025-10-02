package com.wealthmanager.data.service

import com.wealthmanager.data.entity.ExchangeRate
import com.wealthmanager.data.entity.StockAsset
import com.wealthmanager.debug.DebugLogManager
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Data validation service
 */
@Singleton
class DataValidator
    @Inject
    constructor(
        private val debugLogManager: DebugLogManager,
    ) {
        sealed class ValidationResult {
            object Valid : ValidationResult()

            data class Invalid(val reason: String) : ValidationResult()
        }

        fun validateStockData(stock: StockAsset): ValidationResult {
            return when {
                stock.symbol.isBlank() -> {
                    debugLogManager.logWarning("DATA_VALIDATION", "Stock symbol is empty")
                    ValidationResult.Invalid("Stock symbol cannot be empty")
                }
                stock.symbol.length < 1 || stock.symbol.length > 10 -> {
                    debugLogManager.logWarning("DATA_VALIDATION", "Stock symbol length abnormal: ${stock.symbol}")
                    ValidationResult.Invalid("Stock symbol length must be between 1-10 characters")
                }
                stock.currentPrice <= 0 -> {
                    debugLogManager.logWarning("DATA_VALIDATION", "Stock price abnormal: ${stock.currentPrice}")
                    ValidationResult.Invalid("Stock price must be greater than 0")
                }
                stock.currentPrice > 1000000 -> {
                    debugLogManager.logWarning("DATA_VALIDATION", "Stock price too high: ${stock.currentPrice}")
                    ValidationResult.Invalid("Stock price abnormally high, please check data")
                }
                stock.lastUpdated <= 0 -> {
                    debugLogManager.logWarning("DATA_VALIDATION", "Update time abnormal: ${stock.lastUpdated}")
                    ValidationResult.Invalid("Update time abnormal")
                }
                stock.lastUpdated > System.currentTimeMillis() + 60000 -> {
                    debugLogManager.logWarning("DATA_VALIDATION", "Update time in future: ${stock.lastUpdated}")
                    ValidationResult.Invalid("Update time cannot be in the future")
                }
                else -> {
                    debugLogManager.log("DATA_VALIDATION", "Stock data validation passed: ${stock.symbol}")
                    ValidationResult.Valid
                }
            }
        }

        /**
         * Validate exchange rate data
         */
        fun validateExchangeRateData(exchangeRate: ExchangeRate): ValidationResult {
            return when {
                exchangeRate.currencyPair.isBlank() -> {
                    debugLogManager.logWarning("DATA_VALIDATION", "Currency pair is empty")
                    ValidationResult.Invalid("Currency pair cannot be empty")
                }
                !exchangeRate.currencyPair.contains("_") -> {
                    debugLogManager.logWarning(
                        "DATA_VALIDATION",
                        "Currency pair format error: ${exchangeRate.currencyPair}",
                    )
                    ValidationResult.Invalid("Currency pair format must be 'FROM_TO'")
                }
                exchangeRate.rate <= 0 -> {
                    debugLogManager.logWarning("DATA_VALIDATION", "Exchange rate abnormal: ${exchangeRate.rate}")
                    ValidationResult.Invalid("Exchange rate must be greater than 0")
                }
                exchangeRate.rate > 1000 -> {
                    debugLogManager.logWarning("DATA_VALIDATION", "Exchange rate too high: ${exchangeRate.rate}")
                    ValidationResult.Invalid("Exchange rate abnormally high, please check data")
                }
                exchangeRate.lastUpdated <= 0 -> {
                    debugLogManager.logWarning("DATA_VALIDATION", "Update time abnormal: ${exchangeRate.lastUpdated}")
                    ValidationResult.Invalid("Update time abnormal")
                }
                exchangeRate.lastUpdated > System.currentTimeMillis() + 60000 -> {
                    debugLogManager.logWarning("DATA_VALIDATION", "Update time in future: ${exchangeRate.lastUpdated}")
                    ValidationResult.Invalid("Update time cannot be in the future")
                }
                else -> {
                    debugLogManager.log(
                        "DATA_VALIDATION",
                        "Exchange rate data validation passed: ${exchangeRate.currencyPair}",
                    )
                    ValidationResult.Valid
                }
            }
        }

        /**
         * Clean and standardize stock symbol
         */
        fun sanitizeStockSymbol(symbol: String): String {
            return symbol
                .trim()
                .uppercase()
                .replace(Regex("[^A-Z0-9.]"), "") // Only keep letters, numbers and dots
                .take(10) // Limit length
        }

        /**
         * Clean and standardize price data
         */
        fun sanitizePrice(price: String): Double? {
            return try {
                val cleanedPrice =
                    price
                        .replace(Regex("[^0-9.-]"), "") // Only keep numbers, dots and minus
                        .replace(",", "") // Remove commas
                        .trim()

                if (cleanedPrice.isBlank()) {
                    debugLogManager.logWarning("DATA_VALIDATION", "Price string is empty")
                    null
                } else {
                    val parsedPrice = cleanedPrice.toDouble()
                    if (parsedPrice.isNaN() || parsedPrice.isInfinite()) {
                        debugLogManager.logWarning("DATA_VALIDATION", "Price parsing failed: $price")
                        null
                    } else {
                        parsedPrice
                    }
                }
            } catch (e: Exception) {
                debugLogManager.logWarning("DATA_VALIDATION", "Price cleanup failed: $price - ${e.message}")
                null
            }
        }

        /**
         * Clean and standardize exchange rate data
         */
        fun sanitizeExchangeRate(rate: String): Double? {
            return try {
                val cleanedRate =
                    rate
                        .replace(Regex("[^0-9.-]"), "") // Only keep numbers, dots and minus
                        .replace(",", "") // Remove commas
                        .trim()

                if (cleanedRate.isBlank()) {
                    debugLogManager.logWarning("DATA_VALIDATION", "Exchange rate string is empty")
                    null
                } else {
                    val parsedRate = cleanedRate.toDouble()
                    if (parsedRate.isNaN() || parsedRate.isInfinite()) {
                        debugLogManager.logWarning("DATA_VALIDATION", "Exchange rate parsing failed: $rate")
                        null
                    } else {
                        parsedRate
                    }
                }
            } catch (e: Exception) {
                debugLogManager.logWarning("DATA_VALIDATION", "Exchange rate cleanup failed: $rate - ${e.message}")
                null
            }
        }

        /**
         * Validate API response data
         */
        fun validateApiResponse(
            response: String,
            dataType: String,
        ): ValidationResult {
            return when {
                response.isBlank() -> {
                    debugLogManager.logWarning("DATA_VALIDATION", "$dataType API response is empty")
                    ValidationResult.Invalid("API response is empty")
                }
                response.length > 100000 -> {
                    debugLogManager.logWarning(
                        "DATA_VALIDATION",
                        "$dataType API response too large: ${response.length}",
                    )
                    ValidationResult.Invalid("API response too large")
                }
                response.contains("\u0000") -> {
                    debugLogManager.logWarning("DATA_VALIDATION", "$dataType API response contains null characters")
                    ValidationResult.Invalid("API response contains invalid characters")
                }
                response.contains("error") && response.contains("message") -> {
                    debugLogManager.logWarning("DATA_VALIDATION", "$dataType API response contains error message")
                    ValidationResult.Invalid("API response contains error")
                }
                else -> {
                    debugLogManager.log("DATA_VALIDATION", "$dataType API response validation passed")
                    ValidationResult.Valid
                }
            }
        }

        /**
         * Check if data is stale
         */
        fun isDataStale(
            lastUpdated: Long,
            maxAgeMs: Long = 5 * 60 * 1000L,
        ): Boolean {
            val isStale = System.currentTimeMillis() - lastUpdated > maxAgeMs
            if (isStale) {
                debugLogManager.logWarning(
                    "DATA_VALIDATION",
                    "Data is stale: updated ${(System.currentTimeMillis() - lastUpdated) / 1000} seconds ago",
                )
            }
            return isStale
        }

        /**
         * Get data quality score
         */
        fun getDataQualityScore(stock: StockAsset): Int {
            var score = 100

            // Check data integrity
            if (stock.symbol.isBlank()) score -= 30
            if (stock.currentPrice <= 0) score -= 40
            if (stock.lastUpdated <= 0) score -= 20

            // Check data freshness
            val ageMinutes = (System.currentTimeMillis() - stock.lastUpdated) / (60 * 1000)
            when {
                ageMinutes > 60 -> score -= 20 // Over 1 hour
                ageMinutes > 30 -> score -= 10 // Over 30 minutes
                ageMinutes > 15 -> score -= 5 // Over 15 minutes
            }

            // Check data reasonableness
            if (stock.currentPrice > 10000) score -= 10 // Price abnormally high
            if (stock.currentPrice < 0.01) score -= 10 // Price abnormally low

            return maxOf(0, score)
        }

        /**
         * Log validation statistics
         */
        fun logValidationStats(
            validCount: Int,
            invalidCount: Int,
            dataType: String,
        ) {
            val total = validCount + invalidCount
            val validPercent = if (total > 0) (validCount.toFloat() / total * 100) else 0f

            debugLogManager.log(
                "DATA_VALIDATION",
                "$dataType validation stats: valid=$validCount, invalid=$invalidCount, validity rate=${validPercent.toInt()}%",
            )
        }
    }
