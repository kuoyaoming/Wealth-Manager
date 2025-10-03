package com.wealthmanager.ui.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
 * API Key 指南導覽對話框
 * 引導用戶如何取得和設定 API 金鑰
 */
@Composable
fun ApiKeyGuideOnboardingDialog(
    onNext: () -> Unit,
    onGoToSettings: () -> Unit,
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
                        imageVector = Icons.Default.Key,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp),
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = stringResource(id = R.string.onboarding_api_key_setup_title),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 主要說明
                Text(
                    text = stringResource(id = R.string.onboarding_api_key_setup_desc),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 為什麼需要 API Key
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors =
                        CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                        ),
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                    ) {
                        Text(
                            text = stringResource(id = R.string.onboarding_api_key_why_title),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = stringResource(id = R.string.onboarding_api_key_why_desc),
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 取得步驟
                Text(
                    text = stringResource(id = R.string.onboarding_api_key_steps_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )

                Spacer(modifier = Modifier.height(12.dp))

                val steps =
                    listOf(
                        Triple(
                            "1",
                            stringResource(id = R.string.onboarding_api_key_step_title_1),
                            stringResource(id = R.string.onboarding_api_key_step_desc_1),
                        ),
                        Triple(
                            "2",
                            stringResource(id = R.string.onboarding_api_key_step_title_2),
                            stringResource(id = R.string.onboarding_api_key_step_desc_2),
                        ),
                        Triple(
                            "3",
                            stringResource(id = R.string.onboarding_api_key_step_title_3),
                            stringResource(id = R.string.onboarding_api_key_step_desc_3),
                        ),
                        Triple(
                            "4",
                            stringResource(id = R.string.onboarding_api_key_step_title_4),
                            stringResource(id = R.string.onboarding_api_key_step_desc_4),
                        ),
                    )

                steps.forEach { (number, title, description) ->
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
                            Surface(
                                modifier = Modifier.size(32.dp),
                                shape = MaterialTheme.shapes.small,
                                color = MaterialTheme.colorScheme.primary,
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(
                                        text = number,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimary,
                                    )
                                }
                            }
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

                // 安全提示
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors =
                        CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f),
                        ),
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(20.dp),
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(id = R.string.onboarding_api_key_security_tip),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // 操作按鈕
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Button(
                        onClick = onGoToSettings,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(id = R.string.action_go_to_set_api_key))
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        TextButton(
                            onClick = onSkip,
                            modifier = Modifier.weight(1f),
                        ) {
                            Text(stringResource(id = R.string.action_setup_later))
                        }

                        TextButton(
                            onClick = onNext,
                            modifier = Modifier.weight(1f),
                        ) {
                            Text(stringResource(id = R.string.action_continue_tour))
                        }
                    }
                }
            }
            }
        }
    }
}
