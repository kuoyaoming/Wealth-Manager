package com.wealthmanager.data.service

import com.wealthmanager.debug.DebugLogManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 智能快取策略管理器
 * 根據數據類型和訪問模式動態調整快取策略
 */
@Singleton
class SmartCacheStrategy @Inject constructor(
    private val debugLogManager: DebugLogManager
) {
    
    companion object {
        // Cache strategy types
        private const val AGGRESSIVE_CACHE_MS = 2 * 60 * 1000L // 2 minutes
        private const val NORMAL_CACHE_MS = 5 * 60 * 1000L // 5 minutes
        private const val CONSERVATIVE_CACHE_MS = 15 * 60 * 1000L // 15 minutes
        
        // Access frequency thresholds
        private const val HIGH_FREQUENCY_THRESHOLD = 5 // 5 times/minute
        private const val MEDIUM_FREQUENCY_THRESHOLD = 2 // 2 times/minute
    }
    
    // Data access statistics
    private val accessStats = ConcurrentHashMap<String, DataAccessStats>()
    
    /**
     * Data access statistics
     */
    data class DataAccessStats(
        val key: String,
        var accessCount: Int = 0,
        var lastAccessTime: Long = 0L,
        var averageInterval: Long = 0L,
        var cacheStrategy: CacheStrategy = CacheStrategy.NORMAL
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
            cacheStrategy = when {
                accessCount >= HIGH_FREQUENCY_THRESHOLD && averageInterval < 30_000L -> CacheStrategy.AGGRESSIVE
                accessCount >= MEDIUM_FREQUENCY_THRESHOLD && averageInterval < 60_000L -> CacheStrategy.NORMAL
                else -> CacheStrategy.CONSERVATIVE
            }
        }
    }
    
    /**
     * 快取策略枚舉
     */
    enum class CacheStrategy {
        AGGRESSIVE,  // Aggressive cache - high frequency access
        NORMAL,      // Normal cache - medium frequency
        CONSERVATIVE // Conservative cache - low frequency access
    }
    
    /**
     * 獲取智能快取時間
     */
    fun getCacheExpiryTime(key: String): Long {
        val stats = accessStats.getOrPut(key) { DataAccessStats(key) }
        stats.updateAccess()
        
        val expiryTime = when (stats.cacheStrategy) {
            CacheStrategy.AGGRESSIVE -> AGGRESSIVE_CACHE_MS
            CacheStrategy.NORMAL -> NORMAL_CACHE_MS
            CacheStrategy.CONSERVATIVE -> CONSERVATIVE_CACHE_MS
        }
        
        debugLogManager.log("SMART_CACHE", "快取策略: $key -> ${stats.cacheStrategy} (${expiryTime}ms)")
        return expiryTime
    }
    
    /**
     * 檢查是否應該使用快取
     */
    fun shouldUseCache(key: String, lastUpdateTime: Long): Boolean {
        val stats = accessStats[key] ?: return true
        val currentTime = System.currentTimeMillis()
        val timeSinceUpdate = currentTime - lastUpdateTime
        val cacheExpiry = getCacheExpiryTime(key)
        
        val shouldCache = timeSinceUpdate < cacheExpiry
        
        if (!shouldCache) {
            debugLogManager.log("SMART_CACHE", "快取過期: $key (${timeSinceUpdate}ms > ${cacheExpiry}ms)")
        }
        
        return shouldCache
    }
    
    /**
     * 預測數據需求
     */
    fun predictDataNeed(key: String): Flow<Boolean> = flow {
        val stats = accessStats[key] ?: return@flow emit(false)
        
        // Predict if preloading is needed based on access patterns
        val shouldPreload = when {
            stats.accessCount >= HIGH_FREQUENCY_THRESHOLD -> true
            stats.averageInterval < 60_000L && stats.accessCount >= 2 -> true
            else -> false
        }
        
        debugLogManager.log("SMART_CACHE", "預測數據需求: $key -> $shouldPreload")
        emit(shouldPreload)
    }
    
    /**
     * 獲取快取統計
     */
    fun getCacheStats(): CacheStats {
        val totalKeys = accessStats.size
        val aggressiveCount = accessStats.values.count { it.cacheStrategy == CacheStrategy.AGGRESSIVE }
        val normalCount = accessStats.values.count { it.cacheStrategy == CacheStrategy.NORMAL }
        val conservativeCount = accessStats.values.count { it.cacheStrategy == CacheStrategy.CONSERVATIVE }
        
        return CacheStats(
            totalKeys = totalKeys,
            aggressiveStrategy = aggressiveCount,
            normalStrategy = normalCount,
            conservativeStrategy = conservativeCount
        )
    }
    
    /**
     * 清理舊的統計數據
     */
    fun cleanupOldStats() {
        val currentTime = System.currentTimeMillis()
        val oldThreshold = 24 * 60 * 60 * 1000L // 24 hours
        
        val oldStats = accessStats.filter { (_, stats) ->
            currentTime - stats.lastAccessTime > oldThreshold
        }
        
        if (oldStats.isNotEmpty()) {
            debugLogManager.log("SMART_CACHE", "清理舊統計數據: ${oldStats.size} 個")
            oldStats.keys.forEach { key ->
                accessStats.remove(key)
            }
        }
    }
    
    /**
     * 快取統計
     */
    data class CacheStats(
        val totalKeys: Int,
        val aggressiveStrategy: Int,
        val normalStrategy: Int,
        val conservativeStrategy: Int
    )
}
