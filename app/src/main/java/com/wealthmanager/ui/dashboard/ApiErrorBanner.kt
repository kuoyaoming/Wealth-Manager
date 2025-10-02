package com.wealthmanager.ui.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import com.wealthmanager.R
import com.wealthmanager.haptic.HapticFeedbackManager
import com.wealthmanager.haptic.rememberHapticFeedbackWithView

@Composable
fun ApiErrorBanner(
    errorMessage: String,
    isRetrying: Boolean,
    isDataStale: Boolean,
    onRetry: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (hapticManager, view) = rememberHapticFeedbackWithView()
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = stringResource(R.string.cd_warning),
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isDataStale) stringResource(R.string.api_error_data_stale) else stringResource(R.string.api_error_connection_issue),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )

                Text(
                    text = if (isDataStale) {
                        stringResource(R.string.api_error_show_cached_data)
                    } else {
                        errorMessage
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }

            if (isRetrying) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.error
                )
            } else {
                Row {
                    TextButton(
                        onClick = {
                            hapticManager.triggerHaptic(view, HapticFeedbackManager.HapticIntensity.MEDIUM)
                            onRetry()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = stringResource(R.string.cd_retry),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(stringResource(R.string.api_error_retry))
                    }

                    IconButton(onClick = {
                        hapticManager.triggerHaptic(view, HapticFeedbackManager.HapticIntensity.LIGHT)
                        onDismiss()
                    }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(R.string.cd_dismiss),
                            tint = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }
        }
    }
}
