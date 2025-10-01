package com.wealthmanager.ui.charts.treemap

import com.wealthmanager.ui.dashboard.AssetItem
import kotlin.math.*

/**
 * Data class representing a rectangle in the treemap
 */
data class TreemapRect(
    val asset: AssetItem,
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float,
    val isOthers: Boolean = false
) {
    val area: Float get() = width * height
    val centerX: Float get() = x + width / 2
    val centerY: Float get() = y + height / 2
}

/**
 * Squarified treemap algorithm implementation
 * Based on D3's treemap squarify algorithm
 */
object TreemapLayout {
    
    private const val MAX_VISIBLE_ITEMS = 8
    private const val MIN_RECT_SIZE = 20f // Minimum size for a rectangle to be visible
    
    /**
     * Compute treemap rectangles using squarified algorithm
     */
    fun computeTreemapRects(
        assets: List<AssetItem>,
        width: Float,
        height: Float
    ): List<TreemapRect> {
        if (assets.isEmpty() || width <= 0 || height <= 0) return emptyList()
        
        val totalValue = assets.sumOf { it.value }
        if (totalValue <= 0) return emptyList()
        
        // Sort assets by value (descending)
        val sortedAssets = assets.sortedByDescending { it.value }
        
        // Determine which items to show individually vs merge into "Others"
        val (visibleItems, othersItems) = if (sortedAssets.size <= MAX_VISIBLE_ITEMS) {
            sortedAssets to emptyList()
        } else {
            val visible = sortedAssets.take(MAX_VISIBLE_ITEMS - 1) // Reserve one slot for "Others"
            val others = sortedAssets.drop(MAX_VISIBLE_ITEMS - 1)
            visible to others
        }
        
        val itemsToLayout = if (othersItems.isNotEmpty()) {
            val othersValue = othersItems.sumOf { it.value }
            val othersAsset = AssetItem(
                id = "others",
                name = "Others",
                value = othersValue
            )
            visibleItems + othersAsset
        } else {
            visibleItems
        }
        
        // Convert to normalized values (0-1 range)
        val normalizedItems = itemsToLayout.map { asset ->
            NormalizedItem(asset, asset.value / totalValue)
        }
        
        // Apply squarified algorithm
        val rectangles = mutableListOf<TreemapRect>()
        squarify(normalizedItems, 0f, 0f, width, height, rectangles)
        
        return rectangles
    }
    
    private data class NormalizedItem(
        val asset: AssetItem,
        val normalizedValue: Double
    )
    
    private fun squarify(
        items: List<NormalizedItem>,
        x: Float,
        y: Float,
        width: Float,
        height: Float,
        result: MutableList<TreemapRect>
    ) {
        if (items.isEmpty()) return
        
        val totalValue = items.sumOf { it.normalizedValue }
        if (totalValue <= 0) return
        
        // Determine layout direction (horizontal or vertical)
        val isHorizontal = width >= height
        
        if (isHorizontal) {
            layoutHorizontally(items, x, y, width, height, result)
        } else {
            layoutVertically(items, x, y, width, height, result)
        }
    }
    
    private fun layoutHorizontally(
        items: List<NormalizedItem>,
        x: Float,
        y: Float,
        width: Float,
        height: Float,
        result: MutableList<TreemapRect>
    ) {
        val totalValue = items.sumOf { it.normalizedValue }
        var currentX = x
        
        for (item in items) {
            val itemWidth = (item.normalizedValue / totalValue * width).toFloat()
            
            if (itemWidth >= MIN_RECT_SIZE) {
                result.add(
                    TreemapRect(
                        asset = item.asset,
                        x = currentX,
                        y = y,
                        width = itemWidth,
                        height = height,
                        isOthers = item.asset.name == "Others"
                    )
                )
            }
            
            currentX += itemWidth
        }
    }
    
    private fun layoutVertically(
        items: List<NormalizedItem>,
        x: Float,
        y: Float,
        width: Float,
        height: Float,
        result: MutableList<TreemapRect>
    ) {
        val totalValue = items.sumOf { it.normalizedValue }
        var currentY = y
        
        for (item in items) {
            val itemHeight = (item.normalizedValue / totalValue * height).toFloat()
            
            if (itemHeight >= MIN_RECT_SIZE) {
                result.add(
                    TreemapRect(
                        asset = item.asset,
                        x = x,
                        y = currentY,
                        width = width,
                        height = itemHeight,
                        isOthers = item.asset.name == "Others"
                    )
                )
            }
            
            currentY += itemHeight
        }
    }
    
    /**
     * Calculate the aspect ratio of a rectangle
     */
    fun aspectRatio(width: Float, height: Float): Float {
        return maxOf(width / height, height / width)
    }
    
    /**
     * Check if a rectangle is large enough to display text
     */
    fun canDisplayText(rect: TreemapRect, minTextSize: Float = 12f): Boolean {
        return rect.width >= minTextSize * 3 && rect.height >= minTextSize * 1.5f
    }
}
