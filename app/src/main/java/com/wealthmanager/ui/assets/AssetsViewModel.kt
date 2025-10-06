package com.wealthmanager.ui.assets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.content.Context
import com.wealthmanager.R
import com.wealthmanager.data.entity.CashAsset
import com.wealthmanager.data.entity.StockAsset
import com.wealthmanager.data.model.NoResultsReason
import com.wealthmanager.data.model.SearchErrorType
import com.wealthmanager.data.model.SearchResult
import com.wealthmanager.data.model.StockSearchItem
import com.wealthmanager.data.repository.AssetRepository
import com.wealthmanager.data.service.MarketDataService
import com.wealthmanager.debug.DebugLogManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class AssetsViewModel
    @Inject
    constructor(
        private val assetRepository: AssetRepository,
        private val marketDataService: MarketDataService,
        private val debugLogManager: DebugLogManager,
        private val context: Context,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(AssetsUiState())
        val uiState: StateFlow<AssetsUiState> = _uiState.asStateFlow()

        private val _selectedCashCurrency = MutableStateFlow("TWD") // Will be initialized with string resource
        val selectedCashCurrency: StateFlow<String> = _selectedCashCurrency.asStateFlow()

        private val _cashAmountInput = MutableStateFlow("")
        val cashAmountInput: StateFlow<String> = _cashAmountInput.asStateFlow()

        private val _cashActionButtonLabel = MutableStateFlow(R.string.add)
        val cashActionButtonLabel: StateFlow<Int> = _cashActionButtonLabel.asStateFlow()

        private var existingCashAsset: CashAsset? = null

        private val _searchQuery = MutableStateFlow("")

        private val _immediateSearch = MutableSharedFlow<String>(extraBufferCapacity = 1)

        init {
            viewModelScope.launch {
                merge(
                    _searchQuery
                        .debounce(450)
                        .map { it.trim() }
                        .filter { it.isNotEmpty() }
                        .distinctUntilChanged(),
                    _immediateSearch,
                )
                    .onEach { query ->
                        debugLogManager.log("ASSETS", "Search pipeline triggered for query: '$query'")
                        _uiState.value = _uiState.value.copy(isSearching = true, searchError = "")
                    }
                    .flatMapLatest { query ->
                        debugLogManager.log("ASSETS", "Calling marketDataService.searchStocks (merged flow)")
                        marketDataService.searchStocks(query, "US")
                    }
                    .collect { searchResult: SearchResult ->
                        when (searchResult) {
                            is SearchResult.Success -> {
                                debugLogManager.log(
                                    "ASSETS",
                                    "Search successful: ${searchResult.results.size} results found",
                                )
                                _uiState.value =
                                    _uiState.value.copy(
                                        searchResults = searchResult.results,
                                        isSearching = false,
                                        searchError = if (searchResult.results.isEmpty()) context.getString(R.string.search_error_stock_not_found) else "",
                                    )
                            }
                            is SearchResult.NoResults -> {
                                debugLogManager.log("ASSETS", "No results found: ${searchResult.reason}")
                                _uiState.value =
                                    _uiState.value.copy(
                                        searchResults = emptyList(),
                                        isSearching = false,
                                        searchError =
                                            when (searchResult.reason) {
                                                NoResultsReason.STOCK_NOT_FOUND -> context.getString(R.string.search_error_stock_not_found)
                                                NoResultsReason.API_LIMIT_REACHED -> context.getString(R.string.search_error_api_limit)
                                                NoResultsReason.NETWORK_ERROR -> context.getString(R.string.search_error_network)
                                                NoResultsReason.INVALID_QUERY -> context.getString(R.string.search_error_invalid_query)
                                                NoResultsReason.SERVER_ERROR -> context.getString(R.string.search_error_server)
                                            },
                                    )
                            }
                            is SearchResult.Error -> {
                                debugLogManager.logError(
                                    "Search error: ${searchResult.errorType}",
                                    Exception("Search failed"),
                                )
                                _uiState.value =
                                    _uiState.value.copy(
                                        searchResults = emptyList(),
                                        isSearching = false,
                                        searchError =
                                            when (searchResult.errorType) {
                                                SearchErrorType.API_LIMIT -> context.getString(R.string.search_error_api_limit)
                                                SearchErrorType.NETWORK_ERROR -> context.getString(R.string.search_error_network)
                                                SearchErrorType.SERVER_ERROR -> context.getString(R.string.search_error_server)
                                                SearchErrorType.INVALID_API_KEY -> context.getString(R.string.search_error_invalid_api_key)
                                                SearchErrorType.AUTHENTICATION_ERROR -> context.getString(R.string.search_error_authentication)
                                                SearchErrorType.RATE_LIMIT_ERROR -> context.getString(R.string.search_error_rate_limit)
                                                SearchErrorType.UNKNOWN_ERROR -> context.getString(R.string.search_error_unknown)
                                            },
                                    )
                            }
                        }
                    }
            }
        }

        fun setSearchQuery(query: String) {
            debugLogManager.log("ASSETS", "Search query changed: '$query'")
            _searchQuery.value = query
        }

        fun searchStocksNow(query: String) {
            val q = query.trim()
            if (q.isEmpty()) {
                debugLogManager.log("ASSETS", "Ignoring immediate search for empty query")
                return
            }
            debugLogManager.logUserAction("Immediate Stock Search Initiated")
            debugLogManager.log("ASSETS", "Emitting immediate search for: '$q'")
            _immediateSearch.tryEmit(q)
        }

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
                                debugLogManager.log(
                                    "ASSETS",
                                    "Search successful: ${searchResult.results.size} results found",
                                )
                                _uiState.value =
                                    _uiState.value.copy(
                                        searchResults = searchResult.results,
                                        isSearching = false,
                                        searchError = if (searchResult.results.isEmpty()) context.getString(R.string.search_error_stock_not_found) else "",
                                    )
                            }
                            is SearchResult.NoResults -> {
                                debugLogManager.log("ASSETS", "No results found: ${searchResult.reason}")
                                _uiState.value =
                                    _uiState.value.copy(
                                        searchResults = emptyList(),
                                        isSearching = false,
                                        searchError =
                                            when (searchResult.reason) {
                                                NoResultsReason.STOCK_NOT_FOUND -> context.getString(R.string.search_error_stock_not_found)
                                                NoResultsReason.API_LIMIT_REACHED -> context.getString(R.string.search_error_api_limit)
                                                NoResultsReason.NETWORK_ERROR -> context.getString(R.string.search_error_network)
                                                NoResultsReason.INVALID_QUERY -> context.getString(R.string.search_error_invalid_query)
                                                NoResultsReason.SERVER_ERROR -> context.getString(R.string.search_error_server)
                                            },
                                    )
                            }
                            is SearchResult.Error -> {
                                debugLogManager.logError(
                                    "Search error: ${searchResult.errorType}",
                                    Exception("Search failed"),
                                )
                                _uiState.value =
                                    _uiState.value.copy(
                                        searchResults = emptyList(),
                                        isSearching = false,
                                        searchError =
                                            when (searchResult.errorType) {
                                                SearchErrorType.API_LIMIT -> context.getString(R.string.search_error_api_limit)
                                                SearchErrorType.NETWORK_ERROR -> context.getString(R.string.search_error_network)
                                                SearchErrorType.SERVER_ERROR -> context.getString(R.string.search_error_server)
                                                SearchErrorType.INVALID_API_KEY -> context.getString(R.string.search_error_invalid_api_key)
                                                SearchErrorType.AUTHENTICATION_ERROR -> context.getString(R.string.search_error_authentication)
                                                SearchErrorType.RATE_LIMIT_ERROR -> context.getString(R.string.search_error_rate_limit)
                                                SearchErrorType.UNKNOWN_ERROR -> context.getString(R.string.search_error_unknown)
                                            },
                                    )
                            }
                        }
                    }
                    debugLogManager.log("ASSETS", "UI state updated with search results")
                } catch (e: Exception) {
                    debugLogManager.logError("Stock search failed: ${e.message}", e)
                    debugLogManager.log("ASSETS", "Exception during stock search: ${e::class.simpleName}")
                    _uiState.value =
                        _uiState.value.copy(
                            searchResults = emptyList(),
                            isSearching = false,
                            searchError = context.getString(R.string.search_error_unknown),
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
                    assetRepository.getAllStockAssets(),
                ) { cashAssets, stockAssets ->
                    debugLogManager.log(
                        "ASSETS",
                        "Assets loaded - Cash: ${cashAssets.size}, Stock: ${stockAssets.size}",
                    )
                    _uiState.value =
                        _uiState.value.copy(
                            cashAssets = cashAssets,
                            stockAssets = stockAssets,
                            isLoading = false,
                        )
                }.collect { }
            }
        }

        fun setSelectedCashCurrency(currency: String) {
            debugLogManager.log("ASSETS", "Selected cash currency changed to $currency")
            _selectedCashCurrency.value = currency
            loadExistingCashAsset()
        }

        private fun loadExistingCashAsset() {
            viewModelScope.launch {
                val currency = _selectedCashCurrency.value
                existingCashAsset = assetRepository.getCashAssetByCurrencySync(currency)
                if (existingCashAsset != null) {
                    val amount = existingCashAsset!!.amount
                    _cashAmountInput.value = amount.toString()
                    _cashActionButtonLabel.value = R.string.update
                } else {
                    _cashAmountInput.value = ""
                    _cashActionButtonLabel.value = R.string.add
                }
            }
        }

        fun onCashAmountChanged(value: String) {
            if (value.isEmpty() || value.matches(Regex("^\\d*\\.?\\d*"))) {
                _cashAmountInput.value = value
            }
        }

        fun addCashAsset(
            currency: String,
            amount: Double,
        ) {
            debugLogManager.log("ASSETS", "Adding cash asset: $currency $amount")
            viewModelScope.launch {
                val existing = existingCashAsset
                val twdEquivalent = if (currency == "TWD") amount else (amount * 30.0).toInt().toDouble()
                if (existing != null) {
                    val updatedAsset =
                        existing.copy(
                            amount = amount,
                            twdEquivalent = twdEquivalent,
                            lastUpdated = System.currentTimeMillis(),
                        )
                    debugLogManager.log("ASSETS", "Updating existing cash asset: ${updatedAsset.id}")
                    assetRepository.updateCashAsset(updatedAsset)
                } else {
                    val cashAsset =
                        CashAsset(
                            currency = currency,
                            amount = amount,
                            twdEquivalent = twdEquivalent,
                        )
                    debugLogManager.log("ASSETS", "Cash asset created: $currency $amount (TWD: $twdEquivalent)")
                    assetRepository.insertCashAsset(cashAsset)
                }
                loadExistingCashAsset()

                debugLogManager.log("ASSETS", "Triggering immediate price sync after cash asset addition")
                syncMarketData()
            }
        }

        fun openAddCashDialog() {
            loadExistingCashAsset()
        }

        fun addStockAsset(
            symbol: String,
            shares: Double,
        ) {
            debugLogManager.log("ASSETS", "Adding stock asset: $symbol, $shares shares")
            viewModelScope.launch {
                // Determine if it's a Taiwan stock
                val isTaiwanStock =
                    symbol.endsWith(".TW", ignoreCase = true) ||
                        symbol.endsWith(".T", ignoreCase = true) ||
                        symbol.matches(Regex("^\\d{4}$"))

                val currency = if (isTaiwanStock) "TWD" else "USD"
                debugLogManager.log("ASSETS", "Stock currency determined: $currency for $symbol")

                val stockAsset =
                    StockAsset(
                        id = System.currentTimeMillis().toString(),
                        symbol = symbol,
                        companyName = symbol, // Simple mapping
                        shares = shares, // Keep Double type
                        market = "GLOBAL",
                        currentPrice = 0.0, // Will be fetched later
                        originalCurrency = currency, // Set correct currency
                        twdEquivalent = 0.0, // Will be calculated later
                    )
                debugLogManager.log("ASSETS", "Stock asset created: $symbol, $shares shares, currency: $currency")
                assetRepository.insertStockAsset(stockAsset)
                debugLogManager.log("ASSETS", "Stock asset inserted to database")

                debugLogManager.log("ASSETS", "Triggering immediate price sync after stock asset addition")
                syncMarketData()
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

        /**
         * Sync market data (exchange rates and stock prices).
         */
        private suspend fun syncMarketData() {
            try {
                debugLogManager.log("ASSETS", "Starting immediate market data sync")

                marketDataService.updateExchangeRates()
                debugLogManager.log("ASSETS", "Exchange rates updated")

                marketDataService.updateStockPrices()
                debugLogManager.log("ASSETS", "Stock prices updated")

                debugLogManager.log("ASSETS", "Market data sync completed successfully")
            } catch (e: Exception) {
                debugLogManager.logError("ASSETS: Failed to sync market data: ${e.message}", e)
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
    val searchError: String = "",
)
