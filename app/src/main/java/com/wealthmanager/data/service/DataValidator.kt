package com.wealthmanager.data.service

import com.wealthmanager.data.entity.ExchangeRate
import com.wealthmanager.data.entity.StockAsset
import com.wealthmanager.debug.DebugLogManager
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 資料驗證和清理服務
 */
@Singleton
class DataValidator @Inject constructor(
    private val debugLogManager: DebugLogManager
) {
    
    /**
     * 驗證結果
     */
    sealed class ValidationResult {
        object Valid : ValidationResult()
        data class Invalid(val reason: String) : ValidationResult()
    }
    
    /**
     * 驗證股票資料
     */
    fun validateStockData(stock: StockAsset): ValidationResult {
        return when {
            stock.symbol.isBlank() -> {
                debugLogManager.logWarning("DATA_VALIDATION", "股票代碼為空")
                ValidationResult.Invalid("股票代碼不能為空")
            }
            stock.symbol.length < 1 || stock.symbol.length > 10 -> {
                debugLogManager.logWarning("DATA_VALIDATION", "股票代碼長度異常: ${stock.symbol}")
                ValidationResult.Invalid("股票代碼長度必須在1-10個字元之間")
            }
            stock.currentPrice <= 0 -> {
                debugLogManager.logWarning("DATA_VALIDATION", "股票價格異常: ${stock.currentPrice}")
                ValidationResult.Invalid("股票價格必須大於0")
            }
            stock.currentPrice > 1000000 -> {
                debugLogManager.logWarning("DATA_VALIDATION", "股票價格過高: ${stock.currentPrice}")
                ValidationResult.Invalid("股票價格異常高，請檢查資料")
            }
            stock.lastUpdated <= 0 -> {
                debugLogManager.logWarning("DATA_VALIDATION", "更新時間異常: ${stock.lastUpdated}")
                ValidationResult.Invalid("更新時間異常")
            }
            stock.lastUpdated > System.currentTimeMillis() + 60000 -> {
                debugLogManager.logWarning("DATA_VALIDATION", "更新時間在未來: ${stock.lastUpdated}")
                ValidationResult.Invalid("更新時間不能是未來時間")
            }
            else -> {
                debugLogManager.log("DATA_VALIDATION", "股票資料驗證通過: ${stock.symbol}")
                ValidationResult.Valid
            }
        }
    }
    
    /**
     * 驗證匯率資料
     */
    fun validateExchangeRateData(exchangeRate: ExchangeRate): ValidationResult {
        return when {
            exchangeRate.currencyPair.isBlank() -> {
                debugLogManager.logWarning("DATA_VALIDATION", "貨幣對為空")
                ValidationResult.Invalid("貨幣對不能為空")
            }
            !exchangeRate.currencyPair.contains("_") -> {
                debugLogManager.logWarning("DATA_VALIDATION", "貨幣對格式錯誤: ${exchangeRate.currencyPair}")
                ValidationResult.Invalid("貨幣對格式必須為 'FROM_TO'")
            }
            exchangeRate.rate <= 0 -> {
                debugLogManager.logWarning("DATA_VALIDATION", "匯率異常: ${exchangeRate.rate}")
                ValidationResult.Invalid("匯率必須大於0")
            }
            exchangeRate.rate > 1000 -> {
                debugLogManager.logWarning("DATA_VALIDATION", "匯率過高: ${exchangeRate.rate}")
                ValidationResult.Invalid("匯率異常高，請檢查資料")
            }
            exchangeRate.lastUpdated <= 0 -> {
                debugLogManager.logWarning("DATA_VALIDATION", "更新時間異常: ${exchangeRate.lastUpdated}")
                ValidationResult.Invalid("更新時間異常")
            }
            exchangeRate.lastUpdated > System.currentTimeMillis() + 60000 -> {
                debugLogManager.logWarning("DATA_VALIDATION", "更新時間在未來: ${exchangeRate.lastUpdated}")
                ValidationResult.Invalid("更新時間不能是未來時間")
            }
            else -> {
                debugLogManager.log("DATA_VALIDATION", "匯率資料驗證通過: ${exchangeRate.currencyPair}")
                ValidationResult.Valid
            }
        }
    }
    
    /**
     * 清理和標準化股票代碼
     */
    fun sanitizeStockSymbol(symbol: String): String {
        return symbol
            .trim()
            .uppercase()
            .replace(Regex("[^A-Z0-9.]"), "") // Only keep letters, numbers and dots
            .take(10) // Limit length
    }
    
    /**
     * 清理和標準化價格資料
     */
    fun sanitizePrice(price: String): Double? {
        return try {
            val cleanedPrice = price
                .replace(Regex("[^0-9.-]"), "") // Only keep numbers, dots and minus
                .replace(",", "") // Remove commas
                .trim()
            
            if (cleanedPrice.isBlank()) {
                debugLogManager.logWarning("DATA_VALIDATION", "價格字串為空")
                null
            } else {
                val parsedPrice = cleanedPrice.toDouble()
                if (parsedPrice.isNaN() || parsedPrice.isInfinite()) {
                    debugLogManager.logWarning("DATA_VALIDATION", "價格解析失敗: $price")
                    null
                } else {
                    parsedPrice
                }
            }
        } catch (e: Exception) {
            debugLogManager.logWarning("DATA_VALIDATION", "價格清理失敗: $price - ${e.message}")
            null
        }
    }
    
    /**
     * 清理和標準化匯率資料
     */
    fun sanitizeExchangeRate(rate: String): Double? {
        return try {
            val cleanedRate = rate
                .replace(Regex("[^0-9.-]"), "") // Only keep numbers, dots and minus
                .replace(",", "") // Remove commas
                .trim()
            
            if (cleanedRate.isBlank()) {
                debugLogManager.logWarning("DATA_VALIDATION", "匯率字串為空")
                null
            } else {
                val parsedRate = cleanedRate.toDouble()
                if (parsedRate.isNaN() || parsedRate.isInfinite()) {
                    debugLogManager.logWarning("DATA_VALIDATION", "匯率解析失敗: $rate")
                    null
                } else {
                    parsedRate
                }
            }
        } catch (e: Exception) {
            debugLogManager.logWarning("DATA_VALIDATION", "匯率清理失敗: $rate - ${e.message}")
            null
        }
    }
    
    /**
     * 驗證 API 回應資料
     */
    fun validateApiResponse(response: String, dataType: String): ValidationResult {
        return when {
            response.isBlank() -> {
                debugLogManager.logWarning("DATA_VALIDATION", "$dataType API 回應為空")
                ValidationResult.Invalid("API 回應為空")
            }
            response.length > 100000 -> {
                debugLogManager.logWarning("DATA_VALIDATION", "$dataType API 回應過大: ${response.length}")
                ValidationResult.Invalid("API 回應過大")
            }
            response.contains("\u0000") -> {
                debugLogManager.logWarning("DATA_VALIDATION", "$dataType API 回應包含 null 字元")
                ValidationResult.Invalid("API 回應包含無效字元")
            }
            response.contains("error") && response.contains("message") -> {
                debugLogManager.logWarning("DATA_VALIDATION", "$dataType API 回應包含錯誤訊息")
                ValidationResult.Invalid("API 回應包含錯誤")
            }
            else -> {
                debugLogManager.log("DATA_VALIDATION", "$dataType API 回應驗證通過")
                ValidationResult.Valid
            }
        }
    }
    
    /**
     * 檢查資料是否過期
     */
    fun isDataStale(lastUpdated: Long, maxAgeMs: Long = 5 * 60 * 1000L): Boolean {
        val isStale = System.currentTimeMillis() - lastUpdated > maxAgeMs
        if (isStale) {
            debugLogManager.logWarning("DATA_VALIDATION", "資料已過期: ${(System.currentTimeMillis() - lastUpdated) / 1000}秒前更新")
        }
        return isStale
    }
    
    /**
     * 獲取資料品質評分
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
            ageMinutes > 15 -> score -= 5  // Over 15 minutes
        }
        
        // Check data reasonableness
        if (stock.currentPrice > 10000) score -= 10 // Price abnormally high
        if (stock.currentPrice < 0.01) score -= 10  // Price abnormally low
        
        return maxOf(0, score)
    }
    
    /**
     * 記錄驗證統計
     */
    fun logValidationStats(validCount: Int, invalidCount: Int, dataType: String) {
        val total = validCount + invalidCount
        val validPercent = if (total > 0) (validCount.toFloat() / total * 100) else 0f
        
        debugLogManager.log("DATA_VALIDATION", "$dataType 驗證統計: 有效=$validCount, 無效=$invalidCount, 有效率=${validPercent.toInt()}%")
    }
}
