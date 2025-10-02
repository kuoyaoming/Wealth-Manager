package com.wealthmanager.ui.assets

import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.wealthmanager.R
import com.wealthmanager.data.entity.CashAsset
import com.wealthmanager.data.entity.StockAsset
import androidx.compose.runtime.CompositionLocalProvider
import com.wealthmanager.utils.MoneyFormatter
import com.wealthmanager.utils.rememberMoneyText

@Composable
fun CashAssetItem(
    asset: CashAsset,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = stringResource(R.string.assets_cash_title, asset.currency),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = stringResource(
                        R.string.assets_cash_original_amount,
                        rememberMoneyText(
                            asset.amount,
                            asset.currency,
                            style = MoneyFormatter.Style.CurrencyCode,
                            moneyContext = MoneyFormatter.MoneyContext.CashAmount
                        ),
                        asset.currency
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = stringResource(
                        R.string.assets_cash_twd_value,
                        rememberMoneyText(
                            asset.twdEquivalent,
                            "TWD",
                            style = MoneyFormatter.Style.CurrencyCode,
                            moneyContext = MoneyFormatter.MoneyContext.Total
                        )
                    ),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Row {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = stringResource(R.string.edit))
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.delete))
                }
            }
        }
    }
}

@Composable
fun StockAssetItem(
    asset: StockAsset,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = stringResource(
                        R.string.assets_stock_company_symbol,
                        asset.companyName,
                        asset.symbol
                    ),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = stringResource(
                        R.string.assets_stock_shares_price,
                        rememberMoneyText(
                            asset.shares,
                            "USD",
                            style = MoneyFormatter.Style.NumberOnly,
                            maxFractionDigits = 2
                        ),
                        rememberMoneyText(
                            asset.currentPrice,
                            asset.originalCurrency,
                            style = MoneyFormatter.Style.CurrencyCode,
                            moneyContext = MoneyFormatter.MoneyContext.StockPrice
                        )
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = stringResource(
                        R.string.assets_cash_twd_value,
                        rememberMoneyText(
                            asset.twdEquivalent,
                            "TWD",
                            style = MoneyFormatter.Style.CurrencyCode,
                            moneyContext = MoneyFormatter.MoneyContext.Total
                        )
                    ),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Row {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = stringResource(R.string.edit))
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.delete))
                }
            }
        }
    }
}

// Removed local formatter; unified via MoneyFormatter
