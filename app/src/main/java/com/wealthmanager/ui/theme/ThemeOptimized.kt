package com.wealthmanager.ui.theme

import android.app.Activity
import android.os.Build
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
import androidx.core.view.WindowInsetsControllerCompat

/**
 * Optimized theme configuration with better color schemes
 */
private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80,
    background = androidx.compose.ui.graphics.Color(0xFF121212),
    surface = androidx.compose.ui.graphics.Color(0xFF1E1E1E),
    onBackground = androidx.compose.ui.graphics.Color(0xFFE1E1E1),
    onSurface = androidx.compose.ui.graphics.Color(0xFFE1E1E1),
    surfaceVariant = androidx.compose.ui.graphics.Color(0xFF2A2A2A),
    onSurfaceVariant = androidx.compose.ui.graphics.Color(0xFFB3B3B3),
    error = androidx.compose.ui.graphics.Color(0xFFCF6679),
    onError = androidx.compose.ui.graphics.Color(0xFF000000),
    outline = androidx.compose.ui.graphics.Color(0xFF4A4A4A),
    inverseSurface = androidx.compose.ui.graphics.Color(0xFFE1E1E1),
    inverseOnSurface = androidx.compose.ui.graphics.Color(0xFF1A1A1A),
    inversePrimary = Purple40
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40,
    background = androidx.compose.ui.graphics.Color(0xFFFFFBFE),
    surface = androidx.compose.ui.graphics.Color(0xFFFFFBFE),
    onBackground = androidx.compose.ui.graphics.Color(0xFF1C1B1F),
    onSurface = androidx.compose.ui.graphics.Color(0xFF1C1B1F),
    surfaceVariant = androidx.compose.ui.graphics.Color(0xFFE7E0EC),
    onSurfaceVariant = androidx.compose.ui.graphics.Color(0xFF49454F),
    error = androidx.compose.ui.graphics.Color(0xFFBA1A1A),
    onError = androidx.compose.ui.graphics.Color(0xFFFFFFFF),
    outline = androidx.compose.ui.graphics.Color(0xFF79747E),
    inverseSurface = androidx.compose.ui.graphics.Color(0xFF313033),
    inverseOnSurface = androidx.compose.ui.graphics.Color(0xFFF4EFF4),
    inversePrimary = Purple80
)

@Composable
fun WealthManagerThemeOptimized(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                @Suppress("DEPRECATION")
                window.statusBarColor = colorScheme.primary.toArgb()
            }
            
            // Set status bar appearance
            insetsController.isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

/**
 * Extended color scheme for financial app specific colors
 */
object FinancialColors {
    val positiveGreen = androidx.compose.ui.graphics.Color(0xFF4CAF50)
    val negativeRed = androidx.compose.ui.graphics.Color(0xFFF44336)
    val neutralGray = androidx.compose.ui.graphics.Color(0xFF9E9E9E)
    val warningOrange = androidx.compose.ui.graphics.Color(0xFFFF9800)
    val infoBlue = androidx.compose.ui.graphics.Color(0xFF2196F3)
    
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
        return if (MaterialTheme.colorScheme.error == androidx.compose.ui.graphics.Color(0xFFBA1A1A)) {
            negativeRed
        } else {
            MaterialTheme.colorScheme.error
        }
    }
    
    @Composable
    fun getNeutralColor(): androidx.compose.ui.graphics.Color {
        return MaterialTheme.colorScheme.onSurfaceVariant
    }
    
    @Composable
    fun getWarningColor(): androidx.compose.ui.graphics.Color {
        return warningOrange
    }
    
    @Composable
    fun getInfoColor(): androidx.compose.ui.graphics.Color {
        return infoBlue
    }
}
