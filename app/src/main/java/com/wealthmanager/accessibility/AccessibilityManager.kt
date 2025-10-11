package com.wealthmanager.accessibility

import android.content.Context
import android.os.Build
import android.view.accessibility.AccessibilityManager as AndroidAccessibilityManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import kotlin.math.pow

/**
 * Accessibility state.
 */
data class AccessibilityState(
    val isTalkBackEnabled: Boolean = false,
    val isLargeFontEnabled: Boolean = false,
    val isHighContrastEnabled: Boolean = false,
    val fontScale: Float = 1.0f,
    val isAccessibilityMode: Boolean = false,
)

/**
 * Remembers the current accessibility state of the system.
 *
 * This composable function provides an [AccessibilityState] object that reflects
 * the current system settings for TalkBack, font size, and high contrast mode.
 * It automatically updates when these settings change.
 */
@Composable
fun rememberAccessibilityState(): AccessibilityState {
    val context = LocalContext.current
    val accessibilityManager =
        context.getSystemService(Context.ACCESSIBILITY_SERVICE) as? AndroidAccessibilityManager

    val configuration = LocalConfiguration.current
    val fontScale = configuration.fontScale

    return remember(accessibilityManager, fontScale) {
        val isTalkBackEnabled =
            accessibilityManager?.isEnabled == true && accessibilityManager.isTouchExplorationEnabled
        val isLargeFontEnabled = fontScale > 1.1f // Standard threshold for large font
        val isHighContrastEnabled = isHighContrastTextEnabled(accessibilityManager)

        AccessibilityState(
            isTalkBackEnabled = isTalkBackEnabled,
            isLargeFontEnabled = isLargeFontEnabled,
            isHighContrastEnabled = isHighContrastEnabled,
            fontScale = fontScale,
            isAccessibilityMode = isTalkBackEnabled || isLargeFontEnabled || isHighContrastEnabled,
        )
    }
}

/**
 * Checks for high contrast text setting using reflection to support projects
 * with a compileSdk lower than 29.
 */
private fun isHighContrastTextEnabled(manager: AndroidAccessibilityManager?): Boolean {
    if (manager == null || Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
        return false
    }
    return try {
        val method = manager.javaClass.getMethod("isHighContrastTextEnabled")
        (method.invoke(manager) as? Boolean) == true
    } catch (e: Exception) {
        false
    }
}

/**
 * Dynamic font size calculation.
 */
@Composable
fun getAccessibleFontSize(
    baseSize: Dp,
    accessibilityState: AccessibilityState,
): Dp {
    val scaleFactor =
        when {
            accessibilityState.fontScale > 1.5f -> 1.5f
            accessibilityState.fontScale > 1.3f -> 1.3f
            accessibilityState.fontScale > 1.1f -> 1.1f
            else -> 1.0f
        }

    return baseSize * scaleFactor
}

/**
 * Accessibility spacing calculation.
 */
@Composable
fun getAccessibleSpacing(
    baseSpacing: Dp,
    accessibilityState: AccessibilityState,
): Dp {
    val scaleFactor =
        when {
            accessibilityState.isLargeFontEnabled -> 1.3f
            accessibilityState.isTalkBackEnabled -> 1.2f
            else -> 1.0f
        }

    return baseSpacing * scaleFactor
}

/**
 * Accessibility color contrast checking.
 */
object AccessibilityColors {
    /**
     * Check if color contrast meets WCAG AA standards.
     */
    fun isContrastSufficient(
        foreground: Long,
        background: Long,
    ): Boolean {
        val foregroundLuminance = calculateLuminance(foreground)
        val backgroundLuminance = calculateLuminance(background)

        val contrast =
            if (foregroundLuminance > backgroundLuminance) {
                (foregroundLuminance + 0.05) / (backgroundLuminance + 0.05)
            } else {
                (backgroundLuminance + 0.05) / (foregroundLuminance + 0.05)
            }

        return contrast >= 4.5
    }

    private fun calculateLuminance(color: Long): Double {
        val r = ((color shr 16) and 0xFF) / 255.0
        val g = ((color shr 8) and 0xFF) / 255.0
        val b = (color and 0xFF) / 255.0

        val rLinear = if (r <= 0.03928) r / 12.92 else ((r + 0.055) / 1.055).pow(2.4)
        val gLinear = if (g <= 0.03928) g / 12.92 else ((g + 0.055) / 1.055).pow(2.4)
        val bLinear = if (b <= 0.03928) b / 12.92 else ((b + 0.055) / 1.055).pow(2.4)

        return 0.2126 * rLinear + 0.7152 * gLinear + 0.0722 * bLinear
    }
}
