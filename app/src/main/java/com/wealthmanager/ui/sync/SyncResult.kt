package com.wealthmanager.ui.sync

/**
 * Unified data class for sync operation results.
 */
sealed class SyncResult {
    /**
     * Sync in progress.
     */
    object InProgress : SyncResult()

    /**
     * Sync success.
     * @param message Success message
     * @param itemsUpdated Number of updated items
     * @param canUndo Whether undo is available
     */
    data class Success(
        val message: String,
        val itemsUpdated: Int = 0,
        val canUndo: Boolean = false,
    ) : SyncResult()

    /**
     * Sync failure.
     * @param message Error message
     * @param canRetry Whether retry is available
     * @param errorCode Error code (optional)
     */
    data class Failure(
        val message: String,
        val canRetry: Boolean = true,
        val errorCode: String? = null,
    ) : SyncResult()

    /**
     * Sync cancelled.
     */
    object Cancelled : SyncResult()
}

/**
 * Sync operation type.
 */
enum class SyncType {
    MARKET_DATA,
    WEAR_SYNC,
    BACKUP,
    MANUAL_REFRESH,
}

/**
 * Sync operation details.
 */
data class SyncOperation(
    val type: SyncType,
    val description: String,
    val startTime: Long = System.currentTimeMillis(),
    val canUndo: Boolean = false,
    val undoData: Any? = null,
)
