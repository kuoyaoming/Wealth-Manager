package com.wealthmanager.ui.sync

/**
 * 同步操作結果的統一數據類
 */
sealed class SyncResult {
    /**
     * 同步進行中
     */
    object InProgress : SyncResult()

    /**
     * 同步成功
     * @param message 成功訊息
     * @param itemsUpdated 更新的項目數量
     * @param canUndo 是否可以復原
     */
    data class Success(
        val message: String,
        val itemsUpdated: Int = 0,
        val canUndo: Boolean = false,
    ) : SyncResult()

    /**
     * 同步失敗
     * @param message 錯誤訊息
     * @param canRetry 是否可以重試
     * @param errorCode 錯誤代碼（可選）
     */
    data class Failure(
        val message: String,
        val canRetry: Boolean = true,
        val errorCode: String? = null,
    ) : SyncResult()

    /**
     * 同步被取消
     */
    object Cancelled : SyncResult()
}

/**
 * 同步操作類型
 */
enum class SyncType {
    MARKET_DATA, // 市場數據同步（股價、匯率）
    WEAR_SYNC, // Wear OS 同步
    BACKUP, // 備份同步
    MANUAL_REFRESH, // 手動刷新
}

/**
 * 同步操作詳細信息
 */
data class SyncOperation(
    val type: SyncType,
    val description: String,
    val startTime: Long = System.currentTimeMillis(),
    val canUndo: Boolean = false,
    val undoData: Any? = null, // 用於復原的數據
)
