package com.wealthmanager.ui.charts.treemap

import com.wealthmanager.ui.dashboard.AssetItem
import kotlin.math.min

/**
 * Represents a rectangle in the visual layout, corresponding to an asset.
 */
data class TreemapRect(
    val asset: AssetItem,
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float,
)

/**
 * A layout utility that arranges assets into a simple grid-like structure.
 *
 * This is a simplified alternative to a full squarified treemap algorithm, focusing on
 * a clean, organized presentation rather than representing value by area.
 */
object TreemapLayout {

    private const val MAX_ITEMS_PER_ROW = 4
    private const val MAX_ROWS = 2
    private const val MAX_VISIBLE_ITEMS = MAX_ITEMS_PER_ROW * MAX_ROWS
    private const val MIN_RECT_SIZE = 20f

    /**
     * Computes a grid-based layout for the given assets.
     *
     * @param assets The list of assets to lay out.
     * @param width The total available width for the layout.
     * @param height The total available height for the layout.
     * @param spacing The spacing to apply between the grid cells.
     * @param othersGroupName The name to use for the "Others" group if assets are consolidated.
     * @return A list of [TreemapRect] objects representing the calculated layout.
     */
    fun computeTreemapRects(
        assets: List<AssetItem>,
        width: Float,
        height: Float,
        spacing: Float = 8f,
        othersGroupName: String = "Others",
    ): List<TreemapRect> {
        if (assets.isEmpty() || width <= 0 || height <= 0) return emptyList()

        val sortedAssets = assets.sortedByDescending { it.value }

        val itemsToLayout = if (sortedAssets.size > MAX_VISIBLE_ITEMS) {
            val visibleItems = sortedAssets.take(MAX_VISIBLE_ITEMS - 1)
            val otherItems = sortedAssets.drop(MAX_VISIBLE_ITEMS - 1)
            val othersValue = otherItems.sumOf { it.value }
            val othersAsset = AssetItem(id = "others", name = othersGroupName, value = othersValue)
            visibleItems + othersAsset
        } else {
            sortedAssets
        }

        return layoutAsGrid(itemsToLayout, width, height, spacing)
    }

    private fun layoutAsGrid(
        items: List<AssetItem>,
        width: Float,
        height: Float,
        spacing: Float
    ): List<TreemapRect> {
        val rectangles = mutableListOf<TreemapRect>()
        val numRows = if (items.size <= MAX_ITEMS_PER_ROW) 1 else MAX_ROWS
        val rowHeight = (height - (numRows - 1) * spacing) / numRows

        var currentY = 0f
        var itemIndex = 0

        repeat(numRows) {
            val itemsInThisRow = if (numRows == 1) items.size else (items.size + 1) / 2
            val rowItems = items.subList(itemIndex, (itemIndex + itemsInThisRow).coerceAtMost(items.size))
            if (rowItems.isEmpty()) return@repeat

            val totalValueInRow = rowItems.sumOf { it.value }
            val availableWidth = width - (rowItems.size - 1) * spacing
            var currentX = 0f

            rowItems.forEach { item ->
                val itemWidth = if (totalValueInRow > 0) {
                    (item.value / totalValueInRow * availableWidth).toFloat()
                } else {
                    availableWidth / rowItems.size
                }

                if (itemWidth >= MIN_RECT_SIZE) {
                    rectangles.add(
                        TreemapRect(
                            asset = item,
                            x = currentX,
                            y = currentY,
                            width = itemWidth,
                            height = rowHeight
                        )
                    )
                }
                currentX += itemWidth + spacing
            }

            currentY += rowHeight + spacing
            itemIndex += itemsInThisRow
        }

        return rectangles
    }
}
