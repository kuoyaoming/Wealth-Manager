package com.wealthmanager.data.service

import com.wealthmanager.debug.DebugLogManager
import kotlinx.coroutines.delay
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Alpha Vantage API Usage Manager
 * Based on official documentation limits:
 * - Free tier: Maximum 5 requests per minute, 500 requests per day
 * - Premium tier: Maximum 120 requests per minute, 30,000 requests per day
 */
@Singleton
class ApiUsageManager @Inject constructor(
    private val debugLogManager: DebugLogManager
) {

    companion object {
        // Alpha Vantage free tier limits
        private const val FREE_TIER_REQUESTS_PER_MINUTE = 5
        private const val FREE_TIER_REQUESTS_PER_DAY = 500

        // Premium tier limits (if upgraded)
        private const val PREMIUM_TIER_REQUESTS_PER_MINUTE = 120
        private const val PREMIUM_TIER_REQUESTS_PER_DAY = 30000

        // Request intervals (milliseconds)
        private const val MIN_REQUEST_INTERVAL_MS = 12000L // 12 second interval to ensure not exceeding 5 per minute
        private const val PREMIUM_MIN_REQUEST_INTERVAL_MS = 500L // Premium tier 500ms interval
    }

    // Counters
    private val requestsThisMinute = AtomicInteger(0)
    private val requestsToday = AtomicInteger(0)
    private val lastRequestTime = AtomicLong(0L)
    private val lastMinuteReset = AtomicLong(System.currentTimeMillis())
    private val lastDayReset = AtomicLong(System.currentTimeMillis())

    // API tier (default to free tier)
    private var isPremiumTier = false

    /**
     * Set API tier
     */
    fun setPremiumTier(isPremium: Boolean) {
        isPremiumTier = isPremium
        debugLogManager.log("API_USAGE", "API tier set to: ${if (isPremium) "Premium" else "Free"}")
    }

    /**
     * Check if request can be made
     */
    suspend fun canMakeRequest(): Boolean {
        val currentTime = System.currentTimeMillis()

        // Check if counters need to be reset
        resetCountersIfNeeded(currentTime)

        // Check daily limit
        if (requestsToday.get() >= getDailyLimit()) {
            debugLogManager.logWarning("Daily API limit reached: ${requestsToday.get()}/${getDailyLimit()}", "API_USAGE")
            return false
        }

        // Check minute limit
        if (requestsThisMinute.get() >= getMinuteLimit()) {
            debugLogManager.logWarning("Minute API limit reached: ${requestsThisMinute.get()}/${getMinuteLimit()}", "API_USAGE")
            return false
        }

        // Check request interval
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
     * Record API request
     */
    fun recordRequest() {
        val currentTime = System.currentTimeMillis()
        lastRequestTime.set(currentTime)
        requestsThisMinute.incrementAndGet()
        requestsToday.incrementAndGet()

        debugLogManager.log("API_USAGE", "API request recorded. This minute: ${requestsThisMinute.get()}/${getMinuteLimit()}, Today: ${requestsToday.get()}/${getDailyLimit()}")
    }

    /**
     * Get remaining requests today
     */
    fun getRemainingRequestsToday(): Int {
        return maxOf(0, getDailyLimit() - requestsToday.get())
    }

    /**
     * Get remaining requests this minute
     */
    fun getRemainingRequestsThisMinute(): Int {
        return maxOf(0, getMinuteLimit() - requestsThisMinute.get())
    }

    /**
     * Get usage statistics
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
     * Reset counters if needed
     */
    private fun resetCountersIfNeeded(currentTime: Long) {
        // Check if minute counter needs to be reset
        if (currentTime - lastMinuteReset.get() >= 60000L) { // 60 seconds
            requestsThisMinute.set(0)
            lastMinuteReset.set(currentTime)
            debugLogManager.log("API_USAGE", "Minute counter reset")
        }

        // Check if daily counter needs to be reset
        if (currentTime - lastDayReset.get() >= 86400000L) { // 24 hours
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
 * API usage statistics data
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
