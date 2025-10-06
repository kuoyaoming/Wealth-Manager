package com.wealthmanager.ui.responsive

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import com.wealthmanager.ui.components.DialogCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.wealthmanager.accessibility.rememberAccessibilityState
import androidx.compose.ui.window.DialogProperties as WindowDialogProperties

/**
 * 響應式對話框
 * 符合2025 Android設計指導的對話框實現
 */
@Composable
fun ResponsiveDialog(
    onDismissRequest: () -> Unit,
    title: String? = null,
    content: @Composable () -> Unit,
    confirmButton: (@Composable () -> Unit)? = null,
    dismissButton: (@Composable () -> Unit)? = null,
    properties: WindowDialogProperties =
        WindowDialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false,
        ),
) {
    val configuration = LocalConfiguration.current
    val accessibilityState = rememberAccessibilityState()
    val responsiveLayout = rememberResponsiveLayout()

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = properties,
    ) {
        DialogCard(
            modifier =
                Modifier
                    .fillMaxWidth(
                        when {
                            configuration.screenWidthDp >= 840 -> 0.6f // Large screens
                            configuration.screenWidthDp >= 600 -> 0.7f // Tablets
                            else -> 0.9f // Phones
                        },
                    )
                    .fillMaxSize(
                        when {
                            configuration.screenHeightDp >= 840 -> 0.7f // Large screens
                            configuration.screenHeightDp >= 600 -> 0.8f // Tablets
                            else -> 0.9f // Phones
                        },
                    )
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(
                                horizontal = responsiveLayout.paddingLarge,
                                vertical = responsiveLayout.paddingMedium,
                            ),
                    verticalArrangement =
                        Arrangement.spacedBy(
                            if (accessibilityState.isLargeFontEnabled) 16.dp else 12.dp,
                        ),
                ) {
                    // Title
                    title?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }

                    // Content
                    content()

                    // Buttons
                    if (confirmButton != null || dismissButton != null) {
                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            dismissButton?.let {
                                it()
                                if (confirmButton != null) {
                                    Spacer(modifier = Modifier.width(8.dp))
                                }
                            }
                            confirmButton?.let {
                                it()
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * 無障礙性按鈕
 */
@Composable
fun AccessibleButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable () -> Unit,
) {
    val accessibilityState = rememberAccessibilityState()

    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        contentPadding =
            PaddingValues(
                horizontal = if (accessibilityState.isLargeFontEnabled) 24.dp else 16.dp,
                vertical = if (accessibilityState.isLargeFontEnabled) 16.dp else 12.dp,
            ),
    ) {
        content()
    }
}

/**
 * 無障礙性輪廓按鈕
 */
@Composable
fun AccessibleOutlinedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable () -> Unit,
) {
    val accessibilityState = rememberAccessibilityState()

    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        contentPadding =
            PaddingValues(
                horizontal = if (accessibilityState.isLargeFontEnabled) 24.dp else 16.dp,
                vertical = if (accessibilityState.isLargeFontEnabled) 16.dp else 12.dp,
            ),
    ) {
        content()
    }
}
