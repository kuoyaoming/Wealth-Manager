package com.wealthmanager.data.service

import com.wealthmanager.debug.DebugLogManager
import retrofit2.HttpException
import java.io.IOException
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Specialized API error classification and recovery strategy handler
 */
@Singleton
class ApiErrorHandler
    @Inject
    constructor(
        private val debugLogManager: DebugLogManager,
    ) {
        /**
         * API error type enumeration
         */
        sealed class ApiErrorType {
            object NetworkError : ApiErrorType()

            object RateLimitError : ApiErrorType()

            object ServerError : ApiErrorType()

            object InvalidApiCall : ApiErrorType()

            object DataValidationError : ApiErrorType()

            object UnknownError : ApiErrorType()
        }

        /**
         * Error recovery strategy
         */
        data class ErrorRecoveryStrategy(
            val shouldRetry: Boolean,
            val retryDelayMs: Long,
            val maxRetries: Int,
            val fallbackAction: String,
        )

        /**
         * Analyze error and return error type
         */
        fun analyzeError(exception: Exception): ApiErrorType {
            return when (exception) {
                is HttpException -> {
                    when (exception.code()) {
                        429 -> {
                            debugLogManager.logWarning("API_ERROR", "Rate limit exceeded: ${exception.code()}")
                            ApiErrorType.RateLimitError
                        }
                        in 500..599 -> {
                            debugLogManager.logWarning("API_ERROR", "Server error: ${exception.code()}")
                            ApiErrorType.ServerError
                        }
                        400, 401, 403 -> {
                            debugLogManager.logWarning("API_ERROR", "Invalid API call: ${exception.code()}")
                            ApiErrorType.InvalidApiCall
                        }
                        else -> {
                            debugLogManager.logWarning("API_ERROR", "HTTP error: ${exception.code()}")
                            ApiErrorType.UnknownError
                        }
                    }
                }
                is IOException, is UnknownHostException -> {
                    debugLogManager.logWarning("API_ERROR", "Network error: ${exception.message}")
                    ApiErrorType.NetworkError
                }
                else -> {
                    val message = exception.message?.lowercase() ?: ""
                    when {
                        message.contains("no valid") || message.contains("invalid data") -> {
                            debugLogManager.logWarning("API_ERROR", "Data validation error: ${exception.message}")
                            ApiErrorType.DataValidationError
                        }
                        message.contains("rate limit") || message.contains("too many requests") -> {
                            debugLogManager.logWarning("API_ERROR", "Rate limit in message: ${exception.message}")
                            ApiErrorType.RateLimitError
                        }
                        else -> {
                            debugLogManager.logWarning("API_ERROR", "Unknown error: ${exception.message}")
                            ApiErrorType.UnknownError
                        }
                    }
                }
            }
        }

        /**
         * Get recovery strategy based on error type
         */
        fun getRecoveryStrategy(errorType: ApiErrorType): ErrorRecoveryStrategy {
            return when (errorType) {
                ApiErrorType.NetworkError ->
                    ErrorRecoveryStrategy(
                        shouldRetry = true,
                        retryDelayMs = 2000L,
                        maxRetries = 3,
                        fallbackAction = "Use cached data",
                    )
                ApiErrorType.RateLimitError ->
                    ErrorRecoveryStrategy(
                        shouldRetry = true,
                        // 1 minute
                        retryDelayMs = 60000L,
                        maxRetries = 2,
                        fallbackAction = "Use cached data and delay next update",
                    )
                ApiErrorType.ServerError ->
                    ErrorRecoveryStrategy(
                        shouldRetry = true,
                        retryDelayMs = 5000L,
                        maxRetries = 3,
                        fallbackAction = "Use cached data",
                    )
                ApiErrorType.InvalidApiCall ->
                    ErrorRecoveryStrategy(
                        shouldRetry = false,
                        retryDelayMs = 0L,
                        maxRetries = 0,
                        fallbackAction = "Check API key and parameters",
                    )
                ApiErrorType.DataValidationError ->
                    ErrorRecoveryStrategy(
                        shouldRetry = false,
                        retryDelayMs = 0L,
                        maxRetries = 0,
                        fallbackAction = "Use cached data",
                    )
                ApiErrorType.UnknownError ->
                    ErrorRecoveryStrategy(
                        shouldRetry = true,
                        retryDelayMs = 3000L,
                        maxRetries = 2,
                        fallbackAction = "Use cached data",
                    )
            }
        }

        /**
         * Get user-friendly error message
         */
        fun getUserFriendlyMessage(errorType: ApiErrorType): String {
            return when (errorType) {
                ApiErrorType.NetworkError -> "Network connection unstable, please check network settings"
                ApiErrorType.RateLimitError -> "Too many requests, please try again later"
                ApiErrorType.ServerError -> "Server temporarily unavailable, please try again later"
                ApiErrorType.InvalidApiCall -> "API configuration error, please contact technical support"
                ApiErrorType.DataValidationError -> "Data format abnormal, using cached data"
                ApiErrorType.UnknownError -> "Unknown error occurred, please restart the application"
            }
        }

        /**
         * Log error statistics
         */
        fun logErrorStats(
            errorType: ApiErrorType,
            operation: String,
            duration: Long,
        ) {
            val stats =
                mapOf(
                    "error_type" to errorType::class.simpleName,
                    "operation" to operation,
                    "duration_ms" to duration,
                    "timestamp" to System.currentTimeMillis(),
                )

            debugLogManager.log("API_ERROR_STATS", "Error occurred: $stats")
        }
    }
