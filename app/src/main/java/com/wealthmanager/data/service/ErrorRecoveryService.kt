package com.wealthmanager.data.service

import android.util.Log
import com.wealthmanager.debug.DebugLogManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Service for handling error recovery and resilience
 */
@Singleton
class ErrorRecoveryService @Inject constructor(
    private val debugLogManager: DebugLogManager
) {
    
    /**
     * Execute operation with retry mechanism and exponential backoff
     */
    suspend fun <T> executeWithRetry(
        operation: suspend () -> T,
        maxRetries: Int = 3,
        initialDelay: Long = 1000L,
        operationName: String = "Unknown Operation"
    ): Flow<Result<T>> = flow {
        
        var lastException: Exception? = null
        
        repeat(maxRetries) { attempt ->
            try {
                debugLogManager.log("ERROR_RECOVERY", "$operationName - Attempt ${attempt + 1}/$maxRetries")
                
                val result = operation()
                debugLogManager.log("ERROR_RECOVERY", "$operationName - Success on attempt ${attempt + 1}")
                emit(Result.success(result))
                return@flow
                
            } catch (e: Exception) {
                lastException = e
                debugLogManager.log("ERROR_RECOVERY", "$operationName - Attempt ${attempt + 1} failed: ${e.message}")
                
                if (attempt < maxRetries - 1) {
                    val delayTime = initialDelay * (1L shl attempt) // Exponential backoff
                    debugLogManager.log("ERROR_RECOVERY", "$operationName - Retrying in ${delayTime}ms")
                    delay(delayTime)
                }
            }
        }
        
        debugLogManager.log("ERROR_RECOVERY", "$operationName - All attempts failed")
        emit(Result.failure(lastException ?: Exception("All retry attempts failed")))
    }
    
    /**
     * Handle API rate limiting with intelligent backoff
     */
    suspend fun handleRateLimit(
        operation: suspend () -> Unit,
        retryAfter: Long = 60000L // 1 minute default
    ): Flow<Result<Unit>> = flow {
        
        try {
            operation()
            emit(Result.success(Unit))
        } catch (e: Exception) {
            if (e.message?.contains("rate limit", ignoreCase = true) == true ||
                e.message?.contains("too many requests", ignoreCase = true) == true) {
                
                debugLogManager.log("ERROR_RECOVERY", "Rate limit detected, backing off for ${retryAfter}ms")
                delay(retryAfter)
                
                try {
                    operation()
                    emit(Result.success(Unit))
                } catch (retryException: Exception) {
                    debugLogManager.log("ERROR_RECOVERY", "Retry after rate limit failed: ${retryException.message}")
                    emit(Result.failure(retryException))
                }
            } else {
                emit(Result.failure(e))
            }
        }
    }
    
    /**
     * Handle network connectivity issues
     */
    suspend fun handleNetworkError(
        operation: suspend () -> Unit,
        maxRetries: Int = 3
    ): Flow<Result<Unit>> = flow {
        
        var lastException: Exception? = null
        
        repeat(maxRetries) { attempt ->
            try {
                operation()
                emit(Result.success(Unit))
                return@flow
            } catch (e: Exception) {
                lastException = e
                
                if (isNetworkError(e)) {
                    debugLogManager.log("ERROR_RECOVERY", "Network error on attempt ${attempt + 1}: ${e.message}")
                    
                    if (attempt < maxRetries - 1) {
                        val delayTime = 2000L * (attempt + 1) // Linear backoff for network
                        debugLogManager.log("ERROR_RECOVERY", "Retrying network operation in ${delayTime}ms")
                        delay(delayTime)
                    }
                } else {
                    // Non-network error, don't retry
                    emit(Result.failure(e))
                    return@flow
                }
            }
        }
        
        debugLogManager.log("ERROR_RECOVERY", "Network operation failed after $maxRetries attempts")
        emit(Result.failure(lastException ?: Exception("Network operation failed")))
    }
    
    /**
     * Check if exception is network-related
     */
    private fun isNetworkError(exception: Exception): Boolean {
        val message = exception.message?.lowercase() ?: ""
        return message.contains("network") ||
                message.contains("connection") ||
                message.contains("timeout") ||
                message.contains("unreachable") ||
                message.contains("no route to host")
    }
    
    /**
     * Handle data validation errors gracefully
     */
    suspend fun handleDataValidationError(
        data: Any?,
        validator: (Any?) -> Boolean,
        fallbackValue: Any? = null
    ): Flow<Result<Any?>> = flow {
        
        try {
            if (validator(data)) {
                debugLogManager.log("ERROR_RECOVERY", "Data validation successful")
                emit(Result.success(data))
            } else {
                debugLogManager.log("ERROR_RECOVERY", "Data validation failed, using fallback")
                emit(Result.success(fallbackValue))
            }
        } catch (e: Exception) {
            debugLogManager.log("ERROR_RECOVERY", "Data validation error: ${e.message}")
            emit(Result.success(fallbackValue))
        }
    }
    
    /**
     * Circuit breaker pattern for failing services
     */
    class CircuitBreaker(
        private val failureThreshold: Int = 5,
        private val timeout: Long = 60000L, // 1 minute
        private val debugLogManager: DebugLogManager
    ) {
        private var failureCount = 0
        private var lastFailureTime = 0L
        private var state = CircuitState.CLOSED
        
        enum class CircuitState {
            CLOSED,    // Normal operation
            OPEN,      // Failing, blocking requests
            HALF_OPEN  // Testing if service is back
        }
        
        suspend fun <T> execute(operation: suspend () -> T): Flow<Result<T>> = flow {
            when (state) {
                CircuitState.OPEN -> {
                    if (System.currentTimeMillis() - lastFailureTime > timeout) {
                        state = CircuitState.HALF_OPEN
                        debugLogManager.log("CIRCUIT_BREAKER", "Circuit breaker half-open, testing service")
                    } else {
                        debugLogManager.log("CIRCUIT_BREAKER", "Circuit breaker open, blocking request")
                        emit(Result.failure(Exception("Circuit breaker open")))
                        return@flow
                    }
                }
                CircuitState.HALF_OPEN -> {
                    // Allow one request to test
                }
                CircuitState.CLOSED -> {
                    // Normal operation
                }
            }
            
            try {
                val result = operation()
                onSuccess()
                emit(Result.success(result))
            } catch (e: Exception) {
                onFailure()
                emit(Result.failure(e))
            }
        }
        
        private fun onSuccess() {
            failureCount = 0
            state = CircuitState.CLOSED
            debugLogManager.log("CIRCUIT_BREAKER", "Circuit breaker reset to closed")
        }
        
        private fun onFailure() {
            failureCount++
            lastFailureTime = System.currentTimeMillis()
            
            if (failureCount >= failureThreshold) {
                state = CircuitState.OPEN
                debugLogManager.log("CIRCUIT_BREAKER", "Circuit breaker opened after $failureCount failures")
            }
        }
    }
}
