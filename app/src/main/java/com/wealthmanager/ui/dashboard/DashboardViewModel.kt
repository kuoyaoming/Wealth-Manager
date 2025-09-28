package com.wealthmanager.ui.dashboard

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
class DashboardViewModel @Inject constructor(
    private val assetRepository: AssetRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()
    
    init {
        observeAssets()
    }
    
    private fun observeAssets() {
        viewModelScope.launch {
            combine(
                assetRepository.getAllCashAssets(),
                assetRepository.getAllStockAssets()
            ) { cashAssets, stockAssets ->
                val totalCash = cashAssets.sumOf { it.twdEquivalent }
                val totalStock = stockAssets.sumOf { it.twdEquivalent }
                val totalAssets = totalCash + totalStock
                
                _uiState.value = _uiState.value.copy(
                    totalAssets = totalAssets,
                    cashAssets = totalCash,
                    stockAssets = totalStock,
                    assets = cashAssets + stockAssets.map { it.toAssetItem() },
                    isLoading = false
                )
            }.collect { }
        }
    }
    
    fun loadPortfolioData() {
        _uiState.value = _uiState.value.copy(isLoading = true)
        // Data will be loaded through the observeAssets() flow
    }
    
    fun refreshData() {
        _uiState.value = _uiState.value.copy(isLoading = true)
        // TODO: Implement market data refresh
        _uiState.value = _uiState.value.copy(isLoading = false)
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