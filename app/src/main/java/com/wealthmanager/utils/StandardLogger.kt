package com.wealthmanager.utils

import android.util.Log
import com.wealthmanager.BuildConfig
import java.text.SimpleDateFormat
import java.util.*

/**
 * 標準化日誌工具類
 * 遵循 Android 設計指南的最佳實踐
 * 
 * 日誌級別說明：
 * - VERBOSE: 最詳細的調試信息，僅用於開發階段
 * - DEBUG: 一般調試信息，有助於了解應用運行狀態
 * - INFO: 基本信息，顯示應用狀態或功能
 * - WARN: 警告信息，可能表明潛在問題
 * - ERROR: 錯誤信息，處理嚴重問題
 */
object StandardLogger {
    
    // 統一的 TAG 前綴
    private const val TAG_PREFIX = "WealthManager"
    
    // 時間格式
    private val dateFormat = SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault())
    
    /**
     * 記錄 VERBOSE 級別日誌
     * 僅在 DEBUG 模式下輸出
     */
    fun verbose(tag: String, message: String, throwable: Throwable? = null) {
        if (!BuildConfig.DEBUG) return
        
        val formattedTag = formatTag(tag)
        val formattedMessage = formatMessage(tag, message)
        
        if (throwable != null) {
            Log.v(formattedTag, formattedMessage, throwable)
        } else {
            Log.v(formattedTag, formattedMessage)
        }
    }
    
    /**
     * 記錄 DEBUG 級別日誌
     * 僅在 DEBUG 模式下輸出
     */
    fun debug(tag: String, message: String, throwable: Throwable? = null) {
        if (!BuildConfig.DEBUG) return
        
        val formattedTag = formatTag(tag)
        val formattedMessage = formatMessage(tag, message)
        
        if (throwable != null) {
            Log.d(formattedTag, formattedMessage, throwable)
        } else {
            Log.d(formattedTag, formattedMessage)
        }
    }
    
    /**
     * 記錄 INFO 級別日誌
     * 在 DEBUG 和 RELEASE 模式下都輸出
     */
    fun info(tag: String, message: String, throwable: Throwable? = null) {
        val formattedTag = formatTag(tag)
        val formattedMessage = formatMessage(tag, message)
        
        if (throwable != null) {
            Log.i(formattedTag, formattedMessage, throwable)
        } else {
            Log.i(formattedTag, formattedMessage)
        }
    }
    
    /**
     * 記錄 WARN 級別日誌
     * 在 DEBUG 和 RELEASE 模式下都輸出
     */
    fun warn(tag: String, message: String, throwable: Throwable? = null) {
        val formattedTag = formatTag(tag)
        val formattedMessage = formatMessage(tag, message)
        
        if (throwable != null) {
            Log.w(formattedTag, formattedMessage, throwable)
        } else {
            Log.w(formattedTag, formattedMessage)
        }
    }
    
    /**
     * 記錄 ERROR 級別日誌
     * 在 DEBUG 和 RELEASE 模式下都輸出
     */
    fun error(tag: String, message: String, throwable: Throwable? = null) {
        val formattedTag = formatTag(tag)
        val formattedMessage = formatMessage(tag, message)
        
        if (throwable != null) {
            Log.e(formattedTag, formattedMessage, throwable)
        } else {
            Log.e(formattedTag, formattedMessage)
        }
    }
    
    /**
     * 性能監控專用日誌
     * 使用統一的 PERFORMANCE TAG
     */
    fun performance(message: String, throwable: Throwable? = null) {
        debug("PERFORMANCE", message, throwable)
    }
    
    /**
     * 性能警告專用日誌
     */
    fun performanceWarning(message: String, throwable: Throwable? = null) {
        warn("PERFORMANCE", message, throwable)
    }
    
    /**
     * 用戶操作專用日誌
     */
    fun userAction(action: String, details: String = "") {
        info("USER_ACTION", "$action${if (details.isNotEmpty()) " - $details" else ""}")
    }
    
    /**
     * 導航專用日誌
     */
    fun navigation(from: String, to: String) {
        info("NAVIGATION", "From: $from -> To: $to")
    }
    
    /**
     * 生物識別專用日誌
     */
    fun biometric(status: String, details: String = "") {
        info("BIOMETRIC", "$status${if (details.isNotEmpty()) " - $details" else ""}")
    }
    
    /**
     * 資產管理專用日誌
     */
    fun asset(action: String, assetType: String, details: String = "") {
        info("ASSET", "$action $assetType${if (details.isNotEmpty()) " - $details" else ""}")
    }
    
    /**
     * API 請求專用日誌
     */
    fun apiRequest(operation: String, details: String = "") {
        debug("API", "$operation${if (details.isNotEmpty()) " - $details" else ""}")
    }
    
    /**
     * API 錯誤專用日誌
     */
    fun apiError(operation: String, error: String, throwable: Throwable? = null) {
        error("API", "$operation failed: $error", throwable)
    }
    
    /**
     * 格式化 TAG
     */
    private fun formatTag(tag: String): String {
        return "$TAG_PREFIX.$tag"
    }
    
    /**
     * 格式化日誌消息
     * 格式: [時間戳] [TAG] 消息內容
     */
    private fun formatMessage(tag: String, message: String): String {
        val timestamp = dateFormat.format(Date())
        return "[$timestamp] [$tag] $message"
    }
}
