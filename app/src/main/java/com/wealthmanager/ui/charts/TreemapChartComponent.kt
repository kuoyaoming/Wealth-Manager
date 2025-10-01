package com.wealthmanager.ui.charts

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wealthmanager.ui.charts.treemap.TreemapLayout
import com.wealthmanager.ui.charts.treemap.TreemapRect
import com.wealthmanager.ui.dashboard.AssetItem
import com.wealthmanager.R
import com.wealthmanager.debug.DebugLogManager
import com.wealthmanager.utils.MoneyFormatter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.*

/**
 * Treemap chart component using squarified algorithm
 * Each rectangle size represents the asset value proportion
 */
@Composable
fun TreemapChartComponent(
    assets: List<AssetItem>,
    isLoading: Boolean,
    onAssetClick: (AssetItem) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val debugLogManager = remember { DebugLogManager() }
    
    LaunchedEffect(assets.size) {
        debugLogManager.log("CHART", "TreemapChartComponent rendered with ${assets.size} assets")
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
                text = "Portfolio Distribution",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
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
                        text = "No assets available",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Treemap Chart
                    TreemapCanvas(
                        assets = assets,
                        onAssetClick = onAssetClick,
                        modifier = Modifier
                            .size(200.dp)
                            .padding(8.dp)
                    )
                    
                    // Legend
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .height(200.dp)
                    ) {
                        items(assets) { asset ->
                            TreemapLegendItem(asset)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TreemapCanvas(
    assets: List<AssetItem>,
    onAssetClick: (AssetItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    
    // Compute treemap layout
    val treemapRects by remember(assets) {
        derivedStateOf {
            TreemapLayout.computeTreemapRects(
                assets = assets,
                width = 200f,
                height = 200f
            )
        }
    }
    
    Canvas(
        modifier = modifier
            .semantics {
                contentDescription = "Treemap chart showing portfolio distribution"
            }
    ) {
        drawTreemap(treemapRects, onAssetClick)
    }
}

private fun DrawScope.drawTreemap(
    rects: List<TreemapRect>,
    onAssetClick: (AssetItem) -> Unit
) {
    rects.forEach { rect ->
        val color = getAssetColor(rect.asset.name)
        
        // Draw rectangle background
        drawRect(
            color = color,
            topLeft = Offset(rect.x, rect.y),
            size = Size(rect.width, rect.height)
        )
        
        // Draw border
        drawRect(
            color = Color.Black.copy(alpha = 0.1f),
            topLeft = Offset(rect.x, rect.y),
            size = Size(rect.width, rect.height),
            style = Stroke(width = 1.dp.toPx())
        )
        
        // For now, skip text drawing to avoid Canvas complexity
        // TODO: Implement proper text drawing with Compose Canvas
    }
}

@Composable
private fun TreemapLegendItem(asset: AssetItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Color indicator
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(
                    color = getAssetColor(asset.name),
                    shape = MaterialTheme.shapes.small
                )
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        // Asset info
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = asset.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "$${asset.value.toInt()}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        // Percentage
        Text(
            text = "${asset.percentage.toInt()}%",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Get asset color using predefined colors (consistent with pie chart)
 */
private fun getAssetColor(assetName: String): Color {
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
