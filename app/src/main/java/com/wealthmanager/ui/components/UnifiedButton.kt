package com.wealthmanager.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.wealthmanager.accessibility.AccessibilityState
import com.wealthmanager.accessibility.rememberAccessibilityState
import com.wealthmanager.ui.responsive.ResponsiveLayout
import com.wealthmanager.ui.responsive.rememberResponsiveLayout

/**
 * Unified button design system with modern animations, responsive sizing, and accessibility support.
 */
@Composable
fun UnifiedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    buttonType: ButtonType = ButtonType.Primary,
    size: ButtonSize = ButtonSize.Medium,
    enabled: Boolean = true,
    content: @Composable () -> Unit,
) {
    val responsiveLayout = rememberResponsiveLayout()
    val accessibilityState = rememberAccessibilityState()
    val buttonConfig = getButtonConfig(buttonType, size, responsiveLayout, accessibilityState)

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = ModernAnimationSpecs.getOptimizedSpringSpec(),
        label = "button-scale"
    )

    val buttonModifier = modifier
        .height(buttonConfig.height)
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }

    when (buttonType) {
        ButtonType.Primary -> {
            Button(
                onClick = onClick,
                modifier = buttonModifier,
                enabled = enabled,
                colors = buttonConfig.colors,
                shape = buttonConfig.shape,
                contentPadding = buttonConfig.contentPadding,
                interactionSource = interactionSource
            ) { content() }
        }
        ButtonType.Secondary -> {
            OutlinedButton(
                onClick = onClick,
                modifier = buttonModifier,
                enabled = enabled,
                colors = buttonConfig.colors as ButtonColors,
                border = BorderStroke(1.dp, if (enabled) (buttonConfig.colors as ButtonColors).containerColor else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)),
                shape = buttonConfig.shape,
                contentPadding = buttonConfig.contentPadding,
                interactionSource = interactionSource
            ) { content() }
        }
        ButtonType.Text -> {
            TextButton(
                onClick = onClick,
                modifier = buttonModifier,
                enabled = enabled,
                colors = buttonConfig.colors as ButtonColors,
                shape = buttonConfig.shape,
                contentPadding = buttonConfig.contentPadding,
                interactionSource = interactionSource
            ) { content() }
        }
        ButtonType.Tonal -> {
            FilledTonalButton(
                onClick = onClick,
                modifier = buttonModifier,
                enabled = enabled,
                colors = buttonConfig.colors,
                shape = buttonConfig.shape,
                contentPadding = buttonConfig.contentPadding,
                interactionSource = interactionSource
            ) { content() }
        }
    }
}


enum class ButtonType {
    Primary, Secondary, Text, Tonal
}

enum class ButtonSize {
    Small, Medium, Large
}

private data class ButtonConfig(
    val height: Dp,
    val shape: androidx.compose.ui.graphics.Shape,
    val contentPadding: PaddingValues,
    val colors: ButtonColors,
)

@Composable
private fun getButtonConfig(
    buttonType: ButtonType,
    size: ButtonSize,
    responsiveLayout: ResponsiveLayout,
    accessibilityState: AccessibilityState
): ButtonConfig {
    val colorScheme = MaterialTheme.colorScheme
    val responsiveMultiplier = when {
        responsiveLayout.isTablet -> 1.1f
        responsiveLayout.isLargeScreen -> 1.2f
        else -> 1.0f
    }
    val accessibilityMultiplier = if (accessibilityState.isLargeFontEnabled) 1.2f else 1.0f

    val baseHeight = when (size) {
        ButtonSize.Small -> 40.dp
        ButtonSize.Medium -> 48.dp
        ButtonSize.Large -> 56.dp
    }

    val contentPadding = when (size) {
        ButtonSize.Small -> PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ButtonSize.Medium -> PaddingValues(horizontal = 20.dp, vertical = 12.dp)
        ButtonSize.Large -> PaddingValues(horizontal = 24.dp, vertical = 16.dp)
    }

    val cornerRadius = when (size) {
        ButtonSize.Small -> 8.dp
        ButtonSize.Medium -> 12.dp
        ButtonSize.Large -> 16.dp
    }

    val colors = when (buttonType) {
        ButtonType.Primary -> ButtonDefaults.buttonColors(
            containerColor = colorScheme.primary,
            contentColor = colorScheme.onPrimary,
            disabledContainerColor = colorScheme.onSurface.copy(alpha = 0.12f),
            disabledContentColor = colorScheme.onSurface.copy(alpha = 0.38f),
        )
        ButtonType.Secondary -> ButtonDefaults.outlinedButtonColors(
            contentColor = colorScheme.primary
        )
        ButtonType.Text -> ButtonDefaults.textButtonColors(
            contentColor = colorScheme.primary
        )
        ButtonType.Tonal -> ButtonDefaults.filledTonalButtonColors(
            containerColor = colorScheme.secondaryContainer,
            contentColor = colorScheme.onSecondaryContainer
        )
    }

    return ButtonConfig(
        height = baseHeight * responsiveMultiplier * accessibilityMultiplier,
        shape = MaterialTheme.shapes.medium.copy(all = androidx.compose.foundation.shape.CornerSize(cornerRadius * responsiveMultiplier)),
        contentPadding = PaddingValues(
            horizontal = contentPadding.calculateLeftPadding(LayoutDirection.Ltr) * responsiveMultiplier * accessibilityMultiplier,
            vertical = contentPadding.calculateTopPadding() * responsiveMultiplier * accessibilityMultiplier
        ),
        colors = colors
    )
}

@Composable
fun PrimaryButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: ButtonSize = ButtonSize.Medium,
    enabled: Boolean = true,
    content: @Composable () -> Unit,
) {
    UnifiedButton(onClick, modifier, ButtonType.Primary, size, enabled, content)
}

@Composable
fun SecondaryButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: ButtonSize = ButtonSize.Medium,
    enabled: Boolean = true,
    content: @Composable () -> Unit,
) {
    UnifiedButton(onClick, modifier, ButtonType.Secondary, size, enabled, content)
}

@Composable
fun TextButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: ButtonSize = ButtonSize.Medium,
    enabled: Boolean = true,
    content: @Composable () -> Unit,
) {
    UnifiedButton(onClick, modifier, ButtonType.Text, size, enabled, content)
}

@Composable
fun TonalButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: ButtonSize = ButtonSize.Medium,
    enabled: Boolean = true,
    content: @Composable () -> Unit,
) {
    UnifiedButton(onClick, modifier, ButtonType.Tonal, size, enabled, content)
}
