package com.wealthmanager.ui.sync

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import com.wealthmanager.R
import com.wealthmanager.debug.DebugLogManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 統一同步回饋管理器
 * 負責管理所有同步操作的狀態和用戶回饋
 */
@Singleton
class SyncFeedbackManager @Inject constructor(
    private val debugLogManager: DebugLogManager
) {
    private val _syncResults = MutableStateFlow<Map<SyncType, SyncResult>>(emptyMap())
    val syncResults: StateFlow<Map<SyncType, SyncResult>> = _syncResults.asStateFlow()
    
    private val _currentOperation = MutableStateFlow<SyncOperation?>(null)
    val currentOperation: StateFlow<SyncOperation?> = _currentOperation.asStateFlow()
    
    /**
     * 開始同步操作
     */
    fun startSync(type: SyncType, description: String, canUndo: Boolean = false, undoData: Any? = null) {
        val operation = SyncOperation(
            type = type,
            description = description,
            canUndo = canUndo,
            undoData = undoData
        )
        
        _currentOperation.value = operation
        _syncResults.value = _syncResults.value + (type to SyncResult.InProgress)
        
        debugLogManager.log("SYNC_FEEDBACK", "Started sync: $type - $description")
    }
    
    /**
     * 同步成功
     */
    fun syncSuccess(type: SyncType, message: String, itemsUpdated: Int = 0, canUndo: Boolean = false) {
        val result = SyncResult.Success(
            message = message,
            itemsUpdated = itemsUpdated,
            canUndo = canUndo
        )
        
        _syncResults.value = _syncResults.value + (type to result)
        _currentOperation.value = null
        
        debugLogManager.log("SYNC_FEEDBACK", "Sync success: $type - $message (items: $itemsUpdated)")
    }
    
    /**
     * 同步失敗
     */
    fun syncFailure(type: SyncType, message: String, canRetry: Boolean = true, errorCode: String? = null) {
        val result = SyncResult.Failure(
            message = message,
            canRetry = canRetry,
            errorCode = errorCode
        )
        
        _syncResults.value = _syncResults.value + (type to result)
        _currentOperation.value = null
        
        debugLogManager.logError("SYNC_FEEDBACK: Sync failure: $type - $message")
    }
    
    /**
     * 取消同步
     */
    fun cancelSync(type: SyncType) {
        _syncResults.value = _syncResults.value + (type to SyncResult.Cancelled)
        _currentOperation.value = null
        
        debugLogManager.log("SYNC_FEEDBACK", "Sync cancelled: $type")
    }
    
    /**
     * 清除特定類型的同步結果
     */
    fun clearSyncResult(type: SyncType) {
        _syncResults.value = _syncResults.value - type
        debugLogManager.log("SYNC_FEEDBACK", "Cleared sync result: $type")
    }
    
    /**
     * 清除所有同步結果
     */
    fun clearAllSyncResults() {
        _syncResults.value = emptyMap()
        _currentOperation.value = null
        debugLogManager.log("SYNC_FEEDBACK", "Cleared all sync results")
    }
    
    /**
     * 獲取特定類型的同步結果
     */
    fun getSyncResult(type: SyncType): SyncResult? {
        return _syncResults.value[type]
    }
    
    /**
     * 檢查是否有正在進行的同步
     */
    fun hasActiveSync(): Boolean {
        return _syncResults.value.values.any { it is SyncResult.InProgress }
    }
}

/**
 * 同步回饋UI組件
 * 使用Snackbar顯示同步結果
 */
@Composable
fun SyncFeedbackHandler(
    syncFeedbackManager: SyncFeedbackManager,
    snackbarHostState: SnackbarHostState,
    onRetry: (SyncType) -> Unit = {},
    onUndo: (SyncType, Any?) -> Unit = { _, _ -> },
    onDismiss: (SyncType) -> Unit = {}
) {
    val syncResults by syncFeedbackManager.syncResults.collectAsState()
    val currentOperation by syncFeedbackManager.currentOperation.collectAsState()
    
    // 處理同步結果的Snackbar顯示
    LaunchedEffect(syncResults) {
        syncResults.forEach { entry ->
            val type = entry.key
            val result = entry.value
            when (result) {
                is SyncResult.Success -> {
                    val message = if (result.itemsUpdated > 0) {
                        "${result.message} (${result.itemsUpdated} items updated)"
                    } else {
                        result.message
                    }
                    
                    val actionLabel = if (result.canUndo) "Undo" else null
                    val duration = if (result.canUndo) SnackbarDuration.Long else SnackbarDuration.Short
                    
                    val snackbarResult = snackbarHostState.showSnackbar(
                        message = message,
                        actionLabel = actionLabel,
                        duration = duration
                    )
                    
                    if (snackbarResult == SnackbarResult.ActionPerformed && result.canUndo) {
                        onUndo(type, currentOperation?.undoData)
                    }
                    
                    // 清除成功結果
                    syncFeedbackManager.clearSyncResult(type)
                }
                
                is SyncResult.Failure -> {
                    val actionLabel = if (result.canRetry) "Retry" else null
                    val duration = if (result.canRetry) SnackbarDuration.Long else SnackbarDuration.Short
                    
                    val snackbarResult = snackbarHostState.showSnackbar(
                        message = result.message,
                        actionLabel = actionLabel,
                        duration = duration
                    )
                    
                    if (snackbarResult == SnackbarResult.ActionPerformed && result.canRetry) {
                        onRetry(type)
                    }
                    
                    // 清除失敗結果
                    syncFeedbackManager.clearSyncResult(type)
                }
                
                is SyncResult.Cancelled -> {
                    snackbarHostState.showSnackbar(
                        message = "Sync cancelled",
                        duration = SnackbarDuration.Short
                    )
                    
                    // 清除取消結果
                    syncFeedbackManager.clearSyncResult(type)
                }
                
                is SyncResult.InProgress -> {
                    // 進行中的同步不顯示Snackbar，由UI組件處理
                }
            }
        }
    }
}
