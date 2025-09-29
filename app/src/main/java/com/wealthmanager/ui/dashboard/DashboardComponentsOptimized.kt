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
import com.wealthmanager.ui.charts.PieChartComponentFixed
import com.wealthmanager.ui.responsive.rememberResponsiveLayout
import com.wealthmanager.ui.theme.ColorGuidelines
import java.text.NumberFormat
import java.util.*

/**
 * Optimized dashboard components with proper theme color usage
 */
@Composable
fun TotalAssetsCardOptimized(
    totalValue: Double,
    isLoading: Boolean
) {
    val responsiveLayout = rememberResponsiveLayout()
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (responsiveLayout.isTablet) 6.dp else 4.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
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
                    modifier = Modifier.size(if (responsiveLayout.isTablet) 40.dp else 32.dp),
                    color = MaterialTheme.colorScheme.primary
                )
            } else {
                Text(
                    text = "NT$ ${formatCurrency(totalValue)}",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
fun CashAssetsCardOptimized(
    cashValue: Double,
    isLoading: Boolean
) {
    val responsiveLayout = rememberResponsiveLayout()
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (responsiveLayout.isTablet) 4.dp else 2.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(responsiveLayout.paddingMedium)
        ) {
            Text(
                text = stringResource(R.string.cash_assets),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            if (isLoading) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Loading...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                Text(
                    text = "NT$ ${formatCurrency(cashValue)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
fun StockAssetsCardOptimized(
    stockValue: Double,
    isLoading: Boolean
) {
    val responsiveLayout = rememberResponsiveLayout()
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (responsiveLayout.isTablet) 4.dp else 2.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(responsiveLayout.paddingMedium)
        ) {
            Text(
                text = stringResource(R.string.stock_assets),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            if (isLoading) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Loading...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                Text(
                    text = "NT$ ${formatCurrency(stockValue)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
fun PieChartCardOptimized(
    assets: List<AssetItem>,
    isLoading: Boolean
) {
    PieChartComponentFixed(
        assets = assets,
        isLoading = isLoading
    )
}

/**
 * Financial value display with proper color coding
 */
@Composable
fun FinancialValueDisplay(
    value: Double,
    isPositive: Boolean? = null,
    isLoading: Boolean = false
) {
    val financialColors = ColorGuidelines.getFinancialColors()
    val color = when {
        isLoading -> MaterialTheme.colorScheme.onSurfaceVariant
        isPositive == true -> financialColors.positive
        isPositive == false -> financialColors.negative
        else -> MaterialTheme.colorScheme.onSurface
    }
    
    Text(
        text = "NT$ ${formatCurrency(value)}",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Medium,
        color = color
    )
}

/**
 * Status indicator with theme colors
 */
@Composable
fun StatusIndicator(
    status: String,
    isSuccess: Boolean = false,
    isError: Boolean = false,
    isWarning: Boolean = false
) {
    val financialColors = ColorGuidelines.getFinancialColors()
    val color = when {
        isError -> financialColors.negative
        isWarning -> financialColors.warning
        isSuccess -> financialColors.positive
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    
    Text(
        text = status,
        style = MaterialTheme.typography.bodyMedium,
        color = color
    )
}

private fun formatCurrency(amount: Double): String {
    val formatter = NumberFormat.getNumberInstance(Locale.getDefault())
    formatter.maximumFractionDigits = 0
    return formatter.format(amount)
}