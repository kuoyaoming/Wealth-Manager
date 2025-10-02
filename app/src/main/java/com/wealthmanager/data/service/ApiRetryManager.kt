package com.wealthmanager.data.service

import com.wealthmanager.debug.DebugLogManager
import kotlinx.coroutines.delay
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiRetryManager @Inject constructor(
    private val debugLogManager: DebugLogManager,
    private val apiErrorHandler: ApiErrorHandler
) {

    companion object {
        private const val MAX_RETRIES = 3
        private const val BASE_DELAY_MS = 1000L
        private const val MAX_DELAY_MS = 10000L
        private const val RATE_LIMIT_DELAY_MS = 60000L // 1 minute
    }

    suspend fun <T> executeWithRetry(
        operation: suspend () -> T,
        operationName: String = "API Operation"
    ): Result<T> {
        var lastException: Exception? = null
        val startTime = System.currentTimeMillis()

        for (attempt in 1..MAX_RETRIES) {
            try {
                debugLogManager.log("API_RETRY", "$operationName - Attempt $attempt/$MAX_RETRIES")
                val result = operation()
                val duration = System.currentTimeMillis() - startTime
                debugLogManager.log("API_RETRY", "$operationName - Success on attempt $attempt in ${duration}ms")
                return Result.success(result)
            } catch (e: Exception) {
                lastException = e
                val errorType = apiErrorHandler.analyzeError(e)
                val strategy = apiErrorHandler.getRecoveryStrategy(errorType)

                // Record error statistics
                val duration = System.currentTimeMillis() - startTime
                apiErrorHandler.logErrorStats(errorType, operationName, duration)

                if (e is HttpException) {
                    val code = e.code()
                    debugLogManager.logWarning("API_RETRY", "$operationName - HTTP $code on attempt $attempt: ${e.message}")
                } else {
                    debugLogManager.log("API_RETRY", "$operationName - Attempt $attempt failed: ${e.message}")
                }

                // Check if should retry
                if (!strategy.shouldRetry || attempt >= strategy.maxRetries) {
                    debugLogManager.logError("$operationName - Final failure after $attempt attempts. Strategy: ${strategy.fallbackAction}", e)
                    break
                }

                // Use smart delay
                val delayMs = calculateSmartDelay(attempt, errorType, strategy)
                debugLogManager.log("API_RETRY", "$operationName - Retrying in ${delayMs}ms (Error: $errorType)")
                delay(delayMs)
            }
        }

        return Result.failure(lastException ?: Exception("Unknown error"))
    }

    /**
     * Smart delay calculation
     */
    private fun calculateSmartDelay(
        attempt: Int,
        errorType: ApiErrorHandler.ApiErrorType,
        strategy: ApiErrorHandler.ErrorRecoveryStrategy
    ): Long {
        return when (errorType) {
            ApiErrorHandler.ApiErrorType.RateLimitError -> {
                // Rate limit uses fixed delay
                RATE_LIMIT_DELAY_MS
            }
            ApiErrorHandler.ApiErrorType.NetworkError -> {
                // Network error uses linear delay
                strategy.retryDelayMs * attempt
            }
            ApiErrorHandler.ApiErrorType.ServerError -> {
                // Server error uses exponential backoff
                val exponentialDelay = BASE_DELAY_MS * (1L shl (attempt - 1))
                minOf(exponentialDelay, MAX_DELAY_MS)
            }
            else -> {
                // Other errors use strategy-defined delay
                strategy.retryDelayMs
            }
        }
    }

    /**
     * Execute operation with fallback strategy
     */
    suspend fun <T> executeWithFallback(
        operation: suspend () -> T,
        fallbackOperation: suspend () -> T,
        operationName: String = "API Operation with Fallback"
    ): Result<T> {
        val result = executeWithRetry(operation, operationName)

        if (result.isSuccess) {
            return result
        }

        debugLogManager.logWarning("API_RETRY", "$operationName - Primary operation failed, trying fallback")

        return try {
            val fallbackResult = fallbackOperation()
            debugLogManager.log("API_RETRY", "$operationName - Fallback operation succeeded")
            Result.success(fallbackResult)
        } catch (e: Exception) {
            debugLogManager.logError("$operationName - Fallback operation also failed", e)
            Result.failure(e)
        }
    }
}
