package com.wealthmanager.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.wealthmanager.R

/**
 * API Key validation error feedback component.
 */
@Composable
fun ApiKeyValidationFeedback(
    errorMessage: String?,
    keyType: String,
    onRetry: () -> Unit,
    onViewGuide: () -> Unit,
    onClearError: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    if (errorMessage != null) {
        Card(
            modifier = modifier.fillMaxWidth(),
            colors =
                CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f),
                ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        ) {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.Error,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(20.dp),
                    )
                    Text(
                        text = stringResource(id = R.string.api_key_validation_failed_title, keyType),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                    )
                }

                Text(
                    text = errorMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                )

                val suggestions = getValidationSuggestions(errorMessage, keyType)
                if (suggestions.isNotEmpty()) {
                    Card(
                        colors =
                            CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            ),
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                        ) {
                            Text(
                                text = stringResource(id = R.string.suggestions_title),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            suggestions.forEach { suggestion ->
                                Row(
                                    modifier =
                                        Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Lightbulb,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(16.dp),
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = suggestion,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Button(
                        onClick = onRetry,
                        modifier = Modifier.weight(1f),
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(stringResource(id = R.string.action_retry_validation))
                    }

                    OutlinedButton(
                        onClick = onViewGuide,
                        modifier = Modifier.weight(1f),
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Help,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(stringResource(id = R.string.action_view_guide))
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(
                        onClick = onClearError,
                    ) {
                        Text(stringResource(id = R.string.action_close))
                    }
                }
            }
        }
    }
}

/**
 * Provide correction suggestions based on error message and API type.
 */
@Composable
private fun getValidationSuggestions(
    errorMessage: String,
    keyType: String,
): List<String> {
    val context = LocalContext.current
    val suggestions = mutableListOf<String>()

    when {
        errorMessage.contains("validation failed", ignoreCase = true) -> {
            suggestions.add(context.getString(R.string.api_validation_check_format))
            suggestions.add(context.getString(R.string.api_validation_check_length))
            suggestions.add(context.getString(R.string.api_validation_avoid_weak_patterns))
        }

        errorMessage.contains("invalid", ignoreCase = true) -> {
            when (keyType.lowercase()) {
                "finnhub" -> {
                    suggestions.add(context.getString(R.string.api_validation_finnhub_check_status))
                    suggestions.add(context.getString(R.string.api_validation_finnhub_check_enabled))
                    suggestions.add(context.getString(R.string.api_validation_finnhub_check_limits))
                }
                "exchange" -> {
                    suggestions.add(context.getString(R.string.api_validation_exchange_check_status))
                    suggestions.add(context.getString(R.string.api_validation_exchange_check_valid))
                    suggestions.add(context.getString(R.string.api_validation_exchange_check_limits))
                }
            }
        }

        errorMessage.contains("network", ignoreCase = true) ||
            errorMessage.contains("connection", ignoreCase = true) -> {
            suggestions.add(context.getString(R.string.api_validation_check_network))
            suggestions.add(context.getString(R.string.api_validation_try_other_network))
            suggestions.add(context.getString(R.string.api_validation_try_later))
        }

        errorMessage.contains("rate limit", ignoreCase = true) -> {
            suggestions.add(context.getString(R.string.api_validation_wait_minutes))
            suggestions.add(context.getString(R.string.api_validation_check_quota))
            suggestions.add(context.getString(R.string.api_validation_consider_upgrade))
        }

        errorMessage.contains("authentication", ignoreCase = true) -> {
            suggestions.add(context.getString(R.string.api_validation_recopy_key))
            suggestions.add(context.getString(R.string.api_validation_check_spaces))
            suggestions.add(context.getString(R.string.api_validation_confirm_enabled))
        }

        else -> {
            suggestions.add(context.getString(R.string.api_validation_check_copied))
            suggestions.add(context.getString(R.string.api_validation_confirm_network))
            suggestions.add(context.getString(R.string.api_validation_check_provider_status))
        }
    }

    return suggestions
}
