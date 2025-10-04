package com.wealthmanager.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wealthmanager.data.entity.CashAsset
import com.wealthmanager.data.entity.StockAsset
import com.wealthmanager.data.repository.AssetRepository
import com.wealthmanager.data.service.ApiStatus
import com.wealthmanager.data.service.ApiStatusManager
import com.wealthmanager.data.service.MarketDataService
import com.wealthmanager.debug.DebugLogManager
import com.wealthmanager.security.KeyRepository
import com.wealthmanager.ui.sync.SyncFeedbackManager
import com.wealthmanager.ui.sync.SyncType
import com.wealthmanager.util.NetworkUtils
import com.wealthmanager.wear.WearSyncManager
import com.wealthmanager.wear.WearSyncManager.ManualSyncResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the dashboard screen managing portfolio data and UI state.
 *
 * This ViewModel handles:
 * - Portfolio data aggregation and calculation
 * - Real-time market data updates
 * - API status monitoring and error handling
 * - Wear OS synchronization
 * - Asset loading and refresh operations
 *
 * @property assetRepository Repository for asset data operations
 * @property marketDataService Service for fetching market data
 * @property debugLogManager Manager for debug logging
 * @property apiStatusManager Manager for API status monitoring
 * @property wearSyncManager Manager for Wear OS synchronization
 * @property keyRepository Repository for API key management
 */
@HiltViewModel
class DashboardViewModel
    @Inject
    constructor(
        private val assetRepository: AssetRepository,
        private val marketDataService: MarketDataService,
        private val debugLogManager: DebugLogManager,
        private val apiStatusManager: ApiStatusManager,
        private val wearSyncManager: WearSyncManager,
        private val keyRepository: KeyRepository,
        val syncFeedbackManager: SyncFeedbackManager,
        private val networkUtils: NetworkUtils,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(DashboardUiState())
        val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()
        val apiStatus: StateFlow<ApiStatus> = apiStatusManager.apiStatus

        private val _manualSyncStatus = MutableStateFlow<ManualSyncStatus?>(null)
        val manualSyncStatus: StateFlow<ManualSyncStatus?> = _manualSyncStatus.asStateFlow()

        init {
            debugLogManager.log("DASHBOARD", "DashboardViewModel initialized")
            observeAssets()
        }

        fun hasRequiredKeys(): Boolean {
            val finnhub = keyRepository.getUserFinnhubKey()?.isNotBlank() == true
            val exchange = keyRepository.getUserExchangeKey()?.isNotBlank() == true
            debugLogManager.log("DASHBOARD", "Key status - Finnhub: $finnhub, Exchange: $exchange")

            return finnhub && exchange
        }

        private fun observeAssets() {
            debugLogManager.log("DASHBOARD", "Starting to observe assets")
            viewModelScope.launch {
                combine(
                    assetRepository.getAllCashAssets(),
                    assetRepository.getAllStockAssets(),
                ) { cashAssets, stockAssets ->
                    debugLogManager.log(
                        "DASHBOARD",
                        "Assets updated - Cash: ${cashAssets.size}, Stock: ${stockAssets.size}",
                    )

                    val totalCash = cashAssets.sumOf { it.twdEquivalent }
                    val totalStock = stockAssets.sumOf { it.twdEquivalent }
                    val totalAssets = totalCash + totalStock
                    val lastUpdatedCash = cashAssets.maxOfOrNull { it.lastUpdated } ?: 0L
                    val lastUpdatedStock = stockAssets.maxOfOrNull { it.lastUpdated } ?: 0L
                    val lastUpdated = maxOf(lastUpdatedCash, lastUpdatedStock, System.currentTimeMillis())

                    debugLogManager.log("DASHBOARD", "Total Assets: $totalAssets, Cash: $totalCash, Stock: $totalStock")

                    _uiState.value =
                        _uiState.value.copy(
                            totalAssets = totalAssets,
                            cashAssets = totalCash,
                            stockAssets = totalStock,
                            assets = cashAssets.map { it.toAssetItem(totalAssets) } + stockAssets.map { it.toAssetItem(totalAssets) },
                            isLoading = false,
                        )

                    viewModelScope.launch {
                        debugLogManager.log(
                            "WEAR_SYNC",
                            "Starting wear sync - total: $totalAssets, lastUpdated: $lastUpdated",
                        )
                        try {
                            wearSyncManager.syncTotalsFromDashboard(
                                totalAssets = totalAssets,
                                lastUpdated = lastUpdated,
                                hasError = false,
                            )
                            debugLogManager.log("WEAR_SYNC", "Wear sync completed successfully")
                        } catch (e: Exception) {
                            debugLogManager.logError("WEAR_SYNC: Failed to sync to wear: ${e.message}", e)
                        }
                    }
                    
                    // Schedule widget update when data changes
                    debugLogManager.log("WIDGET_SYNC", "Scheduling widget update")
                    com.wealthmanager.widget.WidgetUpdateScheduler.scheduleUpdate(
                        com.wealthmanager.WealthManagerApplication.getInstance()
                    )
                }.collect { }
            }
        }

        fun loadPortfolioData() {
            debugLogManager.log("DASHBOARD", "Loading portfolio data")
            refreshData()
        }

        fun onReturnFromAssets() {
            debugLogManager.log("DASHBOARD", "Returned from Assets screen - checking if data needs refresh")
            // When returning from Assets, only check if update is needed, do not force refresh
            viewModelScope.launch {
                try {
                    val stockAssets = assetRepository.getAllStockAssets().first()
                    if (stockAssets.isNotEmpty()) {
                        debugLogManager.log("DASHBOARD", "Has stock assets, checking if refresh is needed")
                        // Can add smarter check logic here, such as checking if data is expired
                        // Temporarily no auto refresh, let user manually refresh
                    } else {
                        debugLogManager.log("DASHBOARD", "No stock assets, no need to refresh")
                    }
                } catch (e: Exception) {
                    debugLogManager.logError("Error checking assets on return: ${e.message}", e)
                }
            }
        }

        fun refreshData() {
            debugLogManager.log("DASHBOARD", "Refreshing data - starting market data update")
            _uiState.value = _uiState.value.copy(isLoading = true)
            apiStatusManager.setRetrying(true)

            // 開始同步操作
            syncFeedbackManager.startSync(
                type = SyncType.MANUAL_REFRESH,
                description = "Refreshing data"
            )

            viewModelScope.launch {
                try {
                    // Check if we have any assets that need updating
                    val stockAssets = assetRepository.getAllStockAssets().first()
                    val hasStockAssets = stockAssets.isNotEmpty()

                    debugLogManager.log("DASHBOARD", "Asset check - Stock assets: ${stockAssets.size}")

                    var itemsUpdated = 0

                    // Smart update: only update market data when needed
                    if (hasStockAssets) {
                        debugLogManager.log("DASHBOARD", "Updating exchange rates and stock prices")
                        
                        // 開始市場數據同步
                        syncFeedbackManager.startSync(
                            type = SyncType.MARKET_DATA,
                            description = "Syncing market data"
                        )
                        
                        val hasNetwork = networkUtils.hasNetworkConnection()
                        debugLogManager.log("DASHBOARD", "Network available: $hasNetwork")
                        
                        try {
                            marketDataService.updateExchangeRates()
                            marketDataService.updateStockPrices()
                            
                            // 計算更新的項目數量（這裡簡化為股票數量）
                            itemsUpdated = stockAssets.size
                            
                            if (hasNetwork) {
                                syncFeedbackManager.syncSuccess(
                                    type = SyncType.MARKET_DATA,
                                    message = "Market data updated successfully",
                                    itemsUpdated = itemsUpdated
                                )
                            } else {
                                // 沒有網路但使用了緩存數據
                                syncFeedbackManager.syncSuccess(
                                    type = SyncType.MARKET_DATA,
                                    message = "Using cached data (no internet connection)",
                                    itemsUpdated = itemsUpdated
                                )
                            }
                        } catch (e: Exception) {
                            // 檢查是否為網路錯誤
                            val isNetworkError = e.message?.contains("network", ignoreCase = true) == true ||
                                e.message?.contains("connection", ignoreCase = true) == true ||
                                e.message?.contains("timeout", ignoreCase = true) == true
                            
                            if (isNetworkError && !hasNetwork) {
                                // 沒有網路且無法使用緩存
                                syncFeedbackManager.syncFailure(
                                    type = SyncType.MARKET_DATA,
                                    message = "No internet connection and no cached data available",
                                    canRetry = true
                                )
                            } else if (isNetworkError) {
                                // 有網路但連接失敗
                                syncFeedbackManager.syncFailure(
                                    type = SyncType.MARKET_DATA,
                                    message = "Network connection failed. Please check your internet connection.",
                                    canRetry = true
                                )
                            } else {
                                // 其他錯誤
                                syncFeedbackManager.syncFailure(
                                    type = SyncType.MARKET_DATA,
                                    message = "Failed to sync market data: ${e.message}",
                                    canRetry = true
                                )
                            }
                        }
                    } else {
                        debugLogManager.log("DASHBOARD", "No stock assets found, skipping market data update")
                    }

                    debugLogManager.log("DASHBOARD", "Market data update completed")
                    apiStatusManager.setApiSuccess()
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    
                    // 手動刷新成功
                    syncFeedbackManager.syncSuccess(
                        type = SyncType.MANUAL_REFRESH,
                        message = "Data refreshed successfully",
                        itemsUpdated = itemsUpdated
                    )
                } catch (e: Exception) {
                    debugLogManager.logError("Failed to refresh data: ${e.message}", e)
                    apiStatusManager.setApiError(
                        "Server busy, please try again later",
                        isDataStale = true,
                        isRetrying = false,
                    )
                    _uiState.value = _uiState.value.copy(isLoading = false)

                    // 同步失敗
                    syncFeedbackManager.syncFailure(
                        type = SyncType.MANUAL_REFRESH,
                        message = "Failed to refresh data: ${e.message}",
                        canRetry = true
                    )

                    viewModelScope.launch {
                        wearSyncManager.syncTotalsFromDashboard(
                            totalAssets = _uiState.value.totalAssets,
                            lastUpdated = System.currentTimeMillis(),
                            hasError = true,
                        )
                    }
                }
            }
        }

        fun retryApiCall() {
            debugLogManager.logUserAction("Retry API Call")
            refreshData()
        }

        fun dismissApiError() {
            debugLogManager.logUserAction("Dismiss API Error")
            apiStatusManager.clearError()
        }

        fun manualSyncToWear() {
            val currentState = _uiState.value
            
            // 開始Wear同步
            syncFeedbackManager.startSync(
                type = SyncType.WEAR_SYNC,
                description = "Syncing to Wear"
            )
            
            viewModelScope.launch {
                _manualSyncStatus.value = ManualSyncStatus.InProgress
                when (
                    val result =
                        wearSyncManager.manualSync(
                            totalAssets = currentState.totalAssets,
                            lastUpdated = System.currentTimeMillis(),
                            hasError = false,
                        )
                ) {
                    ManualSyncResult.Success -> {
                        _manualSyncStatus.value = ManualSyncStatus.Success
                        syncFeedbackManager.syncSuccess(
                            type = SyncType.WEAR_SYNC,
                            message = "Wear sync completed"
                        )
                    }
                    ManualSyncResult.WearAppNotInstalled -> {
                        _manualSyncStatus.value = ManualSyncStatus.WearAppMissing
                        syncFeedbackManager.syncFailure(
                            type = SyncType.WEAR_SYNC,
                            message = "Wear app not installed",
                            canRetry = false
                        )
                    }
                    is ManualSyncResult.Failure -> {
                        _manualSyncStatus.value = ManualSyncStatus.Failure(result.reason)
                        syncFeedbackManager.syncFailure(
                            type = SyncType.WEAR_SYNC,
                            message = "Wear sync failed: ${result.reason}",
                            canRetry = true
                        )
                    }
                }
            }
        }

        fun clearManualSyncStatus() {
            _manualSyncStatus.value = null
        }
    }

data class DashboardUiState(
    val totalAssets: Double = 0.0,
    val cashAssets: Double = 0.0,
    val stockAssets: Double = 0.0,
    val assets: List<AssetItem> = emptyList(),
    val isLoading: Boolean = true,
)

data class AssetItem(
    val id: String,
    val name: String,
    val value: Double,
    val percentage: Double = 0.0,
)

private fun StockAsset.toAssetItem(totalValue: Double): AssetItem {
    return AssetItem(
        id = id,
        name = companyName, // Only show company name, no duplicate symbol
        value = twdEquivalent,
        percentage = if (totalValue > 0) (twdEquivalent / totalValue * 100) else 0.0,
    )
}

private fun CashAsset.toAssetItem(totalValue: Double): AssetItem {
    return AssetItem(
        id = id,
        name = "$currency $amount",
        value = twdEquivalent,
        percentage = if (totalValue > 0) (twdEquivalent / totalValue * 100) else 0.0,
    )
}

sealed class ManualSyncStatus {
    object InProgress : ManualSyncStatus()

    object Success : ManualSyncStatus()

    data class Failure(val reason: String? = null) : ManualSyncStatus()

    object WearAppMissing : ManualSyncStatus()
}
