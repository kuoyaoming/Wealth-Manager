package com.wealthmanager.ui.responsive

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Responsive layout manager
 * Dynamically adjust layout parameters based on screen size and orientation
 */
@Composable
fun rememberResponsiveLayout(): ResponsiveLayout {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
    val widthSizeClass = LocalWindowWidthSizeClass.current

    return remember(screenWidth, screenHeight, isLandscape, widthSizeClass) {
        ResponsiveLayout(
            screenWidth = screenWidth,
            screenHeight = screenHeight,
            isLandscape = isLandscape,
            widthSizeClass = widthSizeClass
        )
    }
}

data class ResponsiveLayout(
    val screenWidth: Dp,
    val screenHeight: Dp,
    val isLandscape: Boolean,
    val widthSizeClass: WindowWidthSizeClass
) {
    // Screen type detection
    val isTablet: Boolean
        get() = screenWidth >= 600.dp || screenHeight >= 600.dp

    val isPhone: Boolean
        get() = !isTablet

    val isLargeScreen: Boolean
        get() = screenWidth >= 840.dp || screenHeight >= 840.dp

    // Dynamic spacing
    val paddingSmall: Dp
        get() = if (isTablet) 8.dp else 4.dp

    val paddingMedium: Dp
        get() = if (isTablet) 16.dp else 8.dp

    val paddingLarge: Dp
        get() = if (isTablet) 24.dp else 16.dp

    val paddingExtraLarge: Dp
        get() = if (isTablet) 32.dp else 24.dp

    // Dynamic font size
    val titleLargeSize: Dp
        get() = if (isTablet) 28.dp else 24.dp

    val titleMediumSize: Dp
        get() = if (isTablet) 24.dp else 20.dp

    val bodyLargeSize: Dp
        get() = if (isTablet) 18.dp else 16.dp

    val bodyMediumSize: Dp
        get() = if (isTablet) 16.dp else 14.dp

    // Card spacing
    val cardSpacing: Dp
        get() = if (isTablet) 16.dp else 12.dp

    // Chart height
    val chartHeight: Dp
        get() = if (isTablet) 300.dp else 200.dp

    // Dialog width
    val dialogWidth: Dp
        get() = when {
            isLargeScreen -> 600.dp
            isTablet -> 500.dp
            else -> screenWidth * 0.9f
        }

    // Dialog height
    val dialogHeight: Dp
        get() = when {
            isLargeScreen -> 500.dp
            isTablet -> 400.dp
            else -> screenHeight * 0.8f
        }

    // Column configuration
    val columns: Int
        get() = when (widthSizeClass) {
            WindowWidthSizeClass.Compact -> 1
            WindowWidthSizeClass.Medium -> 2
            WindowWidthSizeClass.Expanded -> 2 // Mobile target only, no large screen specialization yet
            else -> 1
        }

    // Grid spacing
    val gridSpacing: Dp
        get() = when (widthSizeClass) {
            WindowWidthSizeClass.Compact -> 8.dp
            WindowWidthSizeClass.Medium -> 16.dp
            WindowWidthSizeClass.Expanded -> 16.dp
            else -> 8.dp
        }
}

/**
 * Responsive card container
 */
@Composable
fun ResponsiveCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    val responsiveLayout = rememberResponsiveLayout()

    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (responsiveLayout.isTablet) 6.dp else 4.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(responsiveLayout.paddingLarge),
            content = content
        )
    }
}

/**
 * Responsive spacing
 */
@Composable
fun ResponsiveSpacer(
    size: ResponsiveSpacerSize = ResponsiveSpacerSize.Medium
) {
    val responsiveLayout = rememberResponsiveLayout()
    val spacerSize = when (size) {
        ResponsiveSpacerSize.Small -> responsiveLayout.paddingSmall
        ResponsiveSpacerSize.Medium -> responsiveLayout.paddingMedium
        ResponsiveSpacerSize.Large -> responsiveLayout.paddingLarge
        ResponsiveSpacerSize.ExtraLarge -> responsiveLayout.paddingExtraLarge
    }

    Spacer(modifier = Modifier.height(spacerSize))
}

enum class ResponsiveSpacerSize {
    Small, Medium, Large, ExtraLarge
}

/**
 * Responsive grid layout
 */
@Composable
fun ResponsiveGrid(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val responsiveLayout = rememberResponsiveLayout()

    if (responsiveLayout.columns > 1) {
        // Use grid layout
        LazyVerticalGrid(
            columns = androidx.compose.foundation.lazy.grid.GridCells.Fixed(responsiveLayout.columns),
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(responsiveLayout.gridSpacing),
            horizontalArrangement = Arrangement.spacedBy(responsiveLayout.gridSpacing)
        ) {
            item { content() }
        }
    } else {
        // Single column layout
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(responsiveLayout.cardSpacing)
        ) {
            content()
        }
    }
}
