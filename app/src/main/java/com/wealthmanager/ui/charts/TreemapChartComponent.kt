package com.wealthmanager.ui.charts

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.derivedStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wealthmanager.R
import com.wealthmanager.debug.DebugLogManager
import com.wealthmanager.ui.charts.treemap.TreemapLayout
import com.wealthmanager.ui.dashboard.AssetItem
import com.wealthmanager.utils.PerformanceTracker
import java.util.Locale

/**
 * A Treemap chart component styled after Google Finance.
 * It visualizes assets where the size of each box represents its value proportion
 * and the color represents its daily performance.
 */
@Composable
fun TreemapChartComponent(
    assets: List<AssetItem>,
    isLoading: Boolean,
    onAssetClick: (AssetItem) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    PerformanceTracker("TreemapChartComponent") {
        val debugLogManager = remember { DebugLogManager() }

        LaunchedEffect(assets.size) {
            if (assets.isNotEmpty()) {
                debugLogManager.log("CHART", "TreemapChart rendered with ${assets.size} assets.")
            }
        }

        Card(
            modifier = modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
            ) {
                Text(
                    text = stringResource(R.string.chart_portfolio_distribution),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (isLoading) {
                    Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else if (assets.isEmpty()) {
                    Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = stringResource(R.string.chart_no_assets_available),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                } else {
                    TreemapCompose(
                        assets = assets,
                        onAssetClick = onAssetClick,
                        modifier = Modifier.fillMaxWidth().height(300.dp).padding(vertical = 8.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun TreemapCompose(
    assets: List<AssetItem>,
    onAssetClick: (AssetItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    val contentDescription = stringResource(R.string.chart_treemap_content_description)
    val othersGroupName = stringResource(R.string.chart_others_group)

    BoxWithConstraints(
        modifier = modifier.semantics { this.contentDescription = contentDescription },
    ) {
        val containerWidth = maxWidth.value * density.density
        val containerHeight = maxHeight.value * density.density

        val treemapRects by remember(assets, containerWidth, containerHeight) {
            derivedStateOf {
                TreemapLayout.computeTreemapRects(
                    assets = assets,
                    width = containerWidth,
                    height = containerHeight,
                    spacing = 8f, // Tighter spacing for a cleaner look
                    othersGroupName = othersGroupName,
                )
            }
        }

        treemapRects.forEach { rect ->
            val asset = rect.asset
            val backgroundColor = getTreemapColor(asset)

            Box(
                modifier = Modifier
                    .offset(x = with(density) { rect.x.toDp() }, y = with(density) { rect.y.toDp() })
                    .size(width = with(density) { rect.width.toDp() }, height = with(density) { rect.height.toDp() })
                    .clip(RoundedCornerShape(8.dp)) // Slightly rounded corners
                    .background(backgroundColor)
                    .clickable { onAssetClick(asset) }
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                    Text(
                        text = asset.name,
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    // Only show change for stocks, not for cash
                    if (asset.dayChangePercentage != 0.0) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = String.format(Locale.US, "%+.2f%%", asset.dayChangePercentage),
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

/**
 * Determines the color of a treemap box based on the asset's daily change percentage.
 * Mirrors the Google Finance color scheme, but uses theme color for cash.
 */
@Composable
private fun getTreemapColor(asset: AssetItem): Color {
    // Cash assets (which have 0 change) use the primary theme color.
    if (asset.dayChangePercentage == 0.0) {
        return MaterialTheme.colorScheme.primary
    }

    return when {
        asset.dayChangePercentage <= -2.5 -> Color(0xFFC62828) // Deep Red
        asset.dayChangePercentage <= -1.5 -> Color(0xFFE53935) // Red
        asset.dayChangePercentage < 0 -> Color(0xFFEF5350)     // Light Red
        asset.dayChangePercentage <= 1.5 -> Color(0xFF66BB6A)  // Light Green
        asset.dayChangePercentage <= 2.5 -> Color(0xFF43A047)  // Green
        else -> Color(0xFF2E7D32)           // Deep Green
    }
}
