package com.wealthmanager.ui.charts

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.wealthmanager.ui.dashboard.AssetItem
import com.wealthmanager.debug.DebugLogManager
import kotlin.math.*

/**
 * Fixed PieChartComponent using system theme colors
 */
@Composable
fun PieChartComponentFixed(
    assets: List<AssetItem>,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    val debugLogManager = remember { DebugLogManager() }
    
    LaunchedEffect(assets.size) {
        debugLogManager.log("CHART", "PieChartComponent rendered with ${assets.size} assets")
        if (assets.isNotEmpty()) {
            val totalValue = assets.sumOf { it.value }
            debugLogManager.log("CHART", "Total chart value: $totalValue")
            assets.forEachIndexed { index, asset ->
                debugLogManager.log("CHART", "Asset $index: ${asset.name} = ${asset.value} (${(asset.value/totalValue*100).toInt()}%)")
            }
        }
    }
    
    Card(
        modifier = modifier.fillMaxWidth(),
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Pie Chart
                    Canvas(
                        modifier = Modifier
                            .size(150.dp)
                            .padding(8.dp)
                    ) {
                        drawPieChartFixed(assets)
                    }
                    
                    // Legend
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .height(150.dp)
                    ) {
                        items(assets) { asset ->
                            LegendItemFixed(asset)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LegendItemFixed(asset: AssetItem) {
    val color = getAssetColorFixed(asset.name)
    val percentage = if (asset.percentage > 0) "${(asset.percentage * 100).toInt()}%" else ""
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .padding(end = 8.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(
                    color = color,
                    radius = size.minDimension / 2
                )
            }
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = asset.name,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "NT$ ${formatCurrency(asset.value)} $percentage",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun DrawScope.drawPieChartFixed(assets: List<AssetItem>) {
    val total = assets.sumOf { it.value }
    if (total <= 0) return
    
    val center = Offset(size.width / 2, size.height / 2)
    val radius = minOf(size.width, size.height) / 2 - 20f
    var startAngle = -90f
    
    assets.forEach { asset ->
        val sweepAngle = (asset.value / total * 360f).toFloat()
        val color = getAssetColorFixed(asset.name)
        
        drawArc(
            color = color,
            startAngle = startAngle,
            sweepAngle = sweepAngle,
            useCenter = true,
            topLeft = Offset(
                center.x - radius,
                center.y - radius
            ),
            size = Size(radius * 2, radius * 2)
        )
        
        startAngle += sweepAngle
    }
}

/**
 * Get asset color using predefined colors (not using MaterialTheme in non-composable context)
 */
private fun getAssetColorFixed(assetName: String): Color {
    // Use predefined colors that work well with both light and dark themes
    val colors = listOf(
        Color(0xFF2196F3), // Blue
        Color(0xFF4CAF50), // Green
        Color(0xFFFF9800), // Orange
        Color(0xFF9C27B0), // Purple
        Color(0xFFF44336), // Red
        Color(0xFF00BCD4), // Cyan
        Color(0xFFFFEB3B), // Yellow
        Color(0xFF795548)  // Brown
    )
    
    val index = assetName.hashCode().mod(colors.size)
    return colors[abs(index)]
}

private fun formatCurrency(amount: Double): String {
    return when {
        amount >= 1_000_000 -> "${(amount / 1_000_000).toInt()}M"
        amount >= 1_000 -> "${(amount / 1_000).toInt()}K"
        else -> amount.toInt().toString()
    }
}
