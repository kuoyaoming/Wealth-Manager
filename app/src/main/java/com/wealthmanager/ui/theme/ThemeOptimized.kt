package com.wealthmanager.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * Optimized theme configuration with better color schemes
 */
private val DarkColorScheme =
    darkColorScheme(
        primary = Purple80,
        secondary = PurpleGrey80,
        tertiary = Pink80,
        background = Color(0xFF1C1B1F),
        surface = Color(0xFF1C1B1F),
        onBackground = Color(0xFFE6E1E5),
        onSurface = Color(0xFFE6E1E5),
        surfaceVariant = Color(0xFF49454F),
        onSurfaceVariant = Color(0xFFCAC4D0),
        error = Color(0xFFF2B8B5),
        onError = Color(0xFF601410),
        outline = Color(0xFF938F99),
        inverseSurface = Color(0xFFE6E1E5),
        inverseOnSurface = Color(0xFF313033),
        inversePrimary = Purple40,
    )

private val LightColorScheme =
    lightColorScheme(
        primary = Purple40,
        secondary = PurpleGrey40,
        tertiary = Pink40,
        background = Color(0xFFFFFBFE),
        surface = Color(0xFFFFFBFE),
        onBackground = Color(0xFF1C1B1F),
        onSurface = Color(0xFF1C1B1F),
        surfaceVariant = Color(0xFFE7E0EC),
        onSurfaceVariant = Color(0xFF49454F),
        error = Color(0xFFBA1A1A),
        onError = Color(0xFFFFFFFF),
        outline = Color(0xFF79747E),
        inverseSurface = Color(0xFF313033),
        inverseOnSurface = Color(0xFFF4EFF4),
        inversePrimary = Purple80,
    )

@Composable
fun WealthManagerThemeOptimized(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit,
) {
    val colorScheme =
        when {
            dynamicColor -> {
                val context = LocalContext.current
                if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            }

            darkTheme -> DarkColorScheme
            else -> LightColorScheme
        }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            val insetsController = WindowCompat.getInsetsController(window, view)

            // Use modern API for status bar color
            window.statusBarColor = colorScheme.primary.toArgb()

            // Set status bar appearance
            insetsController.isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
    )
}

/**
 * Extended color scheme for financial app specific colors
 */
object FinancialColors {
    val positiveGreen @Composable get() = colorResource(id = R.color.success_green)
    val negativeRed @Composable get() = colorResource(id = R.color.negative_red)
    val neutralGray @Composable get() = colorResource(id = R.color.neutral_gray)
    val warningOrange @Composable get() = colorResource(id = R.color.warning_orange)
    val infoBlue @Composable get() = colorResource(id = R.color.info_blue)

    @Composable
    fun getPositiveColor(): androidx.compose.ui.graphics.Color {
        return if (MaterialTheme.colorScheme.primary == Purple40) {
            positiveGreen
        } else {
            MaterialTheme.colorScheme.primary
        }
    }

    @Composable
    fun getNegativeColor(): androidx.compose.ui.graphics.Color {
        return if (MaterialTheme.colorScheme.error == colorResource(id = R.color.md_theme_light_error)) {
            negativeRed
        } else {
            MaterialTheme.colorScheme.error
        }
    }

    @Composable
    fun getNeutralColor(): androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurfaceVariant

    @Composable
    fun getWarningColor(): androidx.compose.ui.graphics.Color = warningOrange

    @Composable
    fun getInfoColor(): androidx.compose.ui.graphics.Color = infoBlue
}
