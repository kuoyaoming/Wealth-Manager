package com.wealthmanager.debug

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.util.Log
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DebugLogManager @Inject constructor() {
    
    private val logs = mutableListOf<String>()
    private val dateFormat = SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault())
    
    fun log(tag: String, message: String) {
        val timestamp = dateFormat.format(Date())
        val logEntry = "[$timestamp] $tag: $message"
        logs.add(logEntry)
        Log.d("WealthManagerDebug", logEntry)
        
        // Keep only last 1000 logs to prevent memory issues
        if (logs.size > 1000) {
            logs.removeAt(0)
        }
    }
    
    fun logUserAction(action: String) {
        log("USER_ACTION", action)
    }
    
    fun logNavigation(from: String, to: String) {
        log("NAVIGATION", "From: $from -> To: $to")
    }
    
    fun logError(error: String, throwable: Throwable? = null) {
        log("ERROR", error)
        throwable?.let {
            log("ERROR", "Exception: ${it.message}")
            log("ERROR", "Stack trace: ${it.stackTraceToString()}")
        }
    }
    
    fun logBiometric(status: String, details: String = "") {
        log("BIOMETRIC", "$status ${if (details.isNotEmpty()) "- $details" else ""}")
    }
    
    fun logAsset(action: String, assetType: String, details: String = "") {
        log("ASSET", "$action $assetType ${if (details.isNotEmpty()) "- $details" else ""}")
    }
    
    fun getAllLogs(): String {
        return logs.joinToString("\n")
    }
    
    fun copyLogsToClipboard(context: Context) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Debug Logs", getAllLogs())
        clipboard.setPrimaryClip(clip)
        log("DEBUG", "Logs copied to clipboard (${getLogCount()} entries)")
    }
    
    fun clearLogs() {
        logs.clear()
    }
    
    fun getLogCount(): Int = logs.size
}