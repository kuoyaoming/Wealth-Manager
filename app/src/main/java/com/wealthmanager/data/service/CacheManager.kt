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
 * Multi-layer cache management system
 */
@Singleton
class CacheManager
    @Inject
    constructor(
        private val assetRepository: AssetRepository,
        private val debugLogManager: DebugLogManager,
        private val smartCacheStrategy: SmartCacheStrategy,
    ) {
        companion object {
            private const val STOCK_CACHE_EXPIRY_MS = 5 * 60 * 1000L
            private const val EXCHANGE_RATE_CACHE_EXPIRY_MS = 60 * 60 * 1000L
            private const val STALE_DATA_THRESHOLD_MS = 24 * 60 * 60 * 1000L
        }

        private val memoryCache = mutableMapOf<String, CacheEntry<Any>>()

        data class CacheEntry<T>(
            val data: T,
            val timestamp: Long,
            val isStale: Boolean = false,
        ) {
            fun isExpired(expiryMs: Long): Boolean {
                return System.currentTimeMillis() - timestamp > expiryMs
            }

            fun isStaleData(): Boolean {
                return System.currentTimeMillis() - timestamp > STALE_DATA_THRESHOLD_MS
            }
        }

        enum class CacheLevel {
            MEMORY,
            DATABASE,
            API,
        }

        suspend fun getStockPrice(symbol: String): Flow<StockPriceResult> =
            flow {
                debugLogManager.log("CACHE", "Starting to get stock price: $symbol")

                val memoryKey = "stock_$symbol"
                val memoryEntry =
                    memoryCache[memoryKey]?.let { entry ->
                        if (entry.data is StockAsset) {
                            @Suppress("UNCHECKED_CAST")
                            entry as CacheEntry<StockAsset>
                        } else {
                            null
                        }
                    }

                if (memoryEntry != null) {
                    val smartExpiryTime = smartCacheStrategy.getCacheExpiryTime(memoryKey)
                    if (!memoryEntry.isExpired(smartExpiryTime)) {
                        debugLogManager.log("CACHE", "Using smart memory cache: $symbol")
                        emit(StockPriceResult.Success(memoryEntry.data, CacheLevel.MEMORY, false))
                        return@flow
                    }
                }
                var dbStock: StockAsset? = null
                try {
                    dbStock = assetRepository.getStockAssetSync(symbol)
                    if (dbStock != null && !isDataExpired(dbStock.lastUpdated, STOCK_CACHE_EXPIRY_MS)) {
                        debugLogManager.log("CACHE", "Using database cache: $symbol")
                        memoryCache[memoryKey] = CacheEntry(dbStock, dbStock.lastUpdated)
                        emit(StockPriceResult.Success(dbStock, CacheLevel.DATABASE, false))
                        return@flow
                    }
                } catch (e: Exception) {
                    debugLogManager.logWarning("CACHE", "Database cache read failed: ${e.message}")
                }
                if (dbStock != null) {
                    val isStale = isDataExpired(dbStock.lastUpdated, STOCK_CACHE_EXPIRY_MS)
                    debugLogManager.logWarning("CACHE", "Using stale data: $symbol (stale: $isStale)")
                    emit(StockPriceResult.Success(dbStock, CacheLevel.DATABASE, isStale))
                } else {
                    debugLogManager.logError("CACHE", "No available data: $symbol")
                    emit(StockPriceResult.Failure("No available stock data"))
                }
            }

        suspend fun getExchangeRate(currencyPair: String): Flow<ExchangeRateResult> =
            flow {
                debugLogManager.log("CACHE", "Starting to get exchange rate: $currencyPair")

                val memoryKey = "exchange_$currencyPair"
                val memoryEntry =
                    memoryCache[memoryKey]?.let { entry ->
                        if (entry.data is ExchangeRate) {
                            @Suppress("UNCHECKED_CAST")
                            entry as CacheEntry<ExchangeRate>
                        } else {
                            null
                        }
                    }

                if (memoryEntry != null && !memoryEntry.isExpired(EXCHANGE_RATE_CACHE_EXPIRY_MS)) {
                    debugLogManager.log("CACHE", "Using memory cache: $currencyPair")
                    emit(ExchangeRateResult.Success(memoryEntry.data, CacheLevel.MEMORY, false))
                    return@flow
                }
                var dbRate: ExchangeRate? = null
                try {
                    dbRate = assetRepository.getExchangeRateSync(currencyPair)
                    if (dbRate != null && !isDataExpired(dbRate.lastUpdated, EXCHANGE_RATE_CACHE_EXPIRY_MS)) {
                        debugLogManager.log("CACHE", "Using database cache: $currencyPair")
                        memoryCache[memoryKey] = CacheEntry(dbRate, dbRate.lastUpdated)
                        emit(ExchangeRateResult.Success(dbRate, CacheLevel.DATABASE, false))
                        return@flow
                    }
                } catch (e: Exception) {
                    debugLogManager.logWarning("CACHE", "Database cache read failed: ${e.message}")
                }
                if (dbRate != null) {
                    val isStale = isDataExpired(dbRate.lastUpdated, EXCHANGE_RATE_CACHE_EXPIRY_MS)
                    debugLogManager.logWarning("CACHE", "Using stale data: $currencyPair (stale: $isStale)")
                    emit(ExchangeRateResult.Success(dbRate, CacheLevel.DATABASE, isStale))
                } else {
                    debugLogManager.logError("CACHE", "No available exchange rate data: $currencyPair")
                    emit(ExchangeRateResult.Failure("No available exchange rate data"))
                }
            }

        suspend fun updateStockCache(stock: StockAsset) {
            val memoryKey = "stock_${stock.symbol}"
            memoryCache[memoryKey] = CacheEntry(stock, stock.lastUpdated)

            debugLogManager.log("CACHE", "Update stock cache: ${stock.symbol}")
        }

        suspend fun updateExchangeRateCache(exchangeRate: ExchangeRate) {
            val memoryKey = "exchange_${exchangeRate.currencyPair}"
            memoryCache[memoryKey] = CacheEntry(exchangeRate, exchangeRate.lastUpdated)

            debugLogManager.log("CACHE", "Update exchange rate cache: ${exchangeRate.currencyPair}")
        }

        fun cleanupExpiredCache() {
            // val currentTime = System.currentTimeMillis()
            val expiredKeys =
                memoryCache.filter { (_, entry) ->
                    entry.isExpired(STOCK_CACHE_EXPIRY_MS)
                }.keys

            expiredKeys.forEach { key ->
                memoryCache.remove(key)
            }

            debugLogManager.log("CACHE", "Clean up expired cache: ${expiredKeys.size} items")
        }

        private fun isDataExpired(
            lastUpdated: Long,
            expiryMs: Long,
        ): Boolean {
            return System.currentTimeMillis() - lastUpdated > expiryMs
        }

        fun getCacheStats(): CacheStats {
            val totalEntries = memoryCache.size
            val expiredEntries =
                memoryCache.count { (_, entry) ->
                    entry.isExpired(STOCK_CACHE_EXPIRY_MS)
                }

            return CacheStats(
                totalEntries = totalEntries,
                expiredEntries = expiredEntries,
                memoryUsage = memoryCache.size * 100,
            )
        }
    }

sealed class StockPriceResult {
    data class Success(
        val stock: StockAsset,
        val cacheLevel: CacheManager.CacheLevel,
        val isStale: Boolean,
    ) : StockPriceResult()

    data class Failure(val error: String) : StockPriceResult()
}

sealed class ExchangeRateResult {
    data class Success(
        val exchangeRate: ExchangeRate,
        val cacheLevel: CacheManager.CacheLevel,
        val isStale: Boolean,
    ) : ExchangeRateResult()

    data class Failure(val error: String) : ExchangeRateResult()
}

data class CacheStats(
    val totalEntries: Int,
    val expiredEntries: Int,
    val memoryUsage: Int,
)
