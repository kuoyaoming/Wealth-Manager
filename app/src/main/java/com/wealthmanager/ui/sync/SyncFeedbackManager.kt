package com.wealthmanager.ui.sync

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.wealthmanager.debug.DebugLogManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Unified sync feedback manager.
 */
@Singleton
class SyncFeedbackManager
    @Inject
    constructor(
        private val debugLogManager: DebugLogManager,
    ) {
        private val _syncResults = MutableStateFlow<Map<SyncType, SyncResult>>(emptyMap())
        val syncResults: StateFlow<Map<SyncType, SyncResult>> = _syncResults.asStateFlow()

        private val _currentOperation = MutableStateFlow<SyncOperation?>(null)
        val currentOperation: StateFlow<SyncOperation?> = _currentOperation.asStateFlow()

        /**
         * Start sync operation.
         */
        fun startSync(
            type: SyncType,
            description: String,
            canUndo: Boolean = false,
            undoData: Any? = null,
        ) {
            val operation =
                SyncOperation(
                    type = type,
                    description = description,
                    canUndo = canUndo,
                    undoData = undoData,
                )

            _currentOperation.value = operation
            _syncResults.value = _syncResults.value + (type to SyncResult.InProgress)

            debugLogManager.log("SYNC_FEEDBACK", "Started sync: $type - $description")
        }

        /**
         * Sync success.
         */
        fun syncSuccess(
            type: SyncType,
            message: String,
            itemsUpdated: Int = 0,
            canUndo: Boolean = false,
        ) {
            val result =
                SyncResult.Success(
                    message = message,
                    itemsUpdated = itemsUpdated,
                    canUndo = canUndo,
                )

            _syncResults.value = _syncResults.value + (type to result)
            _currentOperation.value = null

            debugLogManager.log("SYNC_FEEDBACK", "Sync success: $type - $message (items: $itemsUpdated)")
        }

        /**
         * Sync failure.
         */
        fun syncFailure(
            type: SyncType,
            message: String,
            canRetry: Boolean = true,
            errorCode: String? = null,
        ) {
            val result =
                SyncResult.Failure(
                    message = message,
                    canRetry = canRetry,
                    errorCode = errorCode,
                )

            _syncResults.value = _syncResults.value + (type to result)
            _currentOperation.value = null

            debugLogManager.logError("SYNC_FEEDBACK: Sync failure: $type - $message")
        }

        /**
         * Cancel sync.
         */
        fun cancelSync(type: SyncType) {
            _syncResults.value = _syncResults.value + (type to SyncResult.Cancelled)
            _currentOperation.value = null

            debugLogManager.log("SYNC_FEEDBACK", "Sync cancelled: $type")
        }

        /**
         * Clear sync result for specific type.
         */
        fun clearSyncResult(type: SyncType) {
            _syncResults.value = _syncResults.value - type
            debugLogManager.log("SYNC_FEEDBACK", "Cleared sync result: $type")
        }

        /**
         * Clear all sync results.
         */
        fun clearAllSyncResults() {
            _syncResults.value = emptyMap()
            _currentOperation.value = null
            debugLogManager.log("SYNC_FEEDBACK", "Cleared all sync results")
        }

        /**
         * Get sync result for specific type.
         */
        fun getSyncResult(type: SyncType): SyncResult? {
            return _syncResults.value[type]
        }

        /**
         * Check if there are any ongoing sync operations.
         */
        fun hasActiveSync(): Boolean {
            return _syncResults.value.values.any { it is SyncResult.InProgress }
        }
    }

/**
 * Sync feedback UI component using Snackbar.
 */
@Composable
fun SyncFeedbackHandler(
    syncFeedbackManager: SyncFeedbackManager,
    snackbarHostState: SnackbarHostState,
    onRetry: (SyncType) -> Unit = {},
    onUndo: (SyncType, Any?) -> Unit = { _, _ -> },
    onDismiss: (SyncType) -> Unit = {},
) {
    val syncResults by syncFeedbackManager.syncResults.collectAsState()
    val currentOperation by syncFeedbackManager.currentOperation.collectAsState()

    LaunchedEffect(syncResults) {
        syncResults.forEach { entry ->
            val type = entry.key
            val result = entry.value
            when (result) {
                is SyncResult.Success -> {
                    val message =
                        if (result.itemsUpdated > 0) {
                            "${result.message} (${result.itemsUpdated} items updated)"
                        } else {
                            result.message
                        }

                    val actionLabel = if (result.canUndo) "Undo" else null
                    val duration = if (result.canUndo) SnackbarDuration.Long else SnackbarDuration.Short

                    val snackbarResult =
                        snackbarHostState.showSnackbar(
                            message = message,
                            actionLabel = actionLabel,
                            duration = duration,
                        )

                    if (snackbarResult == SnackbarResult.ActionPerformed && result.canUndo) {
                        onUndo(type, currentOperation?.undoData)
                    }

                    syncFeedbackManager.clearSyncResult(type)
                }

                is SyncResult.Failure -> {
                    val actionLabel = if (result.canRetry) "Retry" else null
                    val duration = if (result.canRetry) SnackbarDuration.Long else SnackbarDuration.Short

                    val snackbarResult =
                        snackbarHostState.showSnackbar(
                            message = result.message,
                            actionLabel = actionLabel,
                            duration = duration,
                        )

                    if (snackbarResult == SnackbarResult.ActionPerformed && result.canRetry) {
                        onRetry(type)
                    }

                    syncFeedbackManager.clearSyncResult(type)
                }

                is SyncResult.Cancelled -> {
                    snackbarHostState.showSnackbar(
                        message = "Sync cancelled",
                        duration = SnackbarDuration.Short,
                    )

                    syncFeedbackManager.clearSyncResult(type)
                }

                is SyncResult.InProgress -> {
                }
            }
        }
    }
}
