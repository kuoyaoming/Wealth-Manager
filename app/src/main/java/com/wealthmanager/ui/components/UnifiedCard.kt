package com.wealthmanager.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
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
import com.wealthmanager.ui.responsive.ResponsiveLayout
import com.wealthmanager.ui.responsive.rememberResponsiveLayout

/**
 * Unified card design system with modern animations and responsive sizing.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnifiedCard(
    modifier: Modifier = Modifier,
    cardType: CardType = CardType.Secondary,
    statusType: StatusType? = null,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    val responsiveLayout = rememberResponsiveLayout()
    val cardConfig = getCardConfig(cardType, statusType, responsiveLayout)

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (onClick != null && isPressed) 0.98f else 1f,
        animationSpec = ModernAnimationSpecs.getOptimizedSpringSpec(),
        label = "card-scale"
    )

    Card(
        modifier = modifier.graphicsLayer {
            scaleX = scale
            scaleY = scale
        },
        onClick = onClick ?: {},
        enabled = onClick != null,
        elevation = CardDefaults.cardElevation(defaultElevation = cardConfig.elevation),
        colors = cardConfig.colors,
        shape = cardConfig.shape,
        interactionSource = interactionSource,
    ) {
        Box(modifier = Modifier.padding(cardConfig.padding)) {
            content()
        }
    }
}

enum class CardType {
    Primary, Secondary, Outlined, Status, Dialog, Banner
}

enum class StatusType {
    Success, Warning, Error, Info
}

private data class CardConfig(
    val elevation: Dp,
    val padding: PaddingValues,
    val shape: androidx.compose.ui.graphics.Shape,
    val colors: CardColors,
)

@Composable
private fun getCardConfig(
    cardType: CardType,
    statusType: StatusType?,
    responsiveLayout: ResponsiveLayout,
): CardConfig {
    val colorScheme = MaterialTheme.colorScheme
    val baseElevation: Dp
    val basePadding: PaddingValues
    val baseCornerRadius: Dp
    val colors: CardColors

    when (cardType) {
        CardType.Primary -> {
            baseElevation = 4.dp
            basePadding = PaddingValues(20.dp)
            baseCornerRadius = 16.dp
            colors = CardDefaults.cardColors(
                containerColor = colorScheme.surface,
                contentColor = colorScheme.onSurface
            )
        }
        CardType.Secondary -> {
            baseElevation = 2.dp
            basePadding = PaddingValues(16.dp)
            baseCornerRadius = 12.dp
            colors = CardDefaults.cardColors(
                containerColor = colorScheme.surfaceVariant,
                contentColor = colorScheme.onSurfaceVariant
            )
        }
        CardType.Outlined -> {
            baseElevation = 0.dp
            basePadding = PaddingValues(16.dp)
            baseCornerRadius = 12.dp
            colors = CardDefaults.outlinedCardColors()
        }
        CardType.Status -> {
            baseElevation = 1.dp
            basePadding = PaddingValues(16.dp)
            baseCornerRadius = 12.dp
            colors = CardDefaults.cardColors(
                containerColor = getStatusColor(statusType),
                contentColor = colorScheme.onSurface
            )
        }
        CardType.Dialog -> {
            baseElevation = 8.dp
            basePadding = PaddingValues(24.dp)
            baseCornerRadius = 28.dp
            colors = CardDefaults.cardColors(
                containerColor = colorScheme.surface,
                contentColor = colorScheme.onSurface
            )
        }
        CardType.Banner -> {
            baseElevation = 2.dp
            basePadding = PaddingValues(16.dp)
            baseCornerRadius = 12.dp
            colors = CardDefaults.cardColors(
                containerColor = colorScheme.surfaceVariant,
                contentColor = colorScheme.onSurfaceVariant
            )
        }
    }

    val responsiveMultiplier = when {
        responsiveLayout.isTablet -> 1.1f
        responsiveLayout.isLargeScreen -> 1.2f
        else -> 1.0f
    }

    return CardConfig(
        elevation = baseElevation * responsiveMultiplier,
        padding = PaddingValues(
            horizontal = basePadding.calculateLeftPadding(LayoutDirection.Ltr) * responsiveMultiplier,
            vertical = basePadding.calculateTopPadding() * responsiveMultiplier
        ),
        shape = MaterialTheme.shapes.medium.copy(all = CornerSize(baseCornerRadius * responsiveMultiplier)),
        colors = colors
    )
}

@Composable
private fun getStatusColor(statusType: StatusType?): Color {
    val colorScheme = MaterialTheme.colorScheme
    return when (statusType) {
        StatusType.Success -> colorScheme.primaryContainer
        StatusType.Warning -> colorScheme.tertiaryContainer
        StatusType.Error -> colorScheme.errorContainer
        StatusType.Info -> colorScheme.secondaryContainer
        null -> colorScheme.surface
    }
}


// --- Convenience Composables ---

@Composable
fun PrimaryCard(modifier: Modifier = Modifier, onClick: (() -> Unit)? = null, content: @Composable () -> Unit) {
    UnifiedCard(modifier, CardType.Primary, null, onClick, content)
}

@Composable
fun SecondaryCard(modifier: Modifier = Modifier, onClick: (() -> Unit)? = null, content: @Composable () -> Unit) {
    UnifiedCard(modifier, CardType.Secondary, null, onClick, content)
}

@Composable
fun OutlinedCard(modifier: Modifier = Modifier, onClick: (() -> Unit)? = null, content: @Composable () -> Unit) {
    UnifiedCard(modifier, CardType.Outlined, null, onClick, content)
}

@Composable
fun StatusCard(modifier: Modifier = Modifier, statusType: StatusType, onClick: (() -> Unit)? = null, content: @Composable () -> Unit) {
    UnifiedCard(modifier, CardType.Status, statusType, onClick, content)
}

@Composable
fun DialogCard(modifier: Modifier = Modifier, onClick: (() -> Unit)? = null, content: @Composable () -> Unit) {
    UnifiedCard(modifier, CardType.Dialog, null, onClick, content)
}

@Composable
fun BannerCard(modifier: Modifier = Modifier, onClick: (() -> Unit)? = null, content: @Composable () -> Unit) {
    UnifiedCard(modifier, CardType.Banner, null, onClick, content)
}
