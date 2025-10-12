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
import com.wealthmanager.ui.components.PrimaryCard
import com.wealthmanager.ui.components.SecondaryCard
import com.wealthmanager.ui.responsive.rememberResponsiveLayout
import com.wealthmanager.utils.rememberMoneyText

/**
 * A card that displays the total value of all assets.
 */
@Composable
fun TotalAssetsCard(
    totalValue: Double,
    isLoading: Boolean,
    currency: String,
    exchangeRate: Double,
) {
    val responsiveLayout = rememberResponsiveLayout()
    val displayValue = if (currency == "USD") totalValue / exchangeRate else totalValue

    PrimaryCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.total_assets),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(responsiveLayout.paddingMedium))
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(if (responsiveLayout.isTablet) 40.dp else 32.dp))
            } else {
                Text(
                    text = rememberMoneyText(displayValue, currency),
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

/**
 * A card that displays a breakdown of a single asset category (e.g., Cash, Stocks).
 */
@Composable
fun AssetBreakdownCard(
    title: String,
    value: Double,
    percentage: Double,
    isLoading: Boolean,
    currency: String,
    exchangeRate: Double,
) {
    val displayValue = if (currency == "USD") value / exchangeRate else value

    SecondaryCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(vertical = 8.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(8.dp))
            if (isLoading) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.loading),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            } else {
                Text(
                    text = "${rememberMoneyText(displayValue, currency)} (${String.format("%.1f%%", percentage)})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                )
            }
        }
    }
}
