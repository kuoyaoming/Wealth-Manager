package com.wealthmanager.ui.assets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.wealthmanager.R
import com.wealthmanager.data.entity.StockAsset
import com.wealthmanager.debug.DebugLogManager
import com.wealthmanager.haptic.HapticFeedbackManager
import com.wealthmanager.haptic.rememberHapticFeedbackWithView
import com.wealthmanager.utils.MoneyFormatter
import com.wealthmanager.utils.rememberMoneyText

@Composable
fun StockAssetItem(
    asset: StockAsset,
    onEdit: (StockAsset) -> Unit,
    onDelete: (StockAsset) -> Unit,
    modifier: Modifier = Modifier,
) {
    val debugLogManager = remember { DebugLogManager() }
    val (hapticManager, view) = rememberHapticFeedbackWithView()

    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text =
                        stringResource(
                            R.string.assets_stock_symbol_market,
                            asset.symbol,
                            asset.market,
                        ),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                )
                Text(
                    text =
                        stringResource(
                            R.string.assets_stock_shares_price,
                            rememberMoneyText(
                                asset.shares,
                                "USD",
                                style = MoneyFormatter.Style.NumberOnly,
                                maxFractionDigits = 2,
                            ),
                            rememberMoneyText(
                                asset.currentPrice,
                                asset.originalCurrency,
                                style = MoneyFormatter.Style.CurrencyCode,
                                moneyContext = MoneyFormatter.MoneyContext.StockPrice,
                            ),
                        ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text =
                        stringResource(
                            R.string.assets_cash_twd_value,
                            rememberMoneyText(
                                asset.twdEquivalent,
                                "TWD",
                                style = MoneyFormatter.Style.CurrencyCode,
                                moneyContext = MoneyFormatter.MoneyContext.Total,
                            ),
                        ),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary,
                )
            }

            Row {
                IconButton(
                    onClick = {
                        debugLogManager.logUserAction("Edit Stock Asset Clicked")
                        debugLogManager.log("UI", "User clicked edit button for stock asset: ${asset.symbol}")
                        hapticManager.triggerHaptic(view, HapticFeedbackManager.HapticIntensity.MEDIUM)
                        onEdit(asset)
                    },
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = stringResource(R.string.cd_edit_stock_asset),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }

                IconButton(
                    onClick = {
                        debugLogManager.logUserAction("Delete Stock Asset Clicked")
                        debugLogManager.log("UI", "User clicked delete button for stock asset: ${asset.symbol}")
                        hapticManager.triggerHaptic(view, HapticFeedbackManager.HapticIntensity.STRONG)
                        onDelete(asset)
                    },
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = stringResource(R.string.cd_delete_stock_asset),
                        tint = MaterialTheme.colorScheme.error,
                    )
                }
            }
        }
    }
}

// Removed local formatter; unified via MoneyFormatter
