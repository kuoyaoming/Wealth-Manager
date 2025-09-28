package com.wealthmanager.ui.assets

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.wealthmanager.data.entity.StockAsset
import com.wealthmanager.debug.DebugLogManager
import java.text.NumberFormat
import java.util.*

@Composable
fun StockAssetItem(
    asset: StockAsset,
    onEdit: (StockAsset) -> Unit,
    onDelete: (StockAsset) -> Unit,
    modifier: Modifier = Modifier
) {
    val debugLogManager = remember { DebugLogManager() }
    
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${asset.symbol} (${asset.market})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "${asset.shares} shares @ ${formatCurrency(asset.currentPrice)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "NT$ ${formatCurrency(asset.twdEquivalent)}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            Row {
                IconButton(
                    onClick = {
                        debugLogManager.logUserAction("Edit Stock Asset Clicked")
                        debugLogManager.log("UI", "User clicked edit button for stock asset: ${asset.symbol}")
                        onEdit(asset)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Stock Asset",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                
                IconButton(
                    onClick = {
                        debugLogManager.logUserAction("Delete Stock Asset Clicked")
                        debugLogManager.log("UI", "User clicked delete button for stock asset: ${asset.symbol}")
                        onDelete(asset)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Stock Asset",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

private fun formatCurrency(amount: Double): String {
    val formatter = NumberFormat.getNumberInstance(Locale.US)
    formatter.maximumFractionDigits = 2
    return formatter.format(amount)
}