package com.wealthmanager.utils

import android.util.Log
import com.wealthmanager.BuildConfig
import java.text.SimpleDateFormat
import java.util.*

/**
 * Standardized logging utility following Android design guidelines.
 */
object StandardLogger {
    private const val TAG_PREFIX = "WealthManager"
    private val dateFormat = SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault())

    /**
     * Logs VERBOSE level messages (DEBUG mode only).
     */
    fun verbose(
        tag: String,
        message: String,
        throwable: Throwable? = null,
    ) {
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
     * Logs DEBUG level messages (DEBUG mode only).
     */
    fun debug(
        tag: String,
        message: String,
        throwable: Throwable? = null,
    ) {
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
     * Logs INFO level messages (both DEBUG and RELEASE modes).
     */
    fun info(
        tag: String,
        message: String,
        throwable: Throwable? = null,
    ) {
        val formattedTag = formatTag(tag)
        val formattedMessage = formatMessage(tag, message)

        if (throwable != null) {
            Log.i(formattedTag, formattedMessage, throwable)
        } else {
            Log.i(formattedTag, formattedMessage)
        }
    }

    /**
     * Logs WARN level messages (both DEBUG and RELEASE modes).
     */
    fun warn(
        tag: String,
        message: String,
        throwable: Throwable? = null,
    ) {
        val formattedTag = formatTag(tag)
        val formattedMessage = formatMessage(tag, message)

        if (throwable != null) {
            Log.w(formattedTag, formattedMessage, throwable)
        } else {
            Log.w(formattedTag, formattedMessage)
        }
    }

    /**
     * Logs ERROR level messages (both DEBUG and RELEASE modes).
     */
    fun error(
        tag: String,
        message: String,
        throwable: Throwable? = null,
    ) {
        val formattedTag = formatTag(tag)
        val formattedMessage = formatMessage(tag, message)

        if (throwable != null) {
            Log.e(formattedTag, formattedMessage, throwable)
        } else {
            Log.e(formattedTag, formattedMessage)
        }
    }

    fun performance(
        message: String,
        throwable: Throwable? = null,
    ) {
        debug("PERFORMANCE", message, throwable)
    }

    fun performanceWarning(
        message: String,
        throwable: Throwable? = null,
    ) {
        warn("PERFORMANCE", message, throwable)
    }

    fun userAction(
        action: String,
        details: String = "",
    ) {
        info("USER_ACTION", "$action${if (details.isNotEmpty()) " - $details" else ""}")
    }

    fun navigation(
        from: String,
        to: String,
    ) {
        info("NAVIGATION", "From: $from -> To: $to")
    }

    fun biometric(
        status: String,
        details: String = "",
    ) {
        info("BIOMETRIC", "$status${if (details.isNotEmpty()) " - $details" else ""}")
    }

    fun asset(
        action: String,
        assetType: String,
        details: String = "",
    ) {
        info("ASSET", "$action $assetType${if (details.isNotEmpty()) " - $details" else ""}")
    }

    fun apiRequest(
        operation: String,
        details: String = "",
    ) {
        debug("API", "$operation${if (details.isNotEmpty()) " - $details" else ""}")
    }

    fun apiError(
        operation: String,
        error: String,
        throwable: Throwable? = null,
    ) {
        error("API", "$operation failed: $error", throwable)
    }

    private fun formatTag(tag: String): String = "$TAG_PREFIX.$tag"

    private fun formatMessage(
        tag: String,
        message: String,
    ): String {
        val timestamp = dateFormat.format(Date())
        return "[$timestamp] [$tag] $message"
    }
}
