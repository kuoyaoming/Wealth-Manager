package com.wealthmanager.ui.assets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wealthmanager.data.entity.CashAsset
import com.wealthmanager.data.entity.StockAsset
import com.wealthmanager.data.repository.AssetRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AssetsViewModel @Inject constructor(
    private val assetRepository: AssetRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(AssetsUiState())
    val uiState: StateFlow<AssetsUiState> = _uiState.asStateFlow()
    
    fun loadAssets() {
        viewModelScope.launch {
            combine(
                assetRepository.getAllCashAssets(),
                assetRepository.getAllStockAssets()
            ) { cashAssets, stockAssets ->
                _uiState.value = _uiState.value.copy(
                    cashAssets = cashAssets,
                    stockAssets = stockAssets,
                    isLoading = false
                )
            }.collect { }
        }
    }
    
    fun addCashAsset(currency: String, amount: Double) {
        viewModelScope.launch {
            val cashAsset = CashAsset(
                id = System.currentTimeMillis().toString(),
                currency = currency,
                amount = amount,
                twdEquivalent = if (currency == "TWD") amount else amount * 30.0 // Simple conversion
            )
            assetRepository.insertCashAsset(cashAsset)
        }
    }
    
    fun addStockAsset(symbol: String, shares: Double, market: String) {
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
            assetRepository.insertStockAsset(stockAsset)
        }
    }
}

data class AssetsUiState(
    val cashAssets: List<CashAsset> = emptyList(),
    val stockAssets: List<StockAsset> = emptyList(),
    val isLoading: Boolean = true
)