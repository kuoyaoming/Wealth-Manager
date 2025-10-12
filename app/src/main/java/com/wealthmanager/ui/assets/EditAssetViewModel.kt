package com.wealthmanager.ui.assets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wealthmanager.data.entity.CashAsset
import com.wealthmanager.data.entity.StockAsset
import com.wealthmanager.data.repository.AssetRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EditAssetUiState(
    val asset: Any? = null, // Can be CashAsset or StockAsset
    val amount: String = "",
    val shares: String = "",
    val currency: String = "TWD",
)

@HiltViewModel
class EditAssetViewModel @Inject constructor(
    private val assetRepository: AssetRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditAssetUiState())
    val uiState = _uiState.asStateFlow()

    fun loadAsset(assetToEdit: Any) {
        when (assetToEdit) {
            is CashAsset -> {
                _uiState.update {
                    it.copy(
                        asset = assetToEdit,
                        amount = assetToEdit.amount.toString(),
                        currency = assetToEdit.currency
                    )
                }
            }
            is StockAsset -> {
                _uiState.update {
                    it.copy(
                        asset = assetToEdit,
                        shares = assetToEdit.shares.toString()
                    )
                }
            }
        }
    }

    fun onAmountChange(newAmount: String) {
        if (newAmount.isEmpty() || newAmount.matches(Regex("^\\d*\\.?\\d*$"))) {
            _uiState.update { it.copy(amount = newAmount) }
        }
    }

    fun onSharesChange(newShares: String) {
        if (newShares.isEmpty() || newShares.matches(Regex("^\\d*\\.?\\d*$"))) {
            _uiState.update { it.copy(shares = newShares) }
        }
    }

    fun onCurrencyChange(newCurrency: String) {
        _uiState.update { it.copy(currency = newCurrency) }
    }

    fun onSave(onSuccess: () -> Unit) {
        viewModelScope.launch {
            when (val asset = _uiState.value.asset) {
                is CashAsset -> {
                    val newAmount = _uiState.value.amount.toDoubleOrNull() ?: return@launch
                    val updatedAsset = asset.copy(
                        amount = newAmount,
                        currency = _uiState.value.currency,
                        // Recalculate TWD equivalent if necessary
                    )
                    assetRepository.updateCashAsset(updatedAsset)
                }
                is StockAsset -> {
                    val newShares = _uiState.value.shares.toDoubleOrNull() ?: return@launch
                    val updatedAsset = asset.copy(shares = newShares)
                    assetRepository.updateStockAsset(updatedAsset)
                }
            }
            onSuccess()
        }
    }
}
