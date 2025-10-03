package com.wealthmanager.ui.about

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
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
import com.wealthmanager.ui.settings.ApiKeyGuideDialog

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

    // 使用 LaunchedEffect 和 remember 來穩定滾動狀態檢測
    var canScrollDown by remember { mutableStateOf(false) }
    var canScrollUp by remember { mutableStateOf(false) }

    LaunchedEffect(scrollState.value, scrollState.maxValue) {
        canScrollDown = scrollState.value < scrollState.maxValue - 10 // 添加緩衝區避免邊界問題
        canScrollUp = scrollState.value > 10 // 添加緩衝區避免邊界問題
    }

    var showApiGuideDialog by remember { mutableStateOf(false) }

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
                            .padding(bottom = 16.dp)
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

                    // API Key Guide entry (opens detailed guide dialog)
                    ApiKeyGuideEntrySection(
                        onShowGuide = {
                            hapticManager.triggerHaptic(view, HapticFeedbackManager.HapticIntensity.MEDIUM)
                            showApiGuideDialog = true
                        },
                    )

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

                // Scroll hint overlays inside Box to ensure correct positioning
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
                                )
                                .align(Alignment.TopCenter),
                    )
                }

                // 底部滾動指示器 - 移到內容區域外，避免重疊
                if (canScrollDown) {
                    Box(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .height(80.dp) // 增加高度確保漸層完整顯示
                                .align(Alignment.BottomCenter)
                                .background(
                                    brush =
                                        androidx.compose.ui.graphics.Brush.verticalGradient(
                                            colors =
                                                listOf(
                                                    MaterialTheme.colorScheme.surface.copy(alpha = 0f),
                                                    MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                                                ),
                                        ),
                                ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Surface(
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

    // API Key guide dialog (launched from About)
    if (showApiGuideDialog) {
        ApiKeyGuideDialog(onDismiss = { showApiGuideDialog = false })
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

@Composable
private fun ApiKeyGuideEntrySection(onShowGuide: () -> Unit) {
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
                    imageVector = Icons.Default.Key,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = stringResource(R.string.settings_apply_api_keys),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
            }

            Text(
                text = stringResource(R.string.settings_apply_api_keys_desc),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Button(onClick = onShowGuide) {
                    Text(stringResource(R.string.dialog_apply_tutorial))
                }
            }
        }
    }
}
