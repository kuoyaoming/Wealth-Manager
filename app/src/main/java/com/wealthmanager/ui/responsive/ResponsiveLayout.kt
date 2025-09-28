package com.wealthmanager.ui.responsive

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * 響應式佈局管理器
 * 根據螢幕尺寸和方向動態調整佈局參數
 */
@Composable
fun rememberResponsiveLayout(): ResponsiveLayout {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
    
    return remember(screenWidth, screenHeight, isLandscape) {
        ResponsiveLayout(
            screenWidth = screenWidth,
            screenHeight = screenHeight,
            isLandscape = isLandscape
        )
    }
}

data class ResponsiveLayout(
    val screenWidth: Dp,
    val screenHeight: Dp,
    val isLandscape: Boolean
) {
    // 螢幕類型判斷
    val isTablet: Boolean
        get() = screenWidth >= 600.dp || screenHeight >= 600.dp
    
    val isPhone: Boolean
        get() = !isTablet
    
    val isLargeScreen: Boolean
        get() = screenWidth >= 840.dp || screenHeight >= 840.dp
    
    // 動態間距
    val paddingSmall: Dp
        get() = if (isTablet) 8.dp else 4.dp
    
    val paddingMedium: Dp
        get() = if (isTablet) 16.dp else 8.dp
    
    val paddingLarge: Dp
        get() = if (isTablet) 24.dp else 16.dp
    
    val paddingExtraLarge: Dp
        get() = if (isTablet) 32.dp else 24.dp
    
    // 動態字體大小
    val titleLargeSize: Dp
        get() = if (isTablet) 28.dp else 24.dp
    
    val titleMediumSize: Dp
        get() = if (isTablet) 24.dp else 20.dp
    
    val bodyLargeSize: Dp
        get() = if (isTablet) 18.dp else 16.dp
    
    val bodyMediumSize: Dp
        get() = if (isTablet) 16.dp else 14.dp
    
    // 卡片間距
    val cardSpacing: Dp
        get() = if (isTablet) 16.dp else 12.dp
    
    // 圖表高度
    val chartHeight: Dp
        get() = if (isTablet) 300.dp else 200.dp
    
    // 對話框寬度
    val dialogWidth: Dp
        get() = when {
            isLargeScreen -> 600.dp
            isTablet -> 500.dp
            else -> screenWidth * 0.9f
        }
    
    // 對話框高度
    val dialogHeight: Dp
        get() = when {
            isLargeScreen -> 500.dp
            isTablet -> 400.dp
            else -> screenHeight * 0.8f
        }
    
    // 列數配置
    val columns: Int
        get() = when {
            isLargeScreen && isLandscape -> 3
            isTablet && isLandscape -> 2
            isTablet -> 2
            else -> 1
        }
    
    // 網格間距
    val gridSpacing: Dp
        get() = if (isTablet) 16.dp else 8.dp
}

/**
 * 響應式卡片容器
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
 * 響應式間距
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
 * 響應式網格佈局
 */
@Composable
fun ResponsiveGrid(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val responsiveLayout = rememberResponsiveLayout()
    
    if (responsiveLayout.columns > 1) {
        // 使用網格佈局
        LazyVerticalGrid(
            columns = androidx.compose.foundation.lazy.grid.GridCells.Fixed(responsiveLayout.columns),
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(responsiveLayout.gridSpacing),
            horizontalArrangement = Arrangement.spacedBy(responsiveLayout.gridSpacing)
        ) {
            item { content() }
        }
    } else {
        // 單列佈局
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(responsiveLayout.cardSpacing)
        ) {
            content()
        }
    }
}