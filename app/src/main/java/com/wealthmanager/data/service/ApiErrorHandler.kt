package com.wealthmanager.data.service

import com.wealthmanager.debug.DebugLogManager
import retrofit2.HttpException
import java.io.IOException
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 專門處理 API 錯誤的分類和恢復策略
 */
@Singleton
class ApiErrorHandler @Inject constructor(
    private val debugLogManager: DebugLogManager
) {
    
    /**
     * API 錯誤類型枚舉
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
     * 錯誤恢復策略
     */
    data class ErrorRecoveryStrategy(
        val shouldRetry: Boolean,
        val retryDelayMs: Long,
        val maxRetries: Int,
        val fallbackAction: String
    )
    
    /**
     * 分析錯誤並返回錯誤類型
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
     * 根據錯誤類型獲取恢復策略
     */
    fun getRecoveryStrategy(errorType: ApiErrorType): ErrorRecoveryStrategy {
        return when (errorType) {
            ApiErrorType.NetworkError -> ErrorRecoveryStrategy(
                shouldRetry = true,
                retryDelayMs = 2000L,
                maxRetries = 3,
                fallbackAction = "使用快取資料"
            )
            ApiErrorType.RateLimitError -> ErrorRecoveryStrategy(
                shouldRetry = true,
                retryDelayMs = 60000L, // 1 minute
                maxRetries = 2,
                fallbackAction = "使用快取資料並延遲下次更新"
            )
            ApiErrorType.ServerError -> ErrorRecoveryStrategy(
                shouldRetry = true,
                retryDelayMs = 5000L,
                maxRetries = 3,
                fallbackAction = "使用快取資料"
            )
            ApiErrorType.InvalidApiCall -> ErrorRecoveryStrategy(
                shouldRetry = false,
                retryDelayMs = 0L,
                maxRetries = 0,
                fallbackAction = "檢查 API 金鑰和參數"
            )
            ApiErrorType.DataValidationError -> ErrorRecoveryStrategy(
                shouldRetry = false,
                retryDelayMs = 0L,
                maxRetries = 0,
                fallbackAction = "使用快取資料"
            )
            ApiErrorType.UnknownError -> ErrorRecoveryStrategy(
                shouldRetry = true,
                retryDelayMs = 3000L,
                maxRetries = 2,
                fallbackAction = "使用快取資料"
            )
        }
    }
    
    /**
     * 獲取使用者友善的錯誤訊息
     */
    fun getUserFriendlyMessage(errorType: ApiErrorType): String {
        return when (errorType) {
            ApiErrorType.NetworkError -> "網路連線不穩定，請檢查網路設定"
            ApiErrorType.RateLimitError -> "請求過於頻繁，請稍後再試"
            ApiErrorType.ServerError -> "伺服器暫時無法使用，請稍後再試"
            ApiErrorType.InvalidApiCall -> "API 設定有誤，請聯繫技術支援"
            ApiErrorType.DataValidationError -> "資料格式異常，使用快取資料"
            ApiErrorType.UnknownError -> "發生未知錯誤，請重新啟動應用程式"
        }
    }
    
    /**
     * 記錄錯誤統計
     */
    fun logErrorStats(errorType: ApiErrorType, operation: String, duration: Long) {
        val stats = mapOf(
            "error_type" to errorType::class.simpleName,
            "operation" to operation,
            "duration_ms" to duration,
            "timestamp" to System.currentTimeMillis()
        )
        
        debugLogManager.log("API_ERROR_STATS", "Error occurred: $stats")
    }
}
