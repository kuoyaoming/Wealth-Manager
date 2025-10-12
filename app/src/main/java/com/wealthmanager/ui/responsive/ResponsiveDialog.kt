package com.wealthmanager.ui.responsive

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.window.Dialog
import com.wealthmanager.ui.components.DialogCard
import androidx.compose.ui.window.DialogProperties as WindowDialogProperties

/**
 * A responsive dialog that adapts its size to different screen dimensions.
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
                    ),
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
                    verticalArrangement = Arrangement.spacedBy(responsiveLayout.paddingMedium),
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
                        Spacer(modifier = Modifier.height(responsiveLayout.paddingLarge))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            dismissButton?.invoke()
                            if (dismissButton != null && confirmButton != null) {
                                Spacer(modifier = Modifier.width(responsiveLayout.paddingSmall))
                            }
                            confirmButton?.invoke()
                        }
                    }
                }
            }
        }
    }
}
