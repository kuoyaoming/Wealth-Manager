package com.wealthmanager.data.service

import com.wealthmanager.debug.DebugLogManager
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages a dynamic caching strategy based on data access frequency.
 *
 * This strategy adjusts the cache expiry time for individual data items (identified by a key)
 * based on how frequently they are accessed. More frequently accessed items get a shorter
 * cache time to ensure data freshness, while less frequent items get a longer cache time
 * to reduce unnecessary API calls.
 */
@Singleton
class SmartCacheStrategy @Inject constructor(private val debugLogManager: DebugLogManager) {

    private val accessStats = ConcurrentHashMap<String, DataAccessStats>()

    private enum class CacheStrategy {
        AGGRESSIVE, // For high-frequency access
        NORMAL,     // For medium-frequency access
        CONSERVATIVE // For low-frequency access
    }

    private data class DataAccessStats(
        var accessCount: Int = 0,
        var lastAccessTime: Long = 0L,
        var averageInterval: Long = 0L,
        var cacheStrategy: CacheStrategy = CacheStrategy.NORMAL,
    ) {
        fun updateAccess() {
            val currentTime = System.currentTimeMillis()
            if (lastAccessTime > 0) {
                val interval = currentTime - lastAccessTime
                averageInterval = if (averageInterval == 0L) interval else (averageInterval + interval) / 2
            }
            accessCount++
            lastAccessTime = currentTime
            updateStrategy()
        }

        private fun updateStrategy() {
            cacheStrategy =
                when {
                    accessCount >= HIGH_FREQUENCY_THRESHOLD && averageInterval < 30_000L -> CacheStrategy.AGGRESSIVE
                    accessCount >= MEDIUM_FREQUENCY_THRESHOLD && averageInterval < 60_000L -> CacheStrategy.NORMAL
                    else -> CacheStrategy.CONSERVATIVE
                }
        }
    }

    /**
     * Determines the appropriate cache expiry time for a given data key.
     *
     * It tracks the access pattern for the key and dynamically returns an expiry time
     * based on the determined [CacheStrategy].
     *
     * @param key A unique identifier for the data item.
     * @return The cache expiry time in milliseconds.
     */
    fun getCacheExpiryTime(key: String): Long {
        val stats = accessStats.getOrPut(key) { DataAccessStats() }
        stats.updateAccess()

        val expiryTime = when (stats.cacheStrategy) {
            CacheStrategy.AGGRESSIVE -> AGGRESSIVE_CACHE_MS
            CacheStrategy.NORMAL -> NORMAL_CACHE_MS
            CacheStrategy.CONSERVATIVE -> CONSERVATIVE_CACHE_MS
        }

        debugLogManager.log("SMART_CACHE", "Cache strategy for '$key': ${stats.cacheStrategy} -> ${expiryTime}ms")
        return expiryTime
    }

    companion object {
        private const val AGGRESSIVE_CACHE_MS = 2 * 60 * 1000L   // 2 minutes
        private const val NORMAL_CACHE_MS = 5 * 60 * 1000L     // 5 minutes
        private const val CONSERVATIVE_CACHE_MS = 15 * 60 * 1000L // 15 minutes
        private const val HIGH_FREQUENCY_THRESHOLD = 5
        private const val MEDIUM_FREQUENCY_THRESHOLD = 2
    }
}
