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
        height: Float,
        spacing: Float = 0f,
        othersGroupName: String = "Others"
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
            val othersPercentage = othersValue / totalValue * 100
            val othersAsset = AssetItem(
                id = "others",
                name = othersGroupName,
                value = othersValue,
                percentage = othersPercentage
            )
            visibleItems + othersAsset
        } else {
            visibleItems
        }

        // Convert to normalized values (0-1 range)
        val normalizedItems = itemsToLayout.map { asset ->
            NormalizedItem(asset, asset.value / totalValue)
        }

        // Apply squarified algorithm with spacing
        val rectangles = mutableListOf<TreemapRect>()
        squarify(normalizedItems, 0f, 0f, width, height, rectangles, spacing)

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
        result: MutableList<TreemapRect>,
        spacing: Float = 0f
    ) {
        if (items.isEmpty()) return

        val totalValue = items.sumOf { it.normalizedValue }
        if (totalValue <= 0) return

        // Use multi-row layout for better organization
        layoutMultiRow(items, x, y, width, height, result, spacing)
    }

    private fun layoutMultiRow(
        items: List<NormalizedItem>,
        x: Float,
        y: Float,
        width: Float,
        height: Float,
        result: MutableList<TreemapRect>,
        spacing: Float = 0f
    ) {
        val maxDisplayItems = 8  // Maximum 8 items to display (2 rows × 4 items)
        val maxItemsPerRow = 4
        val totalItems = items.size

        // If we have more than 8 items, group the excess into "Others"
        val displayItems = if (totalItems > maxDisplayItems) {
            val mainItems = items.take(maxDisplayItems - 1)  // Take first 7 items
            val othersItems = items.drop(maxDisplayItems - 1)  // Rest go to Others
            val othersValue = othersItems.sumOf { it.normalizedValue }
            val othersAsset = AssetItem(
                id = "others",
                name = "Others",
                value = othersValue,
                percentage = othersValue / items.sumOf { it.normalizedValue } * 100
            )
            mainItems + NormalizedItem(othersAsset, othersValue)
        } else {
            items
        }

        val totalRows = 2  // Always 2 rows maximum
        val rowHeight = (height - spacing) / totalRows

        var currentY = y
        var itemIndex = 0

        for (row in 0 until totalRows) {
            val itemsInThisRow = minOf(maxItemsPerRow, displayItems.size - itemIndex)
            if (itemsInThisRow <= 0) break

            val rowItems = displayItems.subList(itemIndex, itemIndex + itemsInThisRow)

            // Calculate total value for this row
            val rowTotalValue = rowItems.sumOf { it.normalizedValue }

            // Layout items horizontally in this row
            val totalSpacing = (itemsInThisRow - 1) * spacing
            val availableWidth = width - totalSpacing
            var currentX = x

            for (item in rowItems) {
                val itemWidth = (item.normalizedValue / rowTotalValue * availableWidth).toFloat()

                if (itemWidth >= MIN_RECT_SIZE) {
                    result.add(
                        TreemapRect(
                            asset = item.asset,
                            x = currentX,
                            y = currentY,
                            width = itemWidth,
                            height = rowHeight,
                            isOthers = item.asset.name == "Others"
                        )
                    )
                }

                currentX += itemWidth + spacing
            }

            currentY += rowHeight + spacing
            itemIndex += itemsInThisRow
        }
    }

    private fun layoutHorizontally(
        items: List<NormalizedItem>,
        x: Float,
        y: Float,
        width: Float,
        height: Float,
        result: MutableList<TreemapRect>,
        spacing: Float = 0f
    ) {
        val totalValue = items.sumOf { it.normalizedValue }
        val totalSpacing = (items.size - 1) * spacing
        val availableWidth = width - totalSpacing
        var currentX = x

        for ((index, item) in items.withIndex()) {
            val itemWidth = (item.normalizedValue / totalValue * availableWidth).toFloat()

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
            // Add spacing between items (except for the last item)
            if (index < items.size - 1) {
                currentX += spacing
            }
        }
    }

    private fun layoutVertically(
        items: List<NormalizedItem>,
        x: Float,
        y: Float,
        width: Float,
        height: Float,
        result: MutableList<TreemapRect>,
        spacing: Float = 0f
    ) {
        val totalValue = items.sumOf { it.normalizedValue }
        val totalSpacing = (items.size - 1) * spacing
        val availableHeight = height - totalSpacing
        var currentY = y

        for ((index, item) in items.withIndex()) {
            val itemHeight = (item.normalizedValue / totalValue * availableHeight).toFloat()

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
            // Add spacing between items (except for the last item)
            if (index < items.size - 1) {
                currentY += spacing
            }
        }
    }

    /**
     * Calculate the aspect ratio of a rectangle
     */
    fun aspectRatio(width: Float, height: Float): Float = maxOf(width / height, height / width)

    /**
     * Check if a rectangle is large enough to display text
     */
    fun canDisplayText(rect: TreemapRect, minTextSize: Float = 12f): Boolean =
        rect.width >= minTextSize * 3 && rect.height >= minTextSize * 1.5f
}
