package com.wealthmanager.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Material Design 3 Typography Scale
 * Fully compliant with Material Design 3 and Android SDK 36 guidelines
 * Optimized line height and character spacing for Traditional Chinese
 * Updated according to Android 2025 official design guidelines
 */
val Typography =
    Typography(
        // Display styles - for largest text like statistics
        displayLarge =
            TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Normal,
                fontSize = 57.sp,
                lineHeight = 68.sp, // Increased line height for Traditional Chinese
                letterSpacing = (-0.25).sp,
            ),
        displayMedium =
            TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Normal,
                fontSize = 45.sp,
                lineHeight = 56.sp, // Increased line height for Traditional Chinese
                letterSpacing = 0.sp,
            ),
        displaySmall =
            TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Normal,
                fontSize = 36.sp,
                lineHeight = 48.sp, // Increased line height for Traditional Chinese
                letterSpacing = 0.sp,
            ),
        // Headline styles - for section titles
        headlineLarge =
            TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Normal,
                fontSize = 32.sp,
                lineHeight = 44.sp, // Increased line height for Traditional Chinese
                letterSpacing = 0.sp,
            ),
        headlineMedium =
            TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Normal,
                fontSize = 28.sp,
                lineHeight = 40.sp, // Increased line height for Traditional Chinese
                letterSpacing = 0.sp,
            ),
        headlineSmall =
            TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Normal,
                fontSize = 24.sp,
                lineHeight = 36.sp, // Increased line height for Traditional Chinese
                letterSpacing = 0.sp,
            ),
        // Title styles - for card titles, dialog titles
        titleLarge =
            TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Normal,
                fontSize = 22.sp,
                lineHeight = 32.sp, // 針對繁體中文增加行高
                letterSpacing = 0.sp,
            ),
        titleMedium =
            TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                lineHeight = 28.sp, // 針對繁體中文增加行高
                letterSpacing = 0.1.sp, // 減少字距以適應中文
            ),
        titleSmall =
            TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                lineHeight = 24.sp, // 針對繁體中文增加行高
                letterSpacing = 0.05.sp, // 減少字距以適應中文
            ),
        // Body styles - for main content (most commonly used)
        bodyLarge =
            TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                lineHeight = 28.sp, // 針對繁體中文增加行高
                letterSpacing = 0.1.sp, // 減少字距以適應中文
            ),
        bodyMedium =
            TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                lineHeight = 24.sp, // 針對繁體中文增加行高
                letterSpacing = 0.05.sp, // 減少字距以適應中文
            ),
        bodySmall =
            TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp,
                lineHeight = 20.sp, // 針對繁體中文增加行高
                letterSpacing = 0.05.sp, // 減少字距以適應中文
            ),
        // Label styles - for buttons, labels
        labelLarge =
            TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                lineHeight = 24.sp, // 針對繁體中文增加行高
                letterSpacing = 0.05.sp, // 減少字距以適應中文
            ),
        labelMedium =
            TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                lineHeight = 20.sp, // 針對繁體中文增加行高
                letterSpacing = 0.1.sp, // 減少字距以適應中文
            ),
        labelSmall =
            TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Medium,
                fontSize = 11.sp,
                lineHeight = 18.sp, // 針對繁體中文增加行高
                letterSpacing = 0.1.sp, // 減少字距以適應中文
            ),
    )
