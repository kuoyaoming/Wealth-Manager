package com.wealthmanager.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wealthmanager.data.entity.CashAsset
import com.wealthmanager.data.entity.StockAsset
import com.wealthmanager.data.repository.AssetRepository
import com.wealthmanager.data.service.ApiStatus
import com.wealthmanager.data.service.ApiStatusManager
import com.wealthmanager.data.service.ApiUsageManager
import com.wealthmanager.data.service.MarketDataService
import com.wealthmanager.debug.DebugLogManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val assetRepository: AssetRepository,
    private val marketDataService: MarketDataService,
    private val debugLogManager: DebugLogManager,
    private val apiStatusManager: ApiStatusManager,
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()
    val apiStatus: StateFlow<ApiStatus> = apiStatusManager.apiStatus
    
    init {
        debugLogManager.log("DASHBOARD", "DashboardViewModel initialized")
        observeAssets()
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
                
                debugLogManager.log("DASHBOARD", "Total Assets: $totalAssets, Cash: $totalCash, Stock: $totalStock")
                
                _uiState.value = _uiState.value.copy(
                    totalAssets = totalAssets,
                    cashAssets = totalCash,
                    stockAssets = totalStock,
                    assets = cashAssets.map { it.toAssetItem() } + stockAssets.map { it.toAssetItem() },
                    isLoading = false
                )
            }.collect { }
        }
    }
    
    fun loadPortfolioData() {
        debugLogManager.log("DASHBOARD", "Loading portfolio data")
        // Do not set isLoading = true, let observeAssets() naturally load data
        // Only set loading state when market data update is needed
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