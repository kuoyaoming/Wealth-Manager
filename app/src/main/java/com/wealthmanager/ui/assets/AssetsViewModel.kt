package com.wealthmanager.ui.assets

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
class AssetsViewModel @Inject constructor(
    private val assetRepository: AssetRepository,
    private val marketDataService: MarketDataService,
    private val debugLogManager: DebugLogManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AssetsUiState())
    val uiState: StateFlow<AssetsUiState> = _uiState.asStateFlow()

    fun loadAssets() {
        debugLogManager.log("ASSETS_VM", "Loading assets...")
        viewModelScope.launch {
            combine(
                assetRepository.getAllCashAssets(),
                assetRepository.getAllStockAssets(),
            ) { cashAssets, stockAssets ->
                _uiState.value = _uiState.value.copy(
                    cashAssets = cashAssets,
                    stockAssets = stockAssets,
                    isLoading = false,
                )
            }.collect { }
        }
    }

    fun addCashAsset(currency: String, amount: Double) {
        viewModelScope.launch {
            val existingAsset = assetRepository.getCashAssetByCurrencySync(currency)
            val twdEquivalent = if (currency == "TWD") amount else (amount * 30.0) // Simplified conversion

            val assetToSave = existingAsset?.copy(
                amount = amount,
                twdEquivalent = twdEquivalent,
                lastUpdated = System.currentTimeMillis()
            ) ?: CashAsset(
                currency = currency,
                amount = amount,
                twdEquivalent = twdEquivalent,
            )
            assetRepository.insertCashAsset(assetToSave)
            syncMarketData()
        }
    }

    fun addStockAsset(symbol: String, shares: Double) {
        viewModelScope.launch {
            val isTaiwanStock = symbol.endsWith(".TW", ignoreCase = true) || symbol.matches(Regex("^\\d{4}$"))
            val currency = if (isTaiwanStock) "TWD" else "USD"

            val stockAsset = StockAsset(
                symbol = symbol,
                companyName = symbol, // Placeholder, should be resolved via search
                shares = shares,
                market = "GLOBAL", // Placeholder
                originalCurrency = currency
            )
            assetRepository.insertStockAsset(stockAsset)
            syncMarketData()
        }
    }

    fun updateCashAsset(asset: CashAsset) {
        viewModelScope.launch {
            assetRepository.updateCashAsset(asset)
        }
    }

    fun updateStockAsset(asset: StockAsset) {
        viewModelScope.launch {
            assetRepository.updateStockAsset(asset)
        }
    }

    fun deleteCashAsset(asset: CashAsset) {
        viewModelScope.launch {
            assetRepository.deleteCashAsset(asset)
        }
    }

    fun deleteStockAsset(asset: StockAsset) {
        viewModelScope.launch {
            assetRepository.deleteStockAsset(asset)
        }
    }

    private suspend fun syncMarketData() {
        try {
            marketDataService.updateExchangeRates()
            marketDataService.updateStockPrices()
        } catch (e: Exception) {
            debugLogManager.logError("ASSETS_VM: Failed to sync market data", e)
        }
    }
}

data class AssetsUiState(
    val cashAssets: List<CashAsset> = emptyList(),
    val stockAssets: List<StockAsset> = emptyList(),
    val isLoading: Boolean = true,
)
