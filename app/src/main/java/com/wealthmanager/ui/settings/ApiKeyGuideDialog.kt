package com.wealthmanager.ui.settings

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.wealthmanager.R

/**
 * API金鑰申請引導對話框
 */
@Composable
fun ApiKeyGuideDialog(onDismiss: () -> Unit) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // 檢測是否可以向下滾動（用於顯示滾動提示）
    val canScrollDown = scrollState.value < scrollState.maxValue
    val canScrollUp = scrollState.value > 0

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
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
                            .padding(24.dp)
                            .verticalScroll(scrollState),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Key,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp),
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = stringResource(R.string.api_guide_title),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = stringResource(R.string.api_guide_description),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    ApiKeyGuideCard(
                        title = "Finnhub API",
                        description = stringResource(R.string.api_guide_finnhub_description),
                        features =
                            listOf(
                                stringResource(R.string.api_guide_finnhub_features).split("\n"),
                            ).flatten(),
                        freeLimit = "60 calls/minute",
                        onApplyClick = {
                            openBrowser(context, "https://finnhub.io/register")
                        },
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    ApiKeyGuideCard(
                        title = "Exchange Rate API",
                        description = stringResource(R.string.api_guide_exchange_description),
                        features =
                            listOf(
                                stringResource(R.string.api_guide_exchange_features).split("\n"),
                            ).flatten(),
                        freeLimit = "1,500 requests/month",
                        onApplyClick = {
                            openBrowser(context, "https://v6.exchangerate-api.com/")
                        },
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = stringResource(R.string.api_guide_steps_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    val steps = stringResource(R.string.api_guide_steps).split("\n")

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

                    Spacer(modifier = Modifier.height(20.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors =
                            CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer,
                            ),
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(20.dp),
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = stringResource(R.string.api_guide_important_reminder),
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.error,
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = stringResource(R.string.api_guide_reminder_points),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                    ) {
                        Button(
                            onClick = onDismiss,
                            colors =
                                ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                ),
                        ) {
                            Text(stringResource(R.string.dialog_i_understand))
                        }
                    }
                } // End of Column (scrollable content)

                // 頂部漸變遮罩 - 提示可以向上滾動
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

                // 底部漸變遮罩 + 向下箭頭 - 提示可以向下滾動
                if (canScrollDown) {
                    Column(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .align(Alignment.BottomCenter),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        // 漸變遮罩
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

                        // 向下箭頭指示器
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
            } // End of Box
        } // End of Card
    } // End of Dialog
}

@Composable
private fun ApiKeyGuideCard(
    title: String,
    description: String,
    features: List<String>,
    freeLimit: String,
    onApplyClick: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
            ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = stringResource(R.string.api_guide_main_features),
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
            )

            Spacer(modifier = Modifier.height(4.dp))

            features.forEach { feature ->
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                ) {
                    Text(
                        text = "• ",
                        style = MaterialTheme.typography.bodySmall,
                    )
                    Text(
                        text = feature,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.api_guide_free_quota, freeLimit),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium,
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onApplyClick,
                modifier = Modifier.fillMaxWidth(),
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                    ),
            ) {
                Icon(
                    imageVector = Icons.Default.OpenInNew,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.dialog_go_apply))
            }
        }
    }
}

/**
 * 開啟瀏覽器
 */
private fun openBrowser(
    context: Context,
    url: String,
) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(intent)
    } catch (e: Exception) {
    }
}
