package com.wealthmanager.ui.responsive

import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.staticCompositionLocalOf

// CompositionLocal to provide WindowWidthSizeClass across the app.
val LocalWindowWidthSizeClass =
    staticCompositionLocalOf<WindowWidthSizeClass> {
        WindowWidthSizeClass.Compact
    }
