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
 * 统一的卡片设计系统 - 基于2025年Material Design 3最佳实践
 * 
 * 提供一致的卡片样式，支持响应式设计和语义化颜色
 */
@Composable
fun UnifiedCard(
    modifier: Modifier = Modifier,
    cardType: CardType = CardType.Secondary,
    statusType: StatusType? = null,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val responsiveLayout = rememberResponsiveLayout()
    val cardConfig = getCardConfig(cardType, statusType, responsiveLayout)
    
    Card(
        modifier = modifier,
        onClick = onClick ?: {},
        enabled = onClick != null,
        elevation = CardDefaults.cardElevation(
            defaultElevation = cardConfig.elevation,
            pressedElevation = if (onClick != null) cardConfig.elevation + 2.dp else cardConfig.elevation,
            focusedElevation = if (onClick != null) cardConfig.elevation + 4.dp else cardConfig.elevation,
            hoveredElevation = if (onClick != null) cardConfig.elevation + 2.dp else cardConfig.elevation,
        ),
        colors = CardDefaults.cardColors(
            containerColor = cardConfig.containerColor,
            contentColor = cardConfig.contentColor,
        ),
        shape = MaterialTheme.shapes.medium.copy(
            topStart = CornerSize(cardConfig.cornerRadius),
            topEnd = CornerSize(cardConfig.cornerRadius),
            bottomStart = CornerSize(cardConfig.cornerRadius),
            bottomEnd = CornerSize(cardConfig.cornerRadius),
        ),
    ) {
        androidx.compose.foundation.layout.Box(
            modifier = Modifier.padding(cardConfig.padding)
        ) {
            content()
        }
    }
}

/**
 * 卡片类型枚举
 */
enum class CardType {
    /**
     * 主要卡片 - 用于重要数据展示、主要功能
     * Elevation: 4dp, Padding: 20dp, 圆角: 12dp
     */
    Primary,
    
    /**
     * 次要卡片 - 用于设置项、次要信息  
     * Elevation: 2dp, Padding: 16dp, 圆角: 8dp
     */
    Secondary,
    
    /**
     * 轮廓卡片 - 用于可选内容、辅助信息
     * Elevation: 0dp, Padding: 16dp, 圆角: 8dp
     */
    Outlined,
    
    /**
     * 状态卡片 - 用于状态提示、警告信息
     * Elevation: 3dp, Padding: 16dp, 圆角: 8dp
     */
    Status
}

/**
 * 状态类型枚举
 */
enum class StatusType {
    Success,
    Warning,
    Error,
    Info
}

/**
 * 卡片配置数据类
 */
private data class CardConfig(
    val elevation: Dp,
    val padding: PaddingValues,
    val cornerRadius: Dp,
    val containerColor: Color,
    val contentColor: Color
)

/**
 * 根据卡片类型和响应式布局获取卡片配置
 */
@Composable
private fun getCardConfig(
    cardType: CardType,
    statusType: StatusType?,
    responsiveLayout: com.wealthmanager.ui.responsive.ResponsiveLayout
): CardConfig {
    val baseConfig = when (cardType) {
        CardType.Primary -> CardConfig(
            elevation = 4.dp,
            padding = PaddingValues(20.dp),
            cornerRadius = 12.dp,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
        CardType.Secondary -> CardConfig(
            elevation = 2.dp,
            padding = PaddingValues(16.dp),
            cornerRadius = 8.dp,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
        CardType.Outlined -> CardConfig(
            elevation = 0.dp,
            padding = PaddingValues(16.dp),
            cornerRadius = 8.dp,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
        CardType.Status -> CardConfig(
            elevation = 3.dp,
            padding = PaddingValues(16.dp),
            cornerRadius = 8.dp,
            containerColor = getStatusColor(statusType),
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    }
    
    // 响应式调整
    val responsiveMultiplier = when {
        responsiveLayout.isTablet -> 1.2f
        responsiveLayout.isLargeScreen -> 1.4f
        else -> 1.0f
    }
    
    return baseConfig.copy(
        elevation = baseConfig.elevation * responsiveMultiplier,
        padding = PaddingValues(
            start = baseConfig.padding.calculateStartPadding(LayoutDirection.Ltr) * responsiveMultiplier,
            top = baseConfig.padding.calculateTopPadding() * responsiveMultiplier,
            end = baseConfig.padding.calculateEndPadding(LayoutDirection.Ltr) * responsiveMultiplier,
            bottom = baseConfig.padding.calculateBottomPadding() * responsiveMultiplier,
        ),
        cornerRadius = baseConfig.cornerRadius * responsiveMultiplier
    )
}

/**
 * 根据状态类型获取颜色
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
 * 便捷的卡片组件 - 主要卡片
 */
@Composable
fun PrimaryCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    UnifiedCard(
        modifier = modifier,
        cardType = CardType.Primary,
        onClick = onClick,
        content = content
    )
}

/**
 * 便捷的卡片组件 - 次要卡片
 */
@Composable
fun SecondaryCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    UnifiedCard(
        modifier = modifier,
        cardType = CardType.Secondary,
        onClick = onClick,
        content = content
    )
}

/**
 * 便捷的卡片组件 - 轮廓卡片
 */
@Composable
fun OutlinedCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    UnifiedCard(
        modifier = modifier,
        cardType = CardType.Outlined,
        onClick = onClick,
        content = content
    )
}

/**
 * 便捷的卡片组件 - 状态卡片
 */
@Composable
fun StatusCard(
    modifier: Modifier = Modifier,
    statusType: StatusType,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    UnifiedCard(
        modifier = modifier,
        cardType = CardType.Status,
        statusType = statusType,
        onClick = onClick,
        content = content
    )
}