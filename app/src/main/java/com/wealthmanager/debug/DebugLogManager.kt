package com.wealthmanager.debug

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.util.Log
import com.wealthmanager.BuildConfig
import com.wealthmanager.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DebugLogManager @Inject constructor() {

    private val logs = mutableListOf<String>()
    private val dateFormat = SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault())

    private val isVerboseLoggingEnabled = BuildConfig.DEBUG
    private val isMarketDataVerboseEnabled = false

    private fun addLogEntry(logEntry: String, logLevel: Int) {
        logs.add(logEntry)
        if (logs.size > MAX_LOG_ENTRIES) {
            logs.removeAt(0)
        }

        when (logLevel) {
            Log.DEBUG -> Log.d(LOG_TAG, logEntry)
            Log.INFO -> Log.i(LOG_TAG, logEntry)
            Log.WARN -> Log.w(LOG_TAG, logEntry)
            Log.ERROR -> Log.e(LOG_TAG, logEntry)
            else -> Log.v(LOG_TAG, logEntry)
        }
    }

    private fun createLogMessage(tag: String, message: String): String {
        val timestamp = dateFormat.format(Date())
        return "[$timestamp] $tag: $message"
    }

    fun log(tag: String, message: String) {
        if (!isVerboseLoggingEnabled) return
        addLogEntry(createLogMessage(tag, message), Log.DEBUG)
    }

    fun logInfo(tag: String, message: String) {
        addLogEntry(createLogMessage(tag, message), Log.INFO)
    }

    fun logMarketData(action: String, message: String) {
        addLogEntry(createLogMessage("MARKET_DATA", "$action - $message"), Log.INFO)
    }

    fun logMarketDataVerbose(action: String, message: String) {
        if (!isMarketDataVerboseEnabled) return
        addLogEntry(createLogMessage("MARKET_DATA_VERBOSE", "$action - $message"), Log.DEBUG)
    }

    fun logWarning(tag: String, message: String) {
        addLogEntry(createLogMessage("WARN $tag", message), Log.WARN)
    }

    fun logError(tag: String, message: String) {
        addLogEntry(createLogMessage("ERROR $tag", message), Log.ERROR)
    }

    fun logError(error: String, throwable: Throwable? = null) {
        logError("ERROR", error)
        throwable?.let {
            val stackTrace = if (isDebugBuild()) it.stackTraceToString() else "(stack trace available in debug builds)"
            logError("EXCEPTION", "${it::class.simpleName}: ${it.message}\n$stackTrace")
        }
    }

    fun logUserAction(action: String) {
        log("USER_ACTION", action)
    }

    fun logNavigation(from: String, to: String) {
        log("NAVIGATION", "From: $from -> To: $to")
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
        val clip = ClipData.newPlainText(context.getString(R.string.debug_clipboard_label), getAllLogs())
        clipboard.setPrimaryClip(clip)
        log("DEBUG", "Logs copied to clipboard (${getLogCount()} entries)")
    }

    fun clearLogs() {
        logs.clear()
    }

    fun getLogCount(): Int = logs.size

    companion object {
        private const val MAX_LOG_ENTRIES = 1000
        private const val LOG_TAG = "WealthManagerDebug"
    }
}
