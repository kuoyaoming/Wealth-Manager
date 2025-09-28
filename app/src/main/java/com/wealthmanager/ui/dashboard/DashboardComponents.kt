package com.wealthmanager.ui.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.wealthmanager.R
import com.wealthmanager.ui.charts.PieChartComponent
import com.wealthmanager.ui.responsive.rememberResponsiveLayout
import java.text.NumberFormat
import java.util.*

@Composable
fun TotalAssetsCard(
    totalValue: Double,
    isLoading: Boolean
) {
    val responsiveLayout = rememberResponsiveLayout()
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (responsiveLayout.isTablet) 6.dp else 4.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(responsiveLayout.paddingLarge),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.total_assets),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(responsiveLayout.paddingMedium))
            
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(if (responsiveLayout.isTablet) 40.dp else 32.dp)
                )
            } else {
                Text(
                    text = "NT$ ${formatCurrency(totalValue)}",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun CashAssetsCard(
    cashValue: Double,
    isLoading: Boolean
) {
    val responsiveLayout = rememberResponsiveLayout()
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (responsiveLayout.isTablet) 4.dp else 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(responsiveLayout.paddingLarge),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.cash_assets),
                style = MaterialTheme.typography.titleMedium
            )
            
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(if (responsiveLayout.isTablet) 24.dp else 20.dp)
                )
            } else {
                Text(
                    text = "NT$ ${formatCurrency(cashValue)}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun StockAssetsCard(
    stockValue: Double,
    isLoading: Boolean
) {
    val responsiveLayout = rememberResponsiveLayout()
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (responsiveLayout.isTablet) 4.dp else 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(responsiveLayout.paddingLarge),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.stock_assets),
                style = MaterialTheme.typography.titleMedium
            )
            
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(if (responsiveLayout.isTablet) 24.dp else 20.dp)
                )
            } else {
                Text(
                    text = "NT$ ${formatCurrency(stockValue)}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun PieChartCard(
    assets: List<AssetItem>,
    isLoading: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Asset Distribution",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (assets.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No assets to display",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                PieChartComponent(
                    assets = assets,
                    isLoading = false
                )
            }
        }
    }
}

private fun formatCurrency(amount: Double): String {
    val formatter = NumberFormat.getNumberInstance(Locale.US)
    formatter.maximumFractionDigits = 0
    return formatter.format(amount)
}