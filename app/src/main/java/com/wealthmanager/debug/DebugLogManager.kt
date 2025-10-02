package com.wealthmanager.debug

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.util.Log
import com.wealthmanager.BuildConfig
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DebugLogManager @Inject constructor() {

    private val logs = mutableListOf<String>()
    private val dateFormat = SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault())

    private val isVerboseLoggingEnabled = BuildConfig.DEBUG
    private val isMarketDataVerboseEnabled = false

    fun log(tag: String, message: String) {
        if (!isVerboseLoggingEnabled) return

        val timestamp = dateFormat.format(Date())
        val logEntry = "[$timestamp] $tag: $message"
        logs.add(logEntry)
        Log.d("WealthManagerDebug", logEntry)

        if (logs.size > 1000) {
            logs.removeAt(0)
        }
    }

    fun logInfo(tag: String, message: String) {
        val timestamp = dateFormat.format(Date())
        val logEntry = "[$timestamp] $tag: $message"
        logs.add(logEntry)
        Log.i("WealthManagerDebug", logEntry)

        if (logs.size > 1000) {
            logs.removeAt(0)
        }
    }

    fun logMarketData(action: String, message: String) {
        val timestamp = dateFormat.format(Date())
        val logEntry = "[$timestamp] MARKET_DATA: $action - $message"
        logs.add(logEntry)
        Log.i("WealthManagerDebug", logEntry)

        if (logs.size > 1000) {
            logs.removeAt(0)
        }
    }

    fun logMarketDataVerbose(action: String, message: String) {
        if (!isMarketDataVerboseEnabled) return

        val timestamp = dateFormat.format(Date())
        val logEntry = "[$timestamp] MARKET_DATA_VERBOSE: $action - $message"
        logs.add(logEntry)
        Log.d("WealthManagerDebug", logEntry)

        if (logs.size > 1000) {
            logs.removeAt(0)
        }
    }

    fun logWarning(tag: String, message: String) {
        val timestamp = dateFormat.format(Date())
        val logEntry = "[$timestamp] WARN $tag: $message"
        logs.add(logEntry)
        Log.w("WealthManagerDebug", logEntry)

        if (logs.size > 1000) {
            logs.removeAt(0)
        }
    }

    fun logError(tag: String, message: String) {
        val timestamp = dateFormat.format(Date())
        val logEntry = "[$timestamp] ERROR $tag: $message"
        logs.add(logEntry)
        Log.e("WealthManagerDebug", logEntry)

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
        logError("ERROR", error)
        throwable?.let {
            logError("ERROR", "Exception: ${it.message}")
            if (isDebugBuild()) {
                logError("ERROR", "Stack trace: ${it.stackTraceToString()}")
            } else {
                logError("ERROR", "Exception type: ${it::class.simpleName}")
            }
        }
    }

    private fun isDebugBuild(): Boolean {
        return BuildConfig.DEBUG
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
        if (!isDebugBuild()) {
            logWarning("DEBUG", "Debug log copying disabled in production build")
            return
        }

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
