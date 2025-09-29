package com.wealthmanager.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Color guidelines and utilities for consistent theme usage
 */
object ColorGuidelines {
    
    /**
     * Get appropriate text color based on background
     */
    @Composable
    fun getTextColorForBackground(backgroundColor: Color): Color {
        return when (backgroundColor) {
            MaterialTheme.colorScheme.primary -> MaterialTheme.colorScheme.onPrimary
            MaterialTheme.colorScheme.secondary -> MaterialTheme.colorScheme.onSecondary
            MaterialTheme.colorScheme.tertiary -> MaterialTheme.colorScheme.onTertiary
            MaterialTheme.colorScheme.error -> MaterialTheme.colorScheme.onError
            MaterialTheme.colorScheme.surface -> MaterialTheme.colorScheme.onSurface
            MaterialTheme.colorScheme.background -> MaterialTheme.colorScheme.onBackground
            else -> MaterialTheme.colorScheme.onSurface
        }
    }
    
    /**
     * Get semantic colors for financial data
     */
    @Composable
    fun getFinancialColors(): FinancialColorSet {
        return FinancialColorSet(
            positive = getPositiveColor(),
            negative = getNegativeColor(),
            neutral = getNeutralColor(),
            warning = getWarningColor(),
            info = getInfoColor()
        )
    }
    
    @Composable
    private fun getPositiveColor(): Color {
        return MaterialTheme.colorScheme.primary
    }
    
    @Composable
    private fun getNegativeColor(): Color {
        return MaterialTheme.colorScheme.error
    }
    
    @Composable
    private fun getNeutralColor(): Color {
        return MaterialTheme.colorScheme.onSurfaceVariant
    }
    
    @Composable
    private fun getWarningColor(): Color {
        return MaterialTheme.colorScheme.tertiary
    }
    
    @Composable
    private fun getInfoColor(): Color {
        return MaterialTheme.colorScheme.secondary
    }
}

/**
 * Financial color set for consistent usage
 */
data class FinancialColorSet(
    val positive: Color,
    val negative: Color,
    val neutral: Color,
    val warning: Color,
    val info: Color
)

/**
 * Color usage guidelines:
 * 
 * 1. PRIMARY COLORS:
 *    - Use MaterialTheme.colorScheme.primary for main actions and highlights
 *    - Use MaterialTheme.colorScheme.onPrimary for text on primary background
 * 
 * 2. SURFACE COLORS:
 *    - Use MaterialTheme.colorScheme.surface for cards and elevated surfaces
 *    - Use MaterialTheme.colorScheme.onSurface for text on surface
 *    - Use MaterialTheme.colorScheme.surfaceVariant for secondary surfaces
 *    - Use MaterialTheme.colorScheme.onSurfaceVariant for secondary text
 * 
 * 3. FINANCIAL COLORS:
 *    - Use positive colors for gains, profits, positive values
 *    - Use negative colors for losses, debts, negative values
 *    - Use neutral colors for neutral information
 *    - Use warning colors for alerts and cautions
 *    - Use info colors for informational content
 * 
 * 4. ACCESSIBILITY:
 *    - Always ensure sufficient contrast between text and background
 *    - Use semantic colors that work in both light and dark themes
 *    - Test with high contrast mode enabled
 * 
 * 5. DYNAMIC COLORS:
 *    - Leverage Android 12+ dynamic colors when available
 *    - Fall back to custom color schemes for older versions
 *    - Ensure colors work well with system themes
 */
