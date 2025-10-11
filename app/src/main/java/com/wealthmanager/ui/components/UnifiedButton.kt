package com.wealthmanager.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.wealthmanager.ui.responsive.rememberResponsiveLayout

/**
 * Unified button design system based on 2025 Android development guidelines and Material Design 3 best practices
 *
 * Design principles:
 * - Consistent button height (48dp minimum)
 * - Unified corner radius (4dp, 8dp, 12dp)
 * - Standardized padding (16dp, 20dp, 24dp)
 * - Semantic color usage
 * - Responsive design support
 * - Accessibility optimization
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
    val buttonConfig = getButtonConfig(buttonType, size, responsiveLayout)

    when (buttonType) {
        ButtonType.Primary -> {
            Button(
                onClick = onClick,
                modifier = modifier.height(buttonConfig.height),
                enabled = enabled,
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = buttonConfig.containerColor,
                        contentColor = buttonConfig.contentColor,
                        disabledContainerColor = buttonConfig.disabledContainerColor,
                        disabledContentColor = buttonConfig.disabledContentColor,
                    ),
                shape =
                    MaterialTheme.shapes.medium.copy(
                        topStart = androidx.compose.foundation.shape.CornerSize(buttonConfig.cornerRadius),
                        topEnd = androidx.compose.foundation.shape.CornerSize(buttonConfig.cornerRadius),
                        bottomStart = androidx.compose.foundation.shape.CornerSize(buttonConfig.cornerRadius),
                        bottomEnd = androidx.compose.foundation.shape.CornerSize(buttonConfig.cornerRadius),
                    ),
                contentPadding = buttonConfig.contentPadding,
            ) {
                content()
            }
        }
        ButtonType.Secondary -> {
            OutlinedButton(
                onClick = onClick,
                modifier = modifier.height(buttonConfig.height),
                enabled = enabled,
                colors =
                    ButtonDefaults.outlinedButtonColors(
                        contentColor = buttonConfig.contentColor,
                        disabledContentColor = buttonConfig.disabledContentColor,
                    ),
                border =
                    androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (enabled) buttonConfig.containerColor else buttonConfig.disabledContainerColor,
                    ),
                shape =
                    MaterialTheme.shapes.medium.copy(
                        topStart = androidx.compose.foundation.shape.CornerSize(buttonConfig.cornerRadius),
                        topEnd = androidx.compose.foundation.shape.CornerSize(buttonConfig.cornerRadius),
                        bottomStart = androidx.compose.foundation.shape.CornerSize(buttonConfig.cornerRadius),
                        bottomEnd = androidx.compose.foundation.shape.CornerSize(buttonConfig.cornerRadius),
                    ),
                contentPadding = buttonConfig.contentPadding,
            ) {
                content()
            }
        }
        ButtonType.Text -> {
            TextButton(
                onClick = onClick,
                modifier = modifier.height(buttonConfig.height),
                enabled = enabled,
                colors =
                    ButtonDefaults.textButtonColors(
                        contentColor = buttonConfig.contentColor,
                        disabledContentColor = buttonConfig.disabledContentColor,
                    ),
                shape =
                    MaterialTheme.shapes.medium.copy(
                        topStart = androidx.compose.foundation.shape.CornerSize(buttonConfig.cornerRadius),
                        topEnd = androidx.compose.foundation.shape.CornerSize(buttonConfig.cornerRadius),
                        bottomStart = androidx.compose.foundation.shape.CornerSize(buttonConfig.cornerRadius),
                        bottomEnd = androidx.compose.foundation.shape.CornerSize(buttonConfig.cornerRadius),
                    ),
                contentPadding = buttonConfig.contentPadding,
            ) {
                content()
            }
        }
        ButtonType.Tonal -> {
            FilledTonalButton(
                onClick = onClick,
                modifier = modifier.height(buttonConfig.height),
                enabled = enabled,
                colors =
                    ButtonDefaults.filledTonalButtonColors(
                        containerColor = buttonConfig.containerColor,
                        contentColor = buttonConfig.contentColor,
                        disabledContainerColor = buttonConfig.disabledContainerColor,
                        disabledContentColor = buttonConfig.disabledContentColor,
                    ),
                shape =
                    MaterialTheme.shapes.medium.copy(
                        topStart = androidx.compose.foundation.shape.CornerSize(buttonConfig.cornerRadius),
                        topEnd = androidx.compose.foundation.shape.CornerSize(buttonConfig.cornerRadius),
                        bottomStart = androidx.compose.foundation.shape.CornerSize(buttonConfig.cornerRadius),
                        bottomEnd = androidx.compose.foundation.shape.CornerSize(buttonConfig.cornerRadius),
                    ),
                contentPadding = buttonConfig.contentPadding,
            ) {
                content()
            }
        }
    }
}

/**
 * Button type enumeration based on 2025 Android development guidelines
 */
enum class ButtonType {
    /**
     * Primary button for main operations
     * Usage: Save, confirm, submit and other main operations
     */
    Primary,

    /**
     * 次要按鈕 - 用於次要操作
     * 用途：取消、返回、次要功能
     */
    Secondary,

    /**
     * 文字按鈕 - 用於低優先級操作
     * 用途：鏈接、輔助操作
     */
    Text,

    /**
     * 色調按鈕 - 用於中等優先級操作
     * 用途：替代操作、中性操作
     */
    Tonal,
}

/**
 * 按鈕尺寸枚舉
 */
enum class ButtonSize {
    /**
     * 小按鈕 - 用於緊湊空間
     * 高度: 40dp, 內邊距: 12dp
     */
    Small,

    /**
     * 中等按鈕 - 標準按鈕
     * 高度: 48dp, 內邊距: 16dp
     */
    Medium,

    /**
     * 大按鈕 - 用於重要操作
     * 高度: 56dp, 內邊距: 20dp
     */
    Large,
}

/**
 * 按鈕配置數據類
 */
private data class ButtonConfig(
    val height: Dp,
    val cornerRadius: Dp,
    val contentPadding: PaddingValues,
    val containerColor: Color,
    val contentColor: Color,
    val disabledContainerColor: Color,
    val disabledContentColor: Color,
)

/**
 * 根據按鈕類型和響應式布局獲取按鈕配置
 */
@Composable
private fun getButtonConfig(
    buttonType: ButtonType,
    size: ButtonSize,
    responsiveLayout: com.wealthmanager.ui.responsive.ResponsiveLayout,
): ButtonConfig {
    val baseHeight =
        when (size) {
            ButtonSize.Small -> 40.dp
            ButtonSize.Medium -> 48.dp
            ButtonSize.Large -> 56.dp
        }

    val basePadding =
        when (size) {
            ButtonSize.Small -> PaddingValues(horizontal = 12.dp, vertical = 8.dp)
            ButtonSize.Medium -> PaddingValues(horizontal = 16.dp, vertical = 12.dp)
            ButtonSize.Large -> PaddingValues(horizontal = 20.dp, vertical = 16.dp)
        }

    val baseCornerRadius =
        when (size) {
            ButtonSize.Small -> 4.dp
            ButtonSize.Medium -> 8.dp
            ButtonSize.Large -> 12.dp
        }

    val colors =
        when (buttonType) {
            ButtonType.Primary ->
                ButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                    disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
                )
            ButtonType.Secondary ->
                ButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.primary,
                    disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                    disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
                )
            ButtonType.Text ->
                ButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.primary,
                    disabledContainerColor = Color.Transparent,
                    disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
                )
            ButtonType.Tonal ->
                ButtonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                    disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
                )
        }

    val responsiveMultiplier =
        when {
            responsiveLayout.isTablet -> 1.1f
            responsiveLayout.isLargeScreen -> 1.2f
            else -> 1.0f
        }

    return ButtonConfig(
        height = baseHeight * responsiveMultiplier,
        cornerRadius = baseCornerRadius * responsiveMultiplier,
        contentPadding =
            PaddingValues(
                horizontal = 16.dp * responsiveMultiplier,
                vertical = 12.dp * responsiveMultiplier,
            ),
        containerColor = colors.containerColor,
        contentColor = colors.contentColor,
        disabledContainerColor = colors.disabledContainerColor,
        disabledContentColor = colors.disabledContentColor,
    )
}

/**
 * 按鈕顏色數據類
 */
private data class ButtonColors(
    val containerColor: Color,
    val contentColor: Color,
    val disabledContainerColor: Color,
    val disabledContentColor: Color,
)

/**
 * 便捷的按鈕組件 - 主要按鈕
 */
@Composable
fun PrimaryButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: ButtonSize = ButtonSize.Medium,
    enabled: Boolean = true,
    content: @Composable () -> Unit,
) {
    UnifiedButton(
        onClick = onClick,
        modifier = modifier,
        buttonType = ButtonType.Primary,
        size = size,
        enabled = enabled,
        content = content,
    )
}

/**
 * 便捷的按鈕組件 - 次要按鈕
 */
@Composable
fun SecondaryButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: ButtonSize = ButtonSize.Medium,
    enabled: Boolean = true,
    content: @Composable () -> Unit,
) {
    UnifiedButton(
        onClick = onClick,
        modifier = modifier,
        buttonType = ButtonType.Secondary,
        size = size,
        enabled = enabled,
        content = content,
    )
}

/**
 * 便捷的按鈕組件 - 文字按鈕
 */
@Composable
fun TextButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: ButtonSize = ButtonSize.Medium,
    enabled: Boolean = true,
    content: @Composable () -> Unit,
) {
    UnifiedButton(
        onClick = onClick,
        modifier = modifier,
        buttonType = ButtonType.Text,
        size = size,
        enabled = enabled,
        content = content,
    )
}

/**
 * 便捷的按鈕組件 - 色調按鈕
 */
@Composable
fun TonalButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: ButtonSize = ButtonSize.Medium,
    enabled: Boolean = true,
    content: @Composable () -> Unit,
) {
    UnifiedButton(
        onClick = onClick,
        modifier = modifier,
        buttonType = ButtonType.Tonal,
        size = size,
        enabled = enabled,
        content = content,
    )
}
