package com.wealthmanager.ui.assets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wealthmanager.R
import com.wealthmanager.data.model.SearchResult
import com.wealthmanager.data.model.StockSearchItem
import com.wealthmanager.data.repository.AssetRepository
import com.wealthmanager.data.service.MarketDataService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class AddAssetViewModel @Inject constructor(
    private val assetRepository: AssetRepository,
    private val marketDataService: MarketDataService,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddAssetUiState())
    val uiState = _uiState.asStateFlow()

    private val searchQuery = MutableStateFlow("")

    init {
        viewModelScope.launch {
            searchQuery
                .debounce(300)
                .filter { it.isNotBlank() && it.length > 1 }
                .distinctUntilChanged()
                .flatMapLatest { query ->
                    _uiState.update { it.copy(isSearching = true) }
                    marketDataService.searchStocks(query, "US")
                }
                .collect { result ->
                    val searchResults = if (result is SearchResult.Success) result.results else emptyList()
                    _uiState.update { it.copy(isSearching = false, searchResults = searchResults) }
                }
        }
    }

    fun onTabSelected(tabIndex: Int) {
        _uiState.update { it.copy(selectedTab = tabIndex) }
    }

    fun onCashCurrencyChange(currency: String) {
        _uiState.update { it.copy(cashCurrency = currency) }
    }

    fun onCashAmountChange(amount: String) {
        if (amount.isEmpty() || amount.matches(Regex("^\\d*\\.?\\d*$"))) {
            _uiState.update { it.copy(cashAmount = amount) }
        }
    }

    fun onStockSymbolChange(symbol: String) {
        _uiState.update { it.copy(stockSymbol = symbol) }
        searchQuery.value = symbol
    }

    fun onStockSharesChange(shares: String) {
        _uiState.update { it.copy(stockShares = shares) }
    }

    fun onSearchResultSelected(symbol: String) {
        _uiState.update { it.copy(stockSymbol = symbol, searchResults = emptyList()) }
        searchQuery.value = "" // Clear search query to hide results
    }

    fun addCashAsset() {
        viewModelScope.launch {
            val state = _uiState.value
            val amount = state.cashAmount.toDoubleOrNull() ?: return@launch
            // TODO: Replace with a more accurate, real-time conversion from MarketDataService
            val twdEquivalent = if (state.cashCurrency == "TWD") amount else amount * 30.0
            assetRepository.insertCashAsset(
                com.wealthmanager.data.entity.CashAsset(
                    currency = state.cashCurrency,
                    amount = amount,
                    twdEquivalent = twdEquivalent
                )
            )
        }
    }

    fun addStockAsset() {
        viewModelScope.launch {
            val state = _uiState.value
            val shares = state.stockShares.toDoubleOrNull() ?: return@launch
            // You might want to fetch company name from search results
            assetRepository.insertStockAsset(
                com.wealthmanager.data.entity.StockAsset(
                    symbol = state.stockSymbol,
                    shares = shares,
                    companyName = state.stockSymbol, // Placeholder
                    market = "US", // Placeholder
                    originalCurrency = "USD" // Placeholder
                )
            )
        }
    }
}

data class AddAssetUiState(
    val selectedTab: Int = 0,
    val cashCurrency: String = "TWD",
    val cashAmount: String = "",
    val stockSymbol: String = "",
    val stockShares: String = "",
    val searchResults: List<StockSearchItem> = emptyList(),
    val isSearching: Boolean = false,
)
