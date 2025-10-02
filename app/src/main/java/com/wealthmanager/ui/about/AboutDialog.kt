package com.wealthmanager.ui.about

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.wealthmanager.BuildConfig
import com.wealthmanager.R
import com.wealthmanager.data.FirstLaunchManager
import com.wealthmanager.haptic.HapticFeedbackManager
import com.wealthmanager.haptic.rememberHapticFeedbackWithView

/**
 * About Dialog Component
 * Shows app information, data usage policy, third-party API usage, and compliance information
 */
@Composable
fun AboutDialog(
    onDismiss: () -> Unit,
    firstLaunchManager: FirstLaunchManager? = null,
) {
    // Context is available but not currently used in this dialog
    // val context = LocalContext.current
    val (hapticManager, view) = rememberHapticFeedbackWithView()
    val scrollState = rememberScrollState()

    val canScrollDown = scrollState.value < scrollState.maxValue
    val canScrollUp = scrollState.value > 0

    Dialog(
        onDismissRequest = onDismiss,
        properties =
            DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = false,
                usePlatformDefaultWidth = false,
            ),
    ) {
        Card(
            modifier =
                Modifier
                    .fillMaxWidth(0.92f)
                    .fillMaxHeight(0.92f)
                    .padding(vertical = 16.dp, horizontal = 16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .verticalScroll(scrollState),
                ) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = stringResource(R.string.about_title),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                        )

                        IconButton(onClick = {
                            hapticManager.triggerHaptic(view, HapticFeedbackManager.HapticIntensity.LIGHT)
                            onDismiss()
                        }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = stringResource(R.string.cd_close),
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Content (cards are added directly, scrolling handled by outer Column)
                    // App Version and Info
                    AppVersionSection()

                    Spacer(modifier = Modifier.height(12.dp))

                    // Data Usage Policy
                    DataUsageSection()

                    Spacer(modifier = Modifier.height(12.dp))

                    // Third-party API Usage
                    ThirdPartyApiSection()

                    Spacer(modifier = Modifier.height(12.dp))

                    // Security and Compliance
                    SecurityComplianceSection()

                    Spacer(modifier = Modifier.height(12.dp))

                    // Privacy Policy
                    PrivacyPolicySection()

                    Spacer(modifier = Modifier.height(16.dp))

                    // Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                    ) {
                        TextButton(onClick = {
                            hapticManager.triggerHaptic(view, HapticFeedbackManager.HapticIntensity.LIGHT)
                            onDismiss()
                        }) {
                            Text(stringResource(R.string.action_later))
                        }

                        Button(
                            onClick = {
                                hapticManager.triggerHaptic(view, HapticFeedbackManager.HapticIntensity.CONFIRM)
                                firstLaunchManager?.markAboutDialogShown()
                                onDismiss()
                            },
                        ) {
                            Text(stringResource(R.string.action_i_understand))
                        }
                    }
                }
            }

            if (canScrollUp) {
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(32.dp)
                            .background(
                                brush =
                                    androidx.compose.ui.graphics.Brush.verticalGradient(
                                        colors =
                                            listOf(
                                                MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                                                MaterialTheme.colorScheme.surface.copy(alpha = 0f),
                                            ),
                                    ),
                            ),
                )
            }

            if (canScrollDown) {
                Column(
                    modifier =
                        Modifier
                            .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally, // This is correct for Column
                ) {
                    Box(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .height(80.dp)
                                .background(
                                    brush =
                                        androidx.compose.ui.graphics.Brush.verticalGradient(
                                            colors =
                                                listOf(
                                                    MaterialTheme.colorScheme.surface.copy(alpha = 0f),
                                                    MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                                                ),
                                        ),
                                ),
                    )

                    Surface(
                        modifier =
                            Modifier
                                .padding(bottom = 16.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f),
                        shape = MaterialTheme.shapes.small,
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                        ) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = stringResource(R.string.cd_scroll_down_more),
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(20.dp),
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = stringResource(R.string.scroll_down_more),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AppVersionSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = stringResource(R.string.about_app_information_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
            }

            Text(
                text =
                    stringResource(
                        R.string.about_version_format,
                        BuildConfig.VERSION_NAME,
                        BuildConfig.VERSION_CODE,
                    ),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
            )

            Text(
                text = stringResource(R.string.about_information_description),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
private fun DataUsageSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.Storage,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = stringResource(R.string.about_data_usage_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
            }

            Text(
                text = stringResource(R.string.about_data_usage_description),
                style = MaterialTheme.typography.bodyMedium,
            )

            Text(
                text = stringResource(R.string.about_data_usage_points),
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

@Composable
private fun ThirdPartyApiSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = stringResource(R.string.about_third_party_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )

            Text(
                text = stringResource(R.string.about_third_party_description),
                style = MaterialTheme.typography.bodyMedium,
            )

            Text(
                text = stringResource(R.string.about_third_party_points),
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

@Composable
private fun SecurityComplianceSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = stringResource(R.string.about_security_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
            }

            Text(
                text = stringResource(R.string.about_security_points),
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

@Composable
private fun PrivacyPolicySection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = stringResource(R.string.about_privacy_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )

            Text(
                text = stringResource(R.string.about_privacy_description),
                style = MaterialTheme.typography.bodyMedium,
            )

            Text(
                text = stringResource(R.string.about_privacy_points),
                style = MaterialTheme.typography.bodySmall,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.about_privacy_acknowledgement),
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
            )
        }
    }
}
