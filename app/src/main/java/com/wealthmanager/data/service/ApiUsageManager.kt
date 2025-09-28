package com.wealthmanager.data.service

import com.wealthmanager.debug.DebugLogManager
import kotlinx.coroutines.delay
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Alpha Vantage API 使用管理器
 * 根據官方文檔限制：
 * - 免費版本：每分鐘最多5次請求，每天最多500次請求
 * - 付費版本：每分鐘最多120次請求，每天最多30,000次請求
 */
@Singleton
class ApiUsageManager @Inject constructor(
    private val debugLogManager: DebugLogManager
) {
    
    companion object {
        // Alpha Vantage 免費版本限制
        private const val FREE_TIER_REQUESTS_PER_MINUTE = 5
        private const val FREE_TIER_REQUESTS_PER_DAY = 500
        
        // 付費版本限制（如果升級）
        private const val PREMIUM_TIER_REQUESTS_PER_MINUTE = 120
        private const val PREMIUM_TIER_REQUESTS_PER_DAY = 30000
        
        // 請求間隔（毫秒）
        private const val MIN_REQUEST_INTERVAL_MS = 12000L // 12秒間隔，確保不超過每分鐘5次
        private const val PREMIUM_MIN_REQUEST_INTERVAL_MS = 500L // 付費版本500毫秒間隔
    }
    
    // 計數器
    private val requestsThisMinute = AtomicInteger(0)
    private val requestsToday = AtomicInteger(0)
    private val lastRequestTime = AtomicLong(0L)
    private val lastMinuteReset = AtomicLong(System.currentTimeMillis())
    private val lastDayReset = AtomicLong(System.currentTimeMillis())
    
    // API 版本（預設為免費版本）
    private var isPremiumTier = false
    
    /**
     * 設置API版本
     */
    fun setPremiumTier(isPremium: Boolean) {
        isPremiumTier = isPremium
        debugLogManager.log("API_USAGE", "API tier set to: ${if (isPremium) "Premium" else "Free"}")
    }
    
    /**
     * 檢查是否可以發送請求
     */
    suspend fun canMakeRequest(): Boolean {
        val currentTime = System.currentTimeMillis()
        
        // 檢查是否需要重置計數器
        resetCountersIfNeeded(currentTime)
        
        // 檢查每日限制
        if (requestsToday.get() >= getDailyLimit()) {
            debugLogManager.logWarning("Daily API limit reached: ${requestsToday.get()}/${getDailyLimit()}", "API_USAGE")
            return false
        }
        
        // 檢查每分鐘限制
        if (requestsThisMinute.get() >= getMinuteLimit()) {
            debugLogManager.logWarning("Minute API limit reached: ${requestsThisMinute.get()}/${getMinuteLimit()}", "API_USAGE")
            return false
        }
        
        // 檢查請求間隔
        val timeSinceLastRequest = currentTime - lastRequestTime.get()
        val minInterval = getMinRequestInterval()
        
        if (timeSinceLastRequest < minInterval) {
            val waitTime = minInterval - timeSinceLastRequest
            debugLogManager.log("API_USAGE", "Rate limiting: waiting ${waitTime}ms before next request")
            delay(waitTime)
        }
        
        return true
    }
    
    /**
     * 記錄API請求
     */
    fun recordRequest() {
        val currentTime = System.currentTimeMillis()
        lastRequestTime.set(currentTime)
        requestsThisMinute.incrementAndGet()
        requestsToday.incrementAndGet()
        
        debugLogManager.log("API_USAGE", "API request recorded. This minute: ${requestsThisMinute.get()}/${getMinuteLimit()}, Today: ${requestsToday.get()}/${getDailyLimit()}")
    }
    
    /**
     * 獲取剩餘請求次數
     */
    fun getRemainingRequestsToday(): Int {
        return maxOf(0, getDailyLimit() - requestsToday.get())
    }
    
    /**
     * 獲取剩餘請求次數（本分鐘）
     */
    fun getRemainingRequestsThisMinute(): Int {
        return maxOf(0, getMinuteLimit() - requestsThisMinute.get())
    }
    
    /**
     * 獲取使用統計
     */
    fun getUsageStats(): ApiUsageStats {
        return ApiUsageStats(
            requestsThisMinute = requestsThisMinute.get(),
            requestsToday = requestsToday.get(),
            minuteLimit = getMinuteLimit(),
            dailyLimit = getDailyLimit(),
            isPremiumTier = isPremiumTier,
            lastRequestTime = lastRequestTime.get()
        )
    }
    
    /**
     * 重置計數器（如果需要）
     */
    private fun resetCountersIfNeeded(currentTime: Long) {
        // 檢查是否需要重置每分鐘計數器
        if (currentTime - lastMinuteReset.get() >= 60000L) { // 60秒
            requestsThisMinute.set(0)
            lastMinuteReset.set(currentTime)
            debugLogManager.log("API_USAGE", "Minute counter reset")
        }
        
        // 檢查是否需要重置每日計數器
        if (currentTime - lastDayReset.get() >= 86400000L) { // 24小時
            requestsToday.set(0)
            lastDayReset.set(currentTime)
            debugLogManager.log("API_USAGE", "Daily counter reset")
        }
    }
    
    private fun getMinuteLimit(): Int {
        return if (isPremiumTier) PREMIUM_TIER_REQUESTS_PER_MINUTE else FREE_TIER_REQUESTS_PER_MINUTE
    }
    
    private fun getDailyLimit(): Int {
        return if (isPremiumTier) PREMIUM_TIER_REQUESTS_PER_DAY else FREE_TIER_REQUESTS_PER_DAY
    }
    
    private fun getMinRequestInterval(): Long {
        return if (isPremiumTier) PREMIUM_MIN_REQUEST_INTERVAL_MS else MIN_REQUEST_INTERVAL_MS
    }
}

/**
 * API使用統計數據
 */
data class ApiUsageStats(
    val requestsThisMinute: Int,
    val requestsToday: Int,
    val minuteLimit: Int,
    val dailyLimit: Int,
    val isPremiumTier: Boolean,
    val lastRequestTime: Long
) {
    val minuteUsagePercent: Float
        get() = (requestsThisMinute.toFloat() / minuteLimit) * 100f
    
    val dailyUsagePercent: Float
        get() = (requestsToday.toFloat() / dailyLimit) * 100f
    
    val isNearLimit: Boolean
        get() = minuteUsagePercent >= 80f || dailyUsagePercent >= 80f
    
    val isAtLimit: Boolean
        get() = requestsThisMinute >= minuteLimit || requestsToday >= dailyLimit
}