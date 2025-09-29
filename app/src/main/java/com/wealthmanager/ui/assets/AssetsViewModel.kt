package com.wealthmanager.ui.assets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wealthmanager.data.entity.CashAsset
import com.wealthmanager.data.entity.StockAsset
import com.wealthmanager.data.model.SearchResult
import com.wealthmanager.data.model.NoResultsReason
import com.wealthmanager.data.model.SearchErrorType
import com.wealthmanager.data.model.StockSearchItem
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
class AssetsViewModel @Inject constructor(
    private val assetRepository: AssetRepository,
    private val marketDataService: MarketDataService,
    private val debugLogManager: DebugLogManager
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(AssetsUiState())
    val uiState: StateFlow<AssetsUiState> = _uiState.asStateFlow()
    
        fun searchStocks(query: String) {
            debugLogManager.log("ASSETS", "=== STARTING STOCK SEARCH IN VIEWMODEL ===")
            debugLogManager.log("ASSETS", "Searching stocks: '$query'")
            debugLogManager.logUserAction("Stock Search Initiated")
            viewModelScope.launch {
                _uiState.value = _uiState.value.copy(isSearching = true)
                debugLogManager.log("ASSETS", "Search state set to loading")
                try {
                    debugLogManager.log("ASSETS", "Calling marketDataService.searchStocks")
                    marketDataService.searchStocks(query, "US").collect { searchResult: SearchResult ->
                        when (searchResult) {
                            is SearchResult.Success -> {
                                debugLogManager.log("ASSETS", "Search successful: ${searchResult.results.size} results found")
                                _uiState.value = _uiState.value.copy(
                                    searchResults = searchResult.results,
                                    isSearching = false,
                                    searchError = if (searchResult.results.isEmpty()) "Stock code not found, please check if correct" else ""
                                )
                            }
                            is SearchResult.NoResults -> {
                                debugLogManager.log("ASSETS", "No results found: ${searchResult.reason}")
                                _uiState.value = _uiState.value.copy(
                                    searchResults = emptyList(),
                                    isSearching = false,
                                    searchError = when (searchResult.reason) {
                                        NoResultsReason.STOCK_NOT_FOUND -> "Stock code not found, please check if correct"
                                        NoResultsReason.API_LIMIT_REACHED -> "API request limit reached, please try again tomorrow"
                                        NoResultsReason.NETWORK_ERROR -> "Network connection issue, please check network settings"
                                        NoResultsReason.INVALID_QUERY -> "Please enter at least 2 characters to search"
                                        NoResultsReason.SERVER_ERROR -> "Server temporarily unavailable, please try again later"
                                    }
                                )
                            }
                            is SearchResult.Error -> {
                                debugLogManager.logError("Search error: ${searchResult.errorType}", Exception("Search failed"))
                                _uiState.value = _uiState.value.copy(
                                    searchResults = emptyList(),
                                    isSearching = false,
                                    searchError = when (searchResult.errorType) {
                                        SearchErrorType.API_LIMIT -> "API request limit reached, please try again tomorrow"
                                        SearchErrorType.NETWORK_ERROR -> "Network connection issue, please check network settings"
                                        SearchErrorType.SERVER_ERROR -> "Server temporarily unavailable, please try again later"
                                        SearchErrorType.INVALID_API_KEY -> "Invalid API key, please contact technical support"
                                        SearchErrorType.AUTHENTICATION_ERROR -> "API authentication failed, please check API key settings"
                                        SearchErrorType.RATE_LIMIT_ERROR -> "Request rate limit exceeded, please try again later"
                                        SearchErrorType.UNKNOWN_ERROR -> "Unknown error occurred, please restart the application"
                                    }
                                )
                            }
                        }
                    }
                    debugLogManager.log("ASSETS", "UI state updated with search results")
                } catch (e: Exception) {
                    debugLogManager.logError("Stock search failed: ${e.message}", e)
                    debugLogManager.log("ASSETS", "Exception during stock search: ${e::class.simpleName}")
                    _uiState.value = _uiState.value.copy(
                        searchResults = emptyList(),
                        isSearching = false,
                        searchError = "Unknown error occurred, please restart the application"
                    )
                    debugLogManager.log("ASSETS", "UI state updated with empty results due to error")
                }
                debugLogManager.log("ASSETS", "=== STOCK SEARCH IN VIEWMODEL COMPLETED ===")
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
            val twdEquivalent = if (currency == "TWD") amount else (amount * 30.0).toInt().toDouble() // Simple conversion, rounded to integer
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
    
        fun addStockAsset(symbol: String, shares: Double) {
            debugLogManager.log("ASSETS", "Adding stock asset: $symbol, $shares shares")
            viewModelScope.launch {
                // Determine if it's a Taiwan stock
                val isTaiwanStock = symbol.endsWith(".TW", ignoreCase = true) ||
                                  symbol.endsWith(".T", ignoreCase = true) ||
                                  symbol.matches(Regex("^\\d{4}$"))
                
                val currency = if (isTaiwanStock) "TWD" else "USD"
                debugLogManager.log("ASSETS", "Stock currency determined: $currency for $symbol")
                
                val stockAsset = StockAsset(
                    id = System.currentTimeMillis().toString(),
                    symbol = symbol,
                    companyName = symbol, // Simple mapping
                    shares = shares, // Keep Double type
                    market = "GLOBAL",
                    currentPrice = 0.0, // Will be fetched later
                    originalCurrency = currency, // Set correct currency
                    twdEquivalent = 0.0 // Will be calculated later
                )
                debugLogManager.log("ASSETS", "Stock asset created: $symbol, $shares shares, currency: $currency")
                assetRepository.insertStockAsset(stockAsset)
                debugLogManager.log("ASSETS", "Stock asset inserted to database")
            }
        }

        fun deleteCashAsset(asset: CashAsset) {
            debugLogManager.log("ASSETS", "Deleting cash asset: ${asset.currency} ${asset.amount}")
            viewModelScope.launch {
                assetRepository.deleteCashAsset(asset)
                debugLogManager.log("ASSETS", "Cash asset deleted successfully")
            }
        }

        fun deleteStockAsset(asset: StockAsset) {
            debugLogManager.log("ASSETS", "Deleting stock asset: ${asset.symbol}")
            viewModelScope.launch {
                assetRepository.deleteStockAsset(asset)
                debugLogManager.log("ASSETS", "Stock asset deleted successfully")
            }
        }

        fun updateCashAsset(asset: CashAsset) {
            debugLogManager.log("ASSETS", "Updating cash asset: ${asset.currency} ${asset.amount}")
            viewModelScope.launch {
                assetRepository.updateCashAsset(asset)
                debugLogManager.log("ASSETS", "Cash asset updated successfully")
            }
        }

        fun updateStockAsset(asset: StockAsset) {
            debugLogManager.log("ASSETS", "Updating stock asset: ${asset.symbol}")
            viewModelScope.launch {
                assetRepository.updateStockAsset(asset)
                debugLogManager.log("ASSETS", "Stock asset updated successfully")
            }
        }
}

data class AssetsUiState(
    val cashAssets: List<CashAsset> = emptyList(),
    val stockAssets: List<StockAsset> = emptyList(),
    val isLoading: Boolean = true,
    val searchResults: List<StockSearchItem> = emptyList(),
    val isSearching: Boolean = false,
    val searchError: String = ""
)