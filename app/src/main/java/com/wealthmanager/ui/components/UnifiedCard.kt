package com.wealthmanager.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.wealthmanager.ui.responsive.rememberResponsiveLayout

/**
 * Unified card design system based on Material Design 3 best practices.
 */
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

    Card(
        modifier = modifier,
        onClick = onClick ?: {},
        enabled = onClick != null,
        elevation =
            CardDefaults.cardElevation(
                defaultElevation = cardConfig.elevation,
                pressedElevation = if (onClick != null) cardConfig.elevation + 2.dp else cardConfig.elevation,
                focusedElevation = if (onClick != null) cardConfig.elevation + 4.dp else cardConfig.elevation,
                hoveredElevation = if (onClick != null) cardConfig.elevation + 2.dp else cardConfig.elevation,
            ),
        colors =
            CardDefaults.cardColors(
                containerColor = cardConfig.containerColor,
                contentColor = cardConfig.contentColor,
            ),
        shape =
            MaterialTheme.shapes.medium.copy(
                topStart = CornerSize(cardConfig.cornerRadius),
                topEnd = CornerSize(cardConfig.cornerRadius),
                bottomStart = CornerSize(cardConfig.cornerRadius),
                bottomEnd = CornerSize(cardConfig.cornerRadius),
            ),
    ) {
        androidx.compose.foundation.layout.Box(
            modifier = Modifier.padding(cardConfig.padding),
        ) {
            content()
        }
    }
}

/**
 * Card type enumeration based on 2025 Android development guidelines
 */
enum class CardType {
    /**
     * Primary card for important data display and main functions
     * Elevation: 4dp, Padding: 20dp, Corner radius: 12dp
     * Usage: Total asset display, main function entry
     */
    Primary,

    /**
     * Secondary card for settings and secondary information
     * Elevation: 2dp, Padding: 16dp, Corner radius: 8dp
     * Usage: Settings options, list items, secondary functions
     */
    Secondary,

    /**
     * Outlined card for optional content and auxiliary information
     * Elevation: 0dp, Padding: 16dp, Corner radius: 8dp
     * Usage: Optional settings, auxiliary information, secondary content
     */
    Outlined,

    /**
     * Status card for status prompts and warning information
     * Elevation: 3dp, Padding: 16dp, Corner radius: 8dp
     * Usage: Success/error/warning/information prompts
     */
    Status,

    /**
     * Dialog card for modal dialogs
     * Elevation: 8dp, Padding: 24dp, Corner radius: 16dp
     * Usage: Dialogs, popups, important confirmations
     */
    Dialog,

    /**
     * Banner card for top banners and notifications
     * Elevation: 4dp, Padding: 16dp, Corner radius: 8dp
     * Usage: Error banners, notification banners, status banners
     */
    Banner,
}

/**
 * Status type enumeration
 */
enum class StatusType {
    Success,
    Warning,
    Error,
    Info,
}

/**
 * Card configuration data class
 */
private data class CardConfig(
    val elevation: Dp,
    val padding: PaddingValues,
    val cornerRadius: Dp,
    val containerColor: Color,
    val contentColor: Color,
)

/**
 * Get card configuration based on card type and responsive layout
 */
@Composable
private fun getCardConfig(
    cardType: CardType,
    statusType: StatusType?,
    responsiveLayout: com.wealthmanager.ui.responsive.ResponsiveLayout,
): CardConfig {
    val baseConfig =
        when (cardType) {
            CardType.Primary ->
                CardConfig(
                    elevation = 4.dp,
                    padding = PaddingValues(20.dp),
                    cornerRadius = 12.dp,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                )
            CardType.Secondary ->
                CardConfig(
                    elevation = 2.dp,
                    padding = PaddingValues(16.dp),
                    cornerRadius = 8.dp,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                )
            CardType.Outlined ->
                CardConfig(
                    elevation = 0.dp,
                    padding = PaddingValues(16.dp),
                    cornerRadius = 8.dp,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                )
            CardType.Status ->
                CardConfig(
                    elevation = 3.dp,
                    padding = PaddingValues(16.dp),
                    cornerRadius = 8.dp,
                    containerColor = getStatusColor(statusType),
                    contentColor = MaterialTheme.colorScheme.onSurface,
                )
            CardType.Dialog ->
                CardConfig(
                    elevation = 8.dp,
                    padding = PaddingValues(24.dp),
                    cornerRadius = 16.dp,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                )
            CardType.Banner ->
                CardConfig(
                    elevation = 4.dp,
                    padding = PaddingValues(16.dp),
                    cornerRadius = 8.dp,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                )
        }

    val responsiveMultiplier =
        when {
            responsiveLayout.isTablet -> 1.2f
            responsiveLayout.isLargeScreen -> 1.4f
            else -> 1.0f
        }

    return baseConfig.copy(
        elevation = baseConfig.elevation * responsiveMultiplier,
        padding =
            PaddingValues(
                start = baseConfig.padding.calculateStartPadding(LayoutDirection.Ltr) * responsiveMultiplier,
                top = baseConfig.padding.calculateTopPadding() * responsiveMultiplier,
                end = baseConfig.padding.calculateEndPadding(LayoutDirection.Ltr) * responsiveMultiplier,
                bottom = baseConfig.padding.calculateBottomPadding() * responsiveMultiplier,
            ),
        cornerRadius = baseConfig.cornerRadius * responsiveMultiplier,
    )
}

/**
 * Get color based on status type
 */
@Composable
private fun getStatusColor(statusType: StatusType?): Color {
    return when (statusType) {
        StatusType.Success -> MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
        StatusType.Warning -> MaterialTheme.colorScheme.tertiary.copy(alpha = 0.1f)
        StatusType.Error -> MaterialTheme.colorScheme.error.copy(alpha = 0.1f)
        StatusType.Info -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)
        null -> MaterialTheme.colorScheme.surface
    }
}

/**
 * Convenient card component - Primary card
 */
@Composable
fun PrimaryCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    UnifiedCard(
        modifier = modifier,
        cardType = CardType.Primary,
        onClick = onClick,
        content = content,
    )
}

/**
 * Convenient card component - Secondary card
 */
@Composable
fun SecondaryCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    UnifiedCard(
        modifier = modifier,
        cardType = CardType.Secondary,
        onClick = onClick,
        content = content,
    )
}

/**
 * Convenient card component - Outlined card
 */
@Composable
fun OutlinedCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    UnifiedCard(
        modifier = modifier,
        cardType = CardType.Outlined,
        onClick = onClick,
        content = content,
    )
}

/**
 * Convenient card component - Status card
 */
@Composable
fun StatusCard(
    modifier: Modifier = Modifier,
    statusType: StatusType,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    UnifiedCard(
        modifier = modifier,
        cardType = CardType.Status,
        statusType = statusType,
        onClick = onClick,
        content = content,
    )
}

/**
 * Convenient card component - Dialog card
 */
@Composable
fun DialogCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    UnifiedCard(
        modifier = modifier,
        cardType = CardType.Dialog,
        onClick = onClick,
        content = content,
    )
}

/**
 * Convenient card component - Banner card
 */
@Composable
fun BannerCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    UnifiedCard(
        modifier = modifier,
        cardType = CardType.Banner,
        onClick = onClick,
        content = content,
    )
}
