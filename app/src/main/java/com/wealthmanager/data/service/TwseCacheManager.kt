package com.wealthmanager.data.service

import com.wealthmanager.data.api.TwseStockItem
import com.wealthmanager.debug.DebugLogManager
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

/**
 * TWSE API cache manager
 * Cache for STOCK_DAY_ALL endpoint to avoid duplicate calls
 */
@Singleton
class TwseCacheManager
    @Inject
    constructor(
        private val debugLogManager: DebugLogManager,
    ) {
        companion object {
            // TWSE API cache time: 15 minutes (within Taiwan stock trading hours)
            private const val TWSE_CACHE_EXPIRY_MS = 15 * 60 * 1000L
        }

        // Cached data
        private var cachedData: List<TwseStockItem>? = null
        private var cacheTimestamp: Long = 0
        private val cacheMutex = Mutex()

        /**
         * Get cached Taiwan stock data
         */
        suspend fun getCachedStockData(): List<TwseStockItem>? {
            return cacheMutex.withLock {
                if (isCacheValid()) {
                    debugLogManager.log("TWSE_CACHE", "Using cached TWSE data (age: ${getCacheAge()}ms)")
                    cachedData
                } else {
                    debugLogManager.log("TWSE_CACHE", "TWSE cache expired or empty")
                    null
                }
            }
        }

        /**
         * Update cached Taiwan stock data
         */
        suspend fun updateCachedStockData(data: List<TwseStockItem>) {
            cacheMutex.withLock {
                cachedData = data
                cacheTimestamp = System.currentTimeMillis()
                debugLogManager.log("TWSE_CACHE", "Updated TWSE cache with ${data.size} stocks")
            }
        }

        /**
         * Check if cache is valid
         */
        private fun isCacheValid(): Boolean {
            val currentTime = System.currentTimeMillis()
            val cacheAge = currentTime - cacheTimestamp
            return cachedData != null && cacheAge < TWSE_CACHE_EXPIRY_MS
        }

        /**
         * Get cache age
         */
        private fun getCacheAge(): Long {
            return System.currentTimeMillis() - cacheTimestamp
        }

        /**
         * Clear cache
         */
        suspend fun clearCache() {
            cacheMutex.withLock {
                cachedData = null
                cacheTimestamp = 0
                debugLogManager.log("TWSE_CACHE", "Cleared TWSE cache")
            }
        }

        /**
         * Get cache status
         */
        fun getCacheStatus(): String {
            return if (cachedData != null) {
                "Cached: ${cachedData!!.size} stocks, age: ${getCacheAge()}ms"
            } else {
                "No cache"
            }
        }
    }
