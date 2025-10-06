package com.wealthmanager.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

/**
 * Standardized text component system
 * Based on Android 2025 official design guidelines and Material Design 3 specifications
 * Ensures consistency and accessibility of all text in the application
 */

/**
 * Text usage enumeration - defines text styles for different scenarios
 */
enum class TextPurpose {
    // Display text for large data display
    DISPLAY_LARGE,
    DISPLAY_MEDIUM,
    DISPLAY_SMALL,
    
    // Title text for page and section titles
    HEADLINE_LARGE,
    HEADLINE_MEDIUM,
    HEADLINE_SMALL,
    
    // Title text for cards and dialog titles
    TITLE_LARGE,
    TITLE_MEDIUM,
    TITLE_SMALL,
    
    // Body text for main content
    BODY_LARGE,
    BODY_MEDIUM,
    BODY_SMALL,
    
    // Label text for buttons and labels
    LABEL_LARGE,
    LABEL_MEDIUM,
    LABEL_SMALL,
    
    // Special purposes
    CAPTION,      // Caption text
    OVERLINE,     // Overline text
    BUTTON_TEXT,  // Button text
    ERROR_TEXT,   // Error text
    SUCCESS_TEXT, // Success text
    WARNING_TEXT, // Warning text
    INFO_TEXT,    // Info text
}

/**
 * Standardized text component
 * 
 * @param text Text content
 * @param purpose Text purpose, determines style and semantics
 * @param modifier Modifier
 * @param color Text color, defaults to theme color
 * @param maxLines Maximum number of lines
 * @param overflow Text overflow handling
 */
@Composable
fun StandardizedText(
    text: String,
    purpose: TextPurpose,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    maxLines: Int = Int.MAX_VALUE,
    overflow: androidx.compose.ui.text.style.TextOverflow = androidx.compose.ui.text.style.TextOverflow.Clip
) {
    val (textStyle, defaultColor) = getTextStyleAndColor(purpose)
    
    Text(
        text = text,
        style = textStyle,
        color = if (color != Color.Unspecified) color else defaultColor,
        modifier = modifier,
        maxLines = maxLines,
        overflow = overflow
    )
}

/**
 * Get corresponding style and color based on text purpose
 */
@Composable
private fun getTextStyleAndColor(purpose: TextPurpose): Pair<TextStyle, Color> {
    return when (purpose) {
        // Display styles
        TextPurpose.DISPLAY_LARGE -> MaterialTheme.typography.displayLarge to MaterialTheme.colorScheme.onBackground
        TextPurpose.DISPLAY_MEDIUM -> MaterialTheme.typography.displayMedium to MaterialTheme.colorScheme.onBackground
        TextPurpose.DISPLAY_SMALL -> MaterialTheme.typography.displaySmall to MaterialTheme.colorScheme.onBackground
        
        // Headline styles
        TextPurpose.HEADLINE_LARGE -> MaterialTheme.typography.headlineLarge to MaterialTheme.colorScheme.onBackground
        TextPurpose.HEADLINE_MEDIUM -> MaterialTheme.typography.headlineMedium to MaterialTheme.colorScheme.onBackground
        TextPurpose.HEADLINE_SMALL -> MaterialTheme.typography.headlineSmall to MaterialTheme.colorScheme.onBackground
        
        // Title styles
        TextPurpose.TITLE_LARGE -> MaterialTheme.typography.titleLarge to MaterialTheme.colorScheme.onSurface
        TextPurpose.TITLE_MEDIUM -> MaterialTheme.typography.titleMedium to MaterialTheme.colorScheme.onSurface
        TextPurpose.TITLE_SMALL -> MaterialTheme.typography.titleSmall to MaterialTheme.colorScheme.onSurface
        
        // Body styles
        TextPurpose.BODY_LARGE -> MaterialTheme.typography.bodyLarge to MaterialTheme.colorScheme.onSurface
        TextPurpose.BODY_MEDIUM -> MaterialTheme.typography.bodyMedium to MaterialTheme.colorScheme.onSurface
        TextPurpose.BODY_SMALL -> MaterialTheme.typography.bodySmall to MaterialTheme.colorScheme.onSurfaceVariant
        
        // Label styles
        TextPurpose.LABEL_LARGE -> MaterialTheme.typography.labelLarge to MaterialTheme.colorScheme.onSurface
        TextPurpose.LABEL_MEDIUM -> MaterialTheme.typography.labelMedium to MaterialTheme.colorScheme.onSurfaceVariant
        TextPurpose.LABEL_SMALL -> MaterialTheme.typography.labelSmall to MaterialTheme.colorScheme.onSurfaceVariant
        
        // Special purposes
        TextPurpose.CAPTION -> MaterialTheme.typography.bodySmall to MaterialTheme.colorScheme.onSurfaceVariant
        TextPurpose.OVERLINE -> MaterialTheme.typography.labelSmall.copy(
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            letterSpacing = 1.5.sp
        ) to MaterialTheme.colorScheme.onSurfaceVariant
        TextPurpose.BUTTON_TEXT -> MaterialTheme.typography.labelLarge to MaterialTheme.colorScheme.onPrimary
        TextPurpose.ERROR_TEXT -> MaterialTheme.typography.bodyMedium to MaterialTheme.colorScheme.error
        TextPurpose.SUCCESS_TEXT -> MaterialTheme.typography.bodyMedium to Color(0xFF4CAF50)
        TextPurpose.WARNING_TEXT -> MaterialTheme.typography.bodyMedium to Color(0xFFFF9800)
        TextPurpose.INFO_TEXT -> MaterialTheme.typography.bodyMedium to MaterialTheme.colorScheme.primary
    }
}

/**
 * 便捷的文字組件 - 頁面標題
 */
@Composable
fun PageTitle(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified
) {
    StandardizedText(
        text = text,
        purpose = TextPurpose.HEADLINE_LARGE,
        modifier = modifier,
        color = color
    )
}

/**
 * 便捷的文字組件 - 區塊標題
 */
@Composable
fun SectionTitle(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified
) {
    StandardizedText(
        text = text,
        purpose = TextPurpose.HEADLINE_SMALL,
        modifier = modifier,
        color = color
    )
}

/**
 * 便捷的文字組件 - 卡片標題
 */
@Composable
fun CardTitle(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified
) {
    StandardizedText(
        text = text,
        purpose = TextPurpose.TITLE_MEDIUM,
        modifier = modifier,
        color = color
    )
}

/**
 * 便捷的文字組件 - 主要內容
 */
@Composable
fun BodyText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    maxLines: Int = Int.MAX_VALUE
) {
    StandardizedText(
        text = text,
        purpose = TextPurpose.BODY_MEDIUM,
        modifier = modifier,
        color = color,
        maxLines = maxLines
    )
}

/**
 * 便捷的文字組件 - 次要內容
 */
@Composable
fun SecondaryText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    maxLines: Int = Int.MAX_VALUE
) {
    StandardizedText(
        text = text,
        purpose = TextPurpose.BODY_SMALL,
        modifier = modifier,
        color = color,
        maxLines = maxLines
    )
}

/**
 * 便捷的文字組件 - 說明文字
 */
@Composable
fun CaptionText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified
) {
    StandardizedText(
        text = text,
        purpose = TextPurpose.CAPTION,
        modifier = modifier,
        color = color
    )
}

/**
 * 便捷的文字組件 - 錯誤文字
 */
@Composable
fun ErrorText(
    text: String,
    modifier: Modifier = Modifier
) {
    StandardizedText(
        text = text,
        purpose = TextPurpose.ERROR_TEXT,
        modifier = modifier
    )
}

/**
 * 便捷的文字組件 - 成功文字
 */
@Composable
fun SuccessText(
    text: String,
    modifier: Modifier = Modifier
) {
    StandardizedText(
        text = text,
        purpose = TextPurpose.SUCCESS_TEXT,
        modifier = modifier
    )
}

/**
 * 便捷的文字組件 - 警告文字
 */
@Composable
fun WarningText(
    text: String,
    modifier: Modifier = Modifier
) {
    StandardizedText(
        text = text,
        purpose = TextPurpose.WARNING_TEXT,
        modifier = modifier
    )
}

/**
 * 便捷的文字組件 - 信息文字
 */
@Composable
fun InfoText(
    text: String,
    modifier: Modifier = Modifier
) {
    StandardizedText(
        text = text,
        purpose = TextPurpose.INFO_TEXT,
        modifier = modifier
    )
}
