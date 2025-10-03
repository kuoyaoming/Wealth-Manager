package com.wealthmanager.data.service

/**
 * 市場數據同步結果
 */
data class MarketDataSyncResult(
    val success: Boolean,
    val itemsUpdated: Int = 0,
    val itemsFailed: Int = 0,
    val errorMessage: String? = null,
    val usedCache: Boolean = false,
)

/**
 * 匯率同步結果
 */
data class ExchangeRateSyncResult(
    val success: Boolean,
    val errorMessage: String? = null,
    val usedCache: Boolean = false,
)
