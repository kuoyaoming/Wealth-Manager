package com.wealthmanager.data.service

import com.wealthmanager.data.entity.ExchangeRate
import com.wealthmanager.data.entity.StockAsset
import com.wealthmanager.data.repository.AssetRepository
import com.wealthmanager.debug.DebugLogManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 多層快取管理系統
 */
@Singleton
class CacheManager @Inject constructor(
    private val assetRepository: AssetRepository,
    private val debugLogManager: DebugLogManager,
    private val smartCacheStrategy: SmartCacheStrategy
) {
    
    companion object {
        // Cache expiry time (ms)
        private const val STOCK_CACHE_EXPIRY_MS = 5 * 60 * 1000L // 5 minutes
        private const val EXCHANGE_RATE_CACHE_EXPIRY_MS = 60 * 60 * 1000L // 1 hour
        private const val STALE_DATA_THRESHOLD_MS = 24 * 60 * 60 * 1000L // 24 hours
    }
    
    // Memory cache
    private val memoryCache = mutableMapOf<String, CacheEntry<Any>>()
    
    /**
     * Cache entry
     */
    data class CacheEntry<T>(
        val data: T,
        val timestamp: Long,
        val isStale: Boolean = false
    ) {
        fun isExpired(expiryMs: Long): Boolean {
            return System.currentTimeMillis() - timestamp > expiryMs
        }
        
        fun isStaleData(): Boolean {
            return System.currentTimeMillis() - timestamp > STALE_DATA_THRESHOLD_MS
        }
    }
    
    /**
     * Cache level enum
     */
    enum class CacheLevel {
        MEMORY,     // Memory cache
        DATABASE,   // Database cache
        API         // API call
    }
    
    /**
     * 獲取股票價格（智能多層快取策略）
     */
    suspend fun getStockPrice(symbol: String): Flow<StockPriceResult> = flow {
        debugLogManager.log("CACHE", "開始獲取股票價格: $symbol")
        
        // First layer: Memory cache (using smart cache strategy)
        val memoryKey = "stock_$symbol"
        val memoryEntry = memoryCache[memoryKey] as? CacheEntry<StockAsset>
        
        if (memoryEntry != null) {
            val smartExpiryTime = smartCacheStrategy.getCacheExpiryTime(memoryKey)
            if (!memoryEntry.isExpired(smartExpiryTime)) {
                debugLogManager.log("CACHE", "使用智能記憶體快取: $symbol (策略: ${smartCacheStrategy.getCacheStats()})")
                emit(StockPriceResult.Success(memoryEntry.data, CacheLevel.MEMORY, false))
                return@flow
            }
        }
        
        // Second layer: Database cache
        var dbStock: StockAsset? = null
        try {
            dbStock = assetRepository.getStockAssetSync(symbol)
            if (dbStock != null && !isDataExpired(dbStock.lastUpdated, STOCK_CACHE_EXPIRY_MS)) {
                debugLogManager.log("CACHE", "使用資料庫快取: $symbol")
                
                // Update memory cache
                memoryCache[memoryKey] = CacheEntry(dbStock, dbStock.lastUpdated)
                
                emit(StockPriceResult.Success(dbStock, CacheLevel.DATABASE, false))
                return@flow
            }
        } catch (e: Exception) {
            debugLogManager.logWarning("CACHE", "資料庫快取讀取失敗: ${e.message}")
        }
        
        // Third layer: Fallback to stale data
        if (dbStock != null) {
            val isStale = isDataExpired(dbStock.lastUpdated, STOCK_CACHE_EXPIRY_MS)
            debugLogManager.logWarning("CACHE", "使用過期資料: $symbol (過期: $isStale)")
            emit(StockPriceResult.Success(dbStock, CacheLevel.DATABASE, isStale))
        } else {
            debugLogManager.logError("CACHE", "無可用資料: $symbol")
            emit(StockPriceResult.Failure("無可用的股票資料"))
        }
    }
    
    /**
     * 獲取匯率（多層快取策略）
     */
    suspend fun getExchangeRate(currencyPair: String): Flow<ExchangeRateResult> = flow {
        debugLogManager.log("CACHE", "開始獲取匯率: $currencyPair")
        
        // First layer: Memory cache
        val memoryKey = "exchange_$currencyPair"
        val memoryEntry = memoryCache[memoryKey] as? CacheEntry<ExchangeRate>
        
        if (memoryEntry != null && !memoryEntry.isExpired(EXCHANGE_RATE_CACHE_EXPIRY_MS)) {
            debugLogManager.log("CACHE", "使用記憶體快取: $currencyPair")
            emit(ExchangeRateResult.Success(memoryEntry.data, CacheLevel.MEMORY, false))
            return@flow
        }
        
        // Second layer: Database cache
        var dbRate: ExchangeRate? = null
        try {
            dbRate = assetRepository.getExchangeRateSync(currencyPair)
            if (dbRate != null && !isDataExpired(dbRate.lastUpdated, EXCHANGE_RATE_CACHE_EXPIRY_MS)) {
                debugLogManager.log("CACHE", "使用資料庫快取: $currencyPair")
                
                // Update memory cache
                memoryCache[memoryKey] = CacheEntry(dbRate, dbRate.lastUpdated)
                
                emit(ExchangeRateResult.Success(dbRate, CacheLevel.DATABASE, false))
                return@flow
            }
        } catch (e: Exception) {
            debugLogManager.logWarning("CACHE", "資料庫快取讀取失敗: ${e.message}")
        }
        
        // Third layer: Fallback to stale data
        if (dbRate != null) {
            val isStale = isDataExpired(dbRate.lastUpdated, EXCHANGE_RATE_CACHE_EXPIRY_MS)
            debugLogManager.logWarning("CACHE", "使用過期資料: $currencyPair (過期: $isStale)")
            emit(ExchangeRateResult.Success(dbRate, CacheLevel.DATABASE, isStale))
        } else {
            debugLogManager.logError("CACHE", "無可用匯率資料: $currencyPair")
            emit(ExchangeRateResult.Failure("無可用的匯率資料"))
        }
    }
    
    /**
     * 更新快取
     */
    suspend fun updateStockCache(stock: StockAsset) {
        val memoryKey = "stock_${stock.symbol}"
        memoryCache[memoryKey] = CacheEntry(stock, stock.lastUpdated)
        
        debugLogManager.log("CACHE", "更新股票快取: ${stock.symbol}")
    }
    
    suspend fun updateExchangeRateCache(exchangeRate: ExchangeRate) {
        val memoryKey = "exchange_${exchangeRate.currencyPair}"
        memoryCache[memoryKey] = CacheEntry(exchangeRate, exchangeRate.lastUpdated)
        
        debugLogManager.log("CACHE", "更新匯率快取: ${exchangeRate.currencyPair}")
    }
    
    /**
     * 清理過期快取
     */
    fun cleanupExpiredCache() {
        val currentTime = System.currentTimeMillis()
        val expiredKeys = memoryCache.filter { (_, entry) ->
            entry.isExpired(STOCK_CACHE_EXPIRY_MS)
        }.keys
        
        expiredKeys.forEach { key ->
            memoryCache.remove(key)
        }
        
        debugLogManager.log("CACHE", "清理過期快取: ${expiredKeys.size} 個項目")
    }
    
    /**
     * 檢查資料是否過期
     */
    private fun isDataExpired(lastUpdated: Long, expiryMs: Long): Boolean {
        return System.currentTimeMillis() - lastUpdated > expiryMs
    }
    
    /**
     * 獲取快取統計
     */
    fun getCacheStats(): CacheStats {
        val totalEntries = memoryCache.size
        val expiredEntries = memoryCache.count { (_, entry) ->
            entry.isExpired(STOCK_CACHE_EXPIRY_MS)
        }
        
        return CacheStats(
            totalEntries = totalEntries,
            expiredEntries = expiredEntries,
            memoryUsage = memoryCache.size * 100 // Estimate memory usage
        )
    }
}

/**
 * 股票價格結果
 */
sealed class StockPriceResult {
    data class Success(
        val stock: StockAsset,
        val cacheLevel: CacheManager.CacheLevel,
        val isStale: Boolean
    ) : StockPriceResult()
    
    data class Failure(val error: String) : StockPriceResult()
}

/**
 * 匯率結果
 */
sealed class ExchangeRateResult {
    data class Success(
        val exchangeRate: ExchangeRate,
        val cacheLevel: CacheManager.CacheLevel,
        val isStale: Boolean
    ) : ExchangeRateResult()
    
    data class Failure(val error: String) : ExchangeRateResult()
}

/**
 * 快取統計
 */
data class CacheStats(
    val totalEntries: Int,
    val expiredEntries: Int,
    val memoryUsage: Int
)
