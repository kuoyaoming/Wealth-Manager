package com.wealthmanager.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wealthmanager.data.entity.CashAsset
import com.wealthmanager.data.entity.StockAsset
import com.wealthmanager.data.repository.AssetRepository
import com.wealthmanager.data.service.MarketDataService
import com.wealthmanager.debug.DebugLogManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val assetRepository: AssetRepository,
    private val marketDataService: MarketDataService,
    private val debugLogManager: DebugLogManager
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()
    
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
        _uiState.value = _uiState.value.copy(isLoading = true)
        // Data will be loaded through the observeAssets() flow
    }
    
    fun refreshData() {
        debugLogManager.log("DASHBOARD", "Refreshing data - starting market data update")
        _uiState.value = _uiState.value.copy(isLoading = true)
        viewModelScope.launch {
            try {
                debugLogManager.log("DASHBOARD", "Updating exchange rates")
                marketDataService.updateExchangeRates()
                
                debugLogManager.log("DASHBOARD", "Updating stock prices")
                marketDataService.updateStockPrices()
                
                debugLogManager.log("DASHBOARD", "Market data update completed")
                // Don't call observeAssets() again - it's already running
                // The data will be updated automatically through the existing flow
            } catch (e: Exception) {
                debugLogManager.logError("Failed to refresh data: ${e.message}", e)
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
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