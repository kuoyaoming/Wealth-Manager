package com.wealthmanager.ui.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.TrendingUp
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
import com.wealthmanager.R

/**
 * 歡迎導覽對話框
 * 介紹應用程式的核心價值與功能
 */
@Composable
fun WelcomeOnboardingDialog(
    onNext: () -> Unit,
    onSkip: () -> Unit,
) {
    Dialog(
        onDismissRequest = onSkip,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Card(
            modifier =
                Modifier
                    .fillMaxWidth(0.92f)
                    .fillMaxHeight(0.85f)
                    .padding(vertical = 16.dp, horizontal = 16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                Column(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(24.dp)
                            .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                // 標題和圖示
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp),
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text =
                            stringResource(
                                id = R.string.onboarding_welcome_title,
                                stringResource(id = R.string.app_name),
                            ),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 主要價值介紹
                Text(
                    text = stringResource(id = R.string.onboarding_welcome_subtitle),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 核心功能介紹
                val features =
                    listOf(
                        Triple(
                            Icons.Default.AccountBalance,
                            stringResource(id = R.string.feature_assets_tracking_title),
                            stringResource(id = R.string.feature_assets_tracking_desc),
                        ),
                        Triple(
                            Icons.Default.Security,
                            stringResource(id = R.string.feature_secure_backup_title),
                            stringResource(id = R.string.feature_secure_backup_desc),
                        ),
                        Triple(
                            Icons.Default.Watch,
                            stringResource(id = R.string.feature_wear_title),
                            stringResource(id = R.string.feature_wear_desc),
                        ),
                        Triple(
                            Icons.Default.Insights,
                            stringResource(id = R.string.feature_insights_title),
                            stringResource(id = R.string.feature_insights_desc),
                        ),
                    )

                features.forEach { (icon, title, description) ->
                    Card(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                        colors =
                            CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            ),
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp),
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                )
                                Text(
                                    text = description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 使用步驟預覽
                Text(
                    text = stringResource(id = R.string.onboarding_get_started_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )

                Spacer(modifier = Modifier.height(12.dp))

                val steps =
                    listOf(
                        stringResource(id = R.string.onboarding_step_get_api_key),
                        stringResource(id = R.string.onboarding_step_setup_gpm_recommend),
                        stringResource(id = R.string.onboarding_step_start_tracking),
                    )

                steps.forEach { step ->
                    Row(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                    ) {
                        Text(
                            text = step,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // 操作按鈕
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    TextButton(
                        onClick = onSkip,
                        modifier = Modifier.weight(1f),
                    ) {
                        Text(stringResource(id = R.string.action_skip_tour))
                    }

                    Button(
                        onClick = onNext,
                        modifier = Modifier.weight(1f),
                    ) {
                        Text(stringResource(id = R.string.action_start_setup))
                    }
                }
            }
            }
        }
    }
}
