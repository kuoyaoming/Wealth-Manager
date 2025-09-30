package com.wealthmanager.wear.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.wear.compose.material.Colors
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Shapes
import androidx.wear.compose.material.Typography

private val DarkColorPalette = Colors(
    primary = PrimaryGreen,
    primaryVariant = PrimaryGreenVariant,
    secondary = SecondaryAmber,
    secondaryVariant = SecondaryAmberVariant,
    background = DarkBackground,
    surface = DarkSurface,
    onPrimary = TextLight,
    onSecondary = TextDark,
    onBackground = TextLight,
    onSurface = TextLight,
    error = ErrorRed,
    onError = TextLight
)

private val LightColorPalette = Colors(
    primary = PrimaryGreen,
    primaryVariant = PrimaryGreenVariant,
    secondary = SecondaryAmber,
    secondaryVariant = SecondaryAmberVariant,
    background = LightBackground,
    surface = LightSurface,
    onPrimary = TextLight,
    onSecondary = TextDark,
    onBackground = TextDark,
    onSurface = TextDark,
    error = ErrorRed,
    onError = TextLight
)

@Composable
fun WearWealthTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColorPalette else LightColorPalette

    MaterialTheme(
        colors = colors,
        typography = Typography(),
        shapes = Shapes(),
        content = content
    )
}

