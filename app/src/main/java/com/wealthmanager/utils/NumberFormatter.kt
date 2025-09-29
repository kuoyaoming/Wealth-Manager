package com.wealthmanager.utils

import java.text.DecimalFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NumberFormatter @Inject constructor() {
    
    companion object {
        // USD format: two decimal places
        private val USD_FORMAT = DecimalFormat("#,##0.00")
        
        // TWD format: integer
        private val TWD_FORMAT = DecimalFormat("#,##0")
        
        // Shares format: two decimal places
        private val SHARES_FORMAT = DecimalFormat("#,##0.00")
    }
    
    /**
     * Format USD price (two decimal places)
     */
    fun formatUsdPrice(price: Double): String {
        return USD_FORMAT.format(price)
    }
    
    /**
     * Format TWD price (integer)
     */
    fun formatTwdPrice(price: Double): String {
        return TWD_FORMAT.format(price.toInt())
    }
    
    /**
     * Format shares (two decimal places)
     */
    fun formatShares(shares: Double): String {
        return SHARES_FORMAT.format(shares)
    }
    
    /**
     * Format price by currency
     */
    fun formatPriceByCurrency(price: Double, currency: String): String {
        return when (currency) {
            "USD" -> formatUsdPrice(price)
            "TWD" -> formatTwdPrice(price)
            else -> formatUsdPrice(price) // Default to USD format
        }
    }
    
    /**
     * Parse formatted price to Double
     */
    fun parsePrice(formattedPrice: String): Double {
        return try {
            formattedPrice.replace(",", "").toDouble()
        } catch (e: Exception) {
            0.0
        }
    }
    
    /**
     * Parse formatted shares to Double
     */
    fun parseShares(formattedShares: String): Double {
        return try {
            formattedShares.replace(",", "").toDouble()
        } catch (e: Exception) {
            0.0
        }
    }
}
