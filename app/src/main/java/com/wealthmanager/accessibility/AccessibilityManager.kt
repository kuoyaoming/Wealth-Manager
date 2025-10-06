package com.wealthmanager.accessibility

import android.content.Context
import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.pow
import android.view.accessibility.AccessibilityManager as AndroidAccessibilityManager

/**
 * Accessibility manager following 2025 Android design guidelines.
 */
@Singleton
class AccessibilityManager
    @Inject
    constructor(
        private val context: Context,
    ) {
        /**
         * Check if TalkBack is enabled.
         */
        fun isTalkBackEnabled(): Boolean {
            return context.getSystemService(Context.ACCESSIBILITY_SERVICE)
                ?.let { accessibilityManager ->
                    (accessibilityManager as AndroidAccessibilityManager).isEnabled &&
                        accessibilityManager.isTouchExplorationEnabled
                } ?: false
        }

        /**
         * Check if large font is enabled.
         */
        fun isLargeFontEnabled(): Boolean {
            val configuration = context.resources.configuration
            val fontScale = configuration.fontScale
            return fontScale > 1.0f
        }

        /**
         * Check if high contrast is enabled.
         */
        fun isHighContrastEnabled(): Boolean {
            val configuration = context.resources.configuration
            return configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
        }

        /**
         * Get font scale factor.
         */
        fun getFontScale(): Float {
            return context.resources.configuration.fontScale
        }

        /**
         * Check if accessibility mode is enabled.
         */
        fun isAccessibilityMode(): Boolean {
            return isTalkBackEnabled() || isLargeFontEnabled() || isHighContrastEnabled()
        }
    }

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
 * Accessibility theme configuration.
 */
@Composable
fun rememberAccessibilityState(): AccessibilityState {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current

    return remember(configuration.fontScale, configuration.uiMode) {
        val accessibilityManager = AccessibilityManager(context)

        AccessibilityState(
            isTalkBackEnabled = accessibilityManager.isTalkBackEnabled(),
            isLargeFontEnabled = accessibilityManager.isLargeFontEnabled(),
            isHighContrastEnabled = accessibilityManager.isHighContrastEnabled(),
            fontScale = accessibilityManager.getFontScale(),
            isAccessibilityMode = accessibilityManager.isAccessibilityMode(),
        )
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

        val rLinear = if (r <= 0.03928) r / 12.92 else (r + 0.055).pow(2.4) / 1.055.pow(2.4)
        val gLinear = if (g <= 0.03928) g / 12.92 else (g + 0.055).pow(2.4) / 1.055.pow(2.4)
        val bLinear = if (b <= 0.03928) b / 12.92 else (b + 0.055).pow(2.4) / 1.055.pow(2.4)

        return 0.2126 * rLinear + 0.7152 * gLinear + 0.0722 * bLinear
    }
}
