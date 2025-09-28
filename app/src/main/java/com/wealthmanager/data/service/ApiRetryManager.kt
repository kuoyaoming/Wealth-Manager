package com.wealthmanager.data.service

import com.wealthmanager.debug.DebugLogManager
import kotlinx.coroutines.delay
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiRetryManager @Inject constructor(
    private val debugLogManager: DebugLogManager
) {
    
    companion object {
        private const val MAX_RETRIES = 3
        private const val BASE_DELAY_MS = 1000L
        private const val MAX_DELAY_MS = 10000L
    }
    
    suspend fun <T> executeWithRetry(
        operation: suspend () -> T,
        operationName: String = "API Operation"
    ): Result<T> {
        var lastException: Exception? = null
        
        for (attempt in 1..MAX_RETRIES) {
            try {
                debugLogManager.log("API_RETRY", "$operationName - Attempt $attempt/$MAX_RETRIES")
                val result = operation()
                debugLogManager.log("API_RETRY", "$operationName - Success on attempt $attempt")
                return Result.success(result)
            } catch (e: Exception) {
                lastException = e
                val shouldRetry = shouldRetryException(e, attempt)
                
                if (e is HttpException) {
                    val code = e.code()
                    debugLogManager.logWarning("API_RETRY", "$operationName - HTTP $code on attempt $attempt: ${e.message}")
                } else {
                    debugLogManager.log("API_RETRY", "$operationName - Attempt $attempt failed: ${e.message}")
                }
                
                if (!shouldRetry || attempt == MAX_RETRIES) {
                    debugLogManager.logError("$operationName - Final failure after $attempt attempts", e)
                    break
                }
                
                val delayMs = calculateDelay(attempt)
                debugLogManager.log("API_RETRY", "$operationName - Retrying in ${delayMs}ms")
                delay(delayMs)
            }
        }
        
        return Result.failure(lastException ?: Exception("Unknown error"))
    }
    
    private fun shouldRetryException(e: Exception, attempt: Int): Boolean {
        return when (e) {
            is HttpException -> {
                val code = e.code()
                debugLogManager.log("API_RETRY", "HTTP Exception: $code")
                // Retry on 5xx server errors, 429 rate limit, and 408 timeout
                code in 500..599 || code == 429 || code == 408
            }
            is IOException -> {
                debugLogManager.log("API_RETRY", "Network IOException: ${e.message}")
                // Retry on network errors
                true
            }
            else -> {
                debugLogManager.log("API_RETRY", "Other exception: ${e::class.simpleName}")
                // Don't retry on other exceptions
                false
            }
        }
    }
    
    private fun calculateDelay(attempt: Int): Long {
        val exponentialDelay = BASE_DELAY_MS * (1L shl (attempt - 1))
        return minOf(exponentialDelay, MAX_DELAY_MS)
    }
}