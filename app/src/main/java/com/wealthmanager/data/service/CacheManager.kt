package com.wealthmanager.data.service

import com.wealthmanager.data.repository.AssetRepository
import com.wealthmanager.debug.DebugLogManager
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages cache validation for various data types in the application.
 *
 * This manager centralizes the logic for determining if cached data, such as
 * stock prices or exchange rates, is still valid or needs to be refreshed.
 * It relies on the underlying database as the primary source of truth.
 */
@Singleton
class CacheManager @Inject constructor(
    private val assetRepository: AssetRepository,
    private val debugLogManager: DebugLogManager,
    private val smartCacheStrategy: SmartCacheStrategy,
) {

    companion object {
        // Default expiry times if not specified by smart strategy
        const val STOCK_CACHE_EXPIRY_MS = 5 * 60 * 1000L // 5 minutes
        const val EXCHANGE_RATE_CACHE_EXPIRY_MS = 60 * 60 * 1000L // 1 hour
    }

    /**
     * Checks if the cache for a specific data key is expired.
     *
     * @param key A unique identifier for the cached data (e.g., "stock_AAPL", "exchange_USD_TWD").
     * @param lastUpdated The timestamp of when the data was last updated.
     * @return `true` if the cache is expired, `false` otherwise.
     */
    fun isCacheExpired(key: String, lastUpdated: Long): Boolean {
        val expiryTime = smartCacheStrategy.getCacheExpiryTime(key)
        val isExpired = System.currentTimeMillis() - lastUpdated > expiryTime

        if (isExpired) {
            debugLogManager.log("CACHE", "Cache expired for key: $key (Expiry: ${expiryTime}ms)")
        } else {
            debugLogManager.log("CACHE", "Cache valid for key: $key (Expiry: ${expiryTime}ms)")
        }

        return isExpired
    }
}
