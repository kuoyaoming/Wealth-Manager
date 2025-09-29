package com.wealthmanager.data.service

import com.wealthmanager.debug.DebugLogManager
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 智能請求去重管理器
 * 防止重複的 API 請求，提升效能
 */
@Singleton
class RequestDeduplicationManager @Inject constructor(
    private val debugLogManager: DebugLogManager
) {
    
    companion object {
        // Request deduplication time window (ms)
        private const val DEDUPLICATION_WINDOW_MS = 30_000L // 30s
        // Maximum concurrent requests
        private const val MAX_CONCURRENT_REQUESTS = 3
    }
    
    // Ongoing requests
    private val ongoingRequests = ConcurrentHashMap<String, OngoingRequest>()
    // Request mutex
    private val requestMutex = Mutex()
    // Concurrent request counter
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
     * 請求結果
     */
    sealed class RequestResult<T> {
        data class Success<T>(val data: T) : RequestResult<T>()
        data class Failure<T>(val error: Throwable) : RequestResult<T>()
        data class Duplicate<T>(val requestId: String) : RequestResult<T>()
    }
    
    /**
     * 執行去重請求
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
                    debugLogManager.log("REQUEST_DEDUP", "重複請求被攔截: $requestKey (進行中: ${timeSinceStart}ms)")
                    return RequestResult.Duplicate(existingRequest.requestId)
                } else {
                    // Clean up expired requests
                    ongoingRequests.remove(requestKey)
                }
            }
            
            // Check concurrent request limit
            if (concurrentRequestCount >= MAX_CONCURRENT_REQUESTS) {
                debugLogManager.logWarning("REQUEST_DEDUP", "達到最大並發請求限制: $concurrentRequestCount")
                return RequestResult.Failure(Exception("Too many concurrent requests"))
            }
            
            // Record new request
            val newRequest = OngoingRequest(requestId, System.currentTimeMillis(), requestType)
            ongoingRequests[requestKey] = newRequest
            concurrentRequestCount++
            
            debugLogManager.log("REQUEST_DEDUP", "開始執行請求: $requestKey (ID: $requestId)")
            
            try {
                val result = operation()
                debugLogManager.log("REQUEST_DEDUP", "請求成功完成: $requestKey")
                RequestResult.Success(result)
            } catch (e: Exception) {
                debugLogManager.logError("請求失敗: $requestKey", e)
                RequestResult.Failure(e)
            } finally {
                // Clean up request records
                ongoingRequests.remove(requestKey)
                concurrentRequestCount--
                debugLogManager.log("REQUEST_DEDUP", "請求清理完成: $requestKey")
            }
        }
    }
    
    /**
     * 生成請求 ID
     */
    private fun generateRequestId(requestKey: String, requestType: String): String {
        return "${requestType}_${requestKey}_${System.currentTimeMillis()}"
    }
    
    /**
     * 獲取當前統計
     */
    fun getStats(): RequestStats {
        return RequestStats(
            ongoingRequests = ongoingRequests.size,
            concurrentRequests = concurrentRequestCount,
            maxConcurrentRequests = MAX_CONCURRENT_REQUESTS
        )
    }
    
    /**
     * 清理過期請求
     */
    suspend fun cleanupExpiredRequests() {
        val currentTime = System.currentTimeMillis()
        val expiredRequests = ongoingRequests.filter { (_, request) ->
            currentTime - request.startTime > DEDUPLICATION_WINDOW_MS
        }
        
        if (expiredRequests.isNotEmpty()) {
            debugLogManager.log("REQUEST_DEDUP", "清理過期請求: ${expiredRequests.size} 個")
            expiredRequests.keys.forEach { key ->
                ongoingRequests.remove(key)
                concurrentRequestCount--
            }
        }
    }
    
    /**
     * 請求統計
     */
    data class RequestStats(
        val ongoingRequests: Int,
        val concurrentRequests: Int,
        val maxConcurrentRequests: Int
    )
}
