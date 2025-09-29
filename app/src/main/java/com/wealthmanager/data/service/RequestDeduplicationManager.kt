package com.wealthmanager.data.service

import com.wealthmanager.debug.DebugLogManager
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Request deduplication manager
 */
@Singleton
class RequestDeduplicationManager @Inject constructor(
    private val debugLogManager: DebugLogManager
) {
    
    companion object {
        private const val DEDUPLICATION_WINDOW_MS = 30_000L
        private const val MAX_CONCURRENT_REQUESTS = 3
    }
    
    private val ongoingRequests = ConcurrentHashMap<String, OngoingRequest>()
    private val requestMutex = Mutex()
    private var concurrentRequestCount = 0
    
    /**
     * Ongoing request
     */
    data class OngoingRequest(
        val requestId: String,
        val startTime: Long,
        val requestType: String
    )
    
    /**
     * Request result
     */
    sealed class RequestResult<T> {
        data class Success<T>(val data: T) : RequestResult<T>()
        data class Failure<T>(val error: Throwable) : RequestResult<T>()
        data class Duplicate<T>(val requestId: String) : RequestResult<T>()
    }
    
    /**
     * Execute deduplicated request
     */
    suspend fun <T> executeDeduplicatedRequest(
        requestKey: String,
        requestType: String,
        operation: suspend () -> T
    ): RequestResult<T> {
        val requestId = generateRequestId(requestKey, requestType)
        
        return requestMutex.withLock {
            // Check if same request is already in progress
            val existingRequest = ongoingRequests[requestKey]
            if (existingRequest != null) {
                val timeSinceStart = System.currentTimeMillis() - existingRequest.startTime
                if (timeSinceStart < DEDUPLICATION_WINDOW_MS) {
                    debugLogManager.log("REQUEST_DEDUP", "Duplicate request intercepted: $requestKey (in progress: ${timeSinceStart}ms)")
                    return RequestResult.Duplicate(existingRequest.requestId)
                } else {
                    // Clean up expired requests
                    ongoingRequests.remove(requestKey)
                }
            }
            
            // Check concurrent request limit
            if (concurrentRequestCount >= MAX_CONCURRENT_REQUESTS) {
                debugLogManager.logWarning("REQUEST_DEDUP", "Reached maximum concurrent request limit: $concurrentRequestCount")
                return RequestResult.Failure(Exception("Too many concurrent requests"))
            }
            
            // Record new request
            val newRequest = OngoingRequest(requestId, System.currentTimeMillis(), requestType)
            ongoingRequests[requestKey] = newRequest
            concurrentRequestCount++
            
            debugLogManager.log("REQUEST_DEDUP", "Starting request execution: $requestKey (ID: $requestId)")
            
            try {
                val result = operation()
                debugLogManager.log("REQUEST_DEDUP", "Request completed successfully: $requestKey")
                RequestResult.Success(result)
            } catch (e: Exception) {
                debugLogManager.logError("Request failed: $requestKey", e)
                RequestResult.Failure(e)
            } finally {
                // Clean up request records
                ongoingRequests.remove(requestKey)
                concurrentRequestCount--
                debugLogManager.log("REQUEST_DEDUP", "Request cleanup completed: $requestKey")
            }
        }
    }
    
    /**
     * Generate request ID
     */
    private fun generateRequestId(requestKey: String, requestType: String): String {
        return "${requestType}_${requestKey}_${System.currentTimeMillis()}"
    }
    
    /**
     * Get current statistics
     */
    fun getStats(): RequestStats {
        return RequestStats(
            ongoingRequests = ongoingRequests.size,
            concurrentRequests = concurrentRequestCount,
            maxConcurrentRequests = MAX_CONCURRENT_REQUESTS
        )
    }
    
    /**
     * Clean up expired requests
     */
    suspend fun cleanupExpiredRequests() {
        val currentTime = System.currentTimeMillis()
        val expiredRequests = ongoingRequests.filter { (_, request) ->
            currentTime - request.startTime > DEDUPLICATION_WINDOW_MS
        }
        
        if (expiredRequests.isNotEmpty()) {
            debugLogManager.log("REQUEST_DEDUP", "Clean up expired requests: ${expiredRequests.size} items")
            expiredRequests.keys.forEach { key ->
                ongoingRequests.remove(key)
                concurrentRequestCount--
            }
        }
    }
    
    /**
     * Request statistics
     */
    data class RequestStats(
        val ongoingRequests: Int,
        val concurrentRequests: Int,
        val maxConcurrentRequests: Int
    )
}
