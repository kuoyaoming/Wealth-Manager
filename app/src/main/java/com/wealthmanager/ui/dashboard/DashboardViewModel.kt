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
import com.wealthmanager.wear.WearSyncManager
import com.wealthmanager.wear.WearSyncManager.ManualSyncResult
import com.wealthmanager.security.KeyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val assetRepository: AssetRepository,
    private val marketDataService: MarketDataService,
    private val debugLogManager: DebugLogManager,
    private val apiStatusManager: ApiStatusManager,
    private val wearSyncManager: WearSyncManager,
    private val keyRepository: KeyRepository,
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
        return finnhub && exchange
    }

    private fun observeAssets() {
        debugLogManager.log("DASHBOARD", "Starting to observe assets")
        viewModelScope.launch {
            combine(
                assetRepository.getAllCashAssets(),
                assetRepository.getAllStockAssets()
            ) { cashAssets, stockAssets ->
                debugLogManager.log("DASHBOARD", "Assets updated - Cash: ${cashAssets.size}, Stock: ${stockAssets.size}")
                
                val totalCash = cashAssets.sumOf { it.twdEquivalent }
                val totalStock = stockAssets.sumOf { it.twdEquivalent }
                val totalAssets = totalCash + totalStock
                val lastUpdatedCash = cashAssets.maxOfOrNull { it.lastUpdated } ?: 0L
                val lastUpdatedStock = stockAssets.maxOfOrNull { it.lastUpdated } ?: 0L
                val lastUpdated = maxOf(lastUpdatedCash, lastUpdatedStock, System.currentTimeMillis())
                
                debugLogManager.log("DASHBOARD", "Total Assets: $totalAssets, Cash: $totalCash, Stock: $totalStock")
                
                _uiState.value = _uiState.value.copy(
                    totalAssets = totalAssets,
                    cashAssets = totalCash,
                    stockAssets = totalStock,
                    assets = cashAssets.map { it.toAssetItem() } + stockAssets.map { it.toAssetItem() },
                    isLoading = false
                )

                viewModelScope.launch {
                    debugLogManager.log("WEAR_SYNC", "Starting wear sync - total: $totalAssets, lastUpdated: $lastUpdated")
                    try {
                        wearSyncManager.syncTotalsFromDashboard(
                            totalAssets = totalAssets,
                            lastUpdated = lastUpdated,
                            hasError = false
                        )
                        debugLogManager.log("WEAR_SYNC", "Wear sync completed successfully")
                    } catch (e: Exception) {
                        debugLogManager.logError("WEAR_SYNC: Failed to sync to wear: ${e.message}", e)
                    }
                }
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
        
        viewModelScope.launch {
            try {
                // Check if we have any assets that need updating
                val stockAssets = assetRepository.getAllStockAssets().first()
                val hasStockAssets = stockAssets.isNotEmpty()
                
                debugLogManager.log("DASHBOARD", "Asset check - Stock assets: ${stockAssets.size}")
                
                // Smart update: only update market data when needed
                if (hasStockAssets) {
                    debugLogManager.log("DASHBOARD", "Updating exchange rates and stock prices")
                    marketDataService.updateExchangeRates()
                    marketDataService.updateStockPrices()
                } else {
                    debugLogManager.log("DASHBOARD", "No stock assets found, skipping market data update")
                }
                
                debugLogManager.log("DASHBOARD", "Market data update completed")
                apiStatusManager.setApiSuccess()
                _uiState.value = _uiState.value.copy(isLoading = false)
            } catch (e: Exception) {
                debugLogManager.logError("Failed to refresh data: ${e.message}", e)
                apiStatusManager.setApiError("Server busy, please try again later", isDataStale = true, isRetrying = false)
                _uiState.value = _uiState.value.copy(isLoading = false)

                viewModelScope.launch {
                    wearSyncManager.syncTotalsFromDashboard(
                        totalAssets = _uiState.value.totalAssets,
                        lastUpdated = System.currentTimeMillis(),
                        hasError = true
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
        viewModelScope.launch {
            _manualSyncStatus.value = ManualSyncStatus.InProgress
            when (val result = wearSyncManager.manualSync(
                totalAssets = currentState.totalAssets,
                lastUpdated = System.currentTimeMillis(),
                hasError = false
            )) {
                ManualSyncResult.Success -> _manualSyncStatus.value = ManualSyncStatus.Success
                ManualSyncResult.WearAppNotInstalled -> _manualSyncStatus.value = ManualSyncStatus.WearAppMissing
                is ManualSyncResult.Failure -> _manualSyncStatus.value = ManualSyncStatus.Failure(result.reason)
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
    val isLoading: Boolean = true
)

data class AssetItem(
    val id: String,
    val name: String,
    val value: Double,
    val percentage: Double = 0.0
)

private fun StockAsset.toAssetItem(): AssetItem {
    return AssetItem(
        id = id,
        name = "$companyName ($symbol)",
        value = twdEquivalent
    )
}

private fun CashAsset.toAssetItem(): AssetItem {
    return AssetItem(
        id = id,
        name = "$currency $amount",
        value = twdEquivalent
    )
}

sealed class ManualSyncStatus {
    object InProgress : ManualSyncStatus()
    object Success : ManualSyncStatus()
    data class Failure(val reason: String? = null) : ManualSyncStatus()
    object WearAppMissing : ManualSyncStatus()
}