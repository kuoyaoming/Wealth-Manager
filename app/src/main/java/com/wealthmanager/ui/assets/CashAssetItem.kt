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
import com.wealthmanager.data.entity.CashAsset
import com.wealthmanager.debug.DebugLogManager
import java.text.NumberFormat
import java.util.*

@Composable
fun CashAssetItem(
    asset: CashAsset,
    onEdit: (CashAsset) -> Unit,
    onDelete: (CashAsset) -> Unit,
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
                    text = "${asset.currency} ${formatCurrency(asset.amount)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "NT$ ${formatCurrency(asset.twdEquivalent)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Row {
                IconButton(
                    onClick = {
                        debugLogManager.logUserAction("Edit Cash Asset Clicked")
                        debugLogManager.log("UI", "User clicked edit button for cash asset: ${asset.currency} ${asset.amount}")
                        onEdit(asset)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Cash Asset",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                
                IconButton(
                    onClick = {
                        debugLogManager.logUserAction("Delete Cash Asset Clicked")
                        debugLogManager.log("UI", "User clicked delete button for cash asset: ${asset.currency} ${asset.amount}")
                        onDelete(asset)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Cash Asset",
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