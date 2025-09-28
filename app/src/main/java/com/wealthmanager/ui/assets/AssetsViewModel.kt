package com.wealthmanager.ui.assets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wealthmanager.data.entity.CashAsset
import com.wealthmanager.data.entity.StockAsset
import com.wealthmanager.data.repository.AssetRepository
import com.wealthmanager.data.service.MarketDataService
import com.wealthmanager.data.service.StockSearchItem
import com.wealthmanager.debug.DebugLogManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AssetsViewModel @Inject constructor(
    private val assetRepository: AssetRepository,
    private val marketDataService: MarketDataService,
    private val debugLogManager: DebugLogManager
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(AssetsUiState())
    val uiState: StateFlow<AssetsUiState> = _uiState.asStateFlow()
    
    fun searchStocks(query: String, market: String) {
        debugLogManager.log("ASSETS", "Searching stocks: '$query' in market: '$market'")
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSearching = true)
            try {
                val results = marketDataService.searchStocks(query, market)
                debugLogManager.log("ASSETS", "Stock search completed: ${results.size} results found")
                _uiState.value = _uiState.value.copy(
                    searchResults = results,
                    isSearching = false
                )
            } catch (e: Exception) {
                debugLogManager.logError("Stock search failed: ${e.message}", e)
                _uiState.value = _uiState.value.copy(
                    searchResults = emptyList(),
                    isSearching = false
                )
            }
        }
    }
    
    fun loadAssets() {
        debugLogManager.log("ASSETS", "Loading assets from repository")
        viewModelScope.launch {
            combine(
                assetRepository.getAllCashAssets(),
                assetRepository.getAllStockAssets()
            ) { cashAssets, stockAssets ->
                debugLogManager.log("ASSETS", "Assets loaded - Cash: ${cashAssets.size}, Stock: ${stockAssets.size}")
                _uiState.value = _uiState.value.copy(
                    cashAssets = cashAssets,
                    stockAssets = stockAssets,
                    isLoading = false
                )
            }.collect { }
        }
    }
    
    fun addCashAsset(currency: String, amount: Double) {
        debugLogManager.log("ASSETS", "Adding cash asset: $currency $amount")
        viewModelScope.launch {
            val twdEquivalent = if (currency == "TWD") amount else amount * 30.0 // Simple conversion
            val cashAsset = CashAsset(
                id = System.currentTimeMillis().toString(),
                currency = currency,
                amount = amount,
                twdEquivalent = twdEquivalent
            )
            debugLogManager.log("ASSETS", "Cash asset created: $currency $amount (TWD: $twdEquivalent)")
            assetRepository.insertCashAsset(cashAsset)
            debugLogManager.log("ASSETS", "Cash asset inserted to database")
        }
    }
    
    fun addStockAsset(symbol: String, shares: Double, market: String) {
        debugLogManager.log("ASSETS", "Adding stock asset: $symbol, $shares shares, market: $market")
        viewModelScope.launch {
            val stockAsset = StockAsset(
                id = System.currentTimeMillis().toString(),
                symbol = symbol,
                companyName = symbol, // Simple mapping
                shares = shares.toInt(),
                market = market,
                currentPrice = 0.0, // Will be fetched later
                twdEquivalent = 0.0 // Will be calculated later
            )
            debugLogManager.log("ASSETS", "Stock asset created: $symbol, ${shares.toInt()} shares")
            assetRepository.insertStockAsset(stockAsset)
            debugLogManager.log("ASSETS", "Stock asset inserted to database")
        }
    }
}

data class AssetsUiState(
    val cashAssets: List<CashAsset> = emptyList(),
    val stockAssets: List<StockAsset> = emptyList(),
    val isLoading: Boolean = true,
    val searchResults: List<StockSearchItem> = emptyList(),
    val isSearching: Boolean = false
)